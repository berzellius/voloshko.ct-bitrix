package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.Call;
import com.voloshko.ctbitrix.dmodel.CallTrackingSourceCondition;
import com.voloshko.ctbitrix.dmodel.LeadFromSite;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMContact;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMDeal;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLead;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLiveFeedMessage;
import com.voloshko.ctbitrix.dto.api.bitrix.params.MultiValueEntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIFindByCommunicationRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIListRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import com.voloshko.ctbitrix.repository.CallRepository;
import com.voloshko.ctbitrix.repository.LeadFromSiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by berz on 21.03.2016.
 */
@Service
@Transactional
public class CtBitrixBusinessLogicServiceImpl implements CtBitrixBusinessLogicService {

    private static final Logger log = LoggerFactory.getLogger(CtBitrixBusinessLogicServiceImpl.class);

    @Autowired
    BitrixAPIService bitrixAPIService;

    @Autowired
    CallRepository callRepository;

    @Autowired
    CallTrackingAPIService callTrackingAPIService;

    @Autowired
    CallTrackingSourceConditionService callTrackingSourceConditionService;

    @Autowired
    LeadFromSiteRepository leadFromSiteRepository;

    @Override
    public void processCall(Call call) throws APIAuthException {

        if(call.getState().equals(Call.State.DONE)){
            throw new IllegalArgumentException("Call must not be in 'DONE' state!");
        }

        String phone = phoneExec(call.getNumber());

        // Ищем сущности Bitrix CRM по телефону
        BitrixAPIFindByCommunicationResponse.Result findByPhone = communicationByPhone(phone);

        Long leadID = null;
        BitrixCRMContact crmContact = null;
        ArrayList<BitrixCRMDeal> deals = new ArrayList<>();

        if(findByPhone.getContact() != null && findByPhone.getContact().size() > 0){
            Long contactID = findByPhone.getContact().get(0);
            crmContact = bitrixAPIService.getContactByID(contactID);
            if(crmContact.getMarketingChannel() == null || crmContact.getMarketingChannel().equals("")){
                crmContact.setMarketingChannel(call.getSource());
                crmContact.setChanged(true);
            }
            log.info("found contact by 'findByCommunication' function: contact#".concat(contactID.toString()));
        }

        if(findByPhone.getLead() != null && findByPhone.getLead().size() > 0){
            leadID = findByPhone.getLead().get(0);
            log.info("found lead by 'findByCommunication' function: lead#".concat(leadID.toString()));
        }
        else if(crmContact != null){

            if(crmContact.getLead_id() != null){
                leadID = crmContact.getLead_id();
                log.info("found lead from contact#".concat(crmContact.getId()).concat(": lead#").concat(leadID.toString()));
            }
        }

        if(leadID != null) {
            log.info("work with lead#".concat(leadID.toString()));
            BitrixCRMLead lead = bitrixAPIService.getLeadByID(leadID);
            if(lead.getMarketingChannel() == null || lead.getMarketingChannel().equals("")){
                lead.setMarketingChannel(call.getSource());
                lead.setChanged(true);
            }

            Long liveFeedMsgID = bitrixAPIService.postMessageInLiveFeed(
                    "Повторный звонок с номера ".concat(call.getNumber()),
                    "Лид по такому звонку уже существует",
                    BitrixCRMLiveFeedMessage.EntityType.LEAD,
                    leadID
            );
        }
        else{
            log.info("need to create new lead");
            BitrixCRMLead bitrixCRMLeadToCreate = (BitrixCRMLead) BitrixCRMLead.newInstance()
                .marketingChannel(call.getSource())
                .title("Автоматически - Calltracking [через API] (".concat(call.getNumber()).concat(")"))
                        // == Алена Волошко
                .assignedByID(1l)
                        // == Интернет-реклама
                .sourceID("84996537185")
                .phones(
                        MultiValueEntityField.arrayList(
                                MultiValueEntityField.newInstance(null, "WORK", "7" .concat(call.getNumber()), null)
                        )
                );

            if(bitrixCRMLeadToCreate.getContact_id() == null && crmContact != null){
                bitrixCRMLeadToCreate.contactID(Long.decode(crmContact.getId()));
            }



            leadID = bitrixAPIService.createBitrixCRMEntity(
                    bitrixCRMLeadToCreate
            );

            if(leadID == null){
                throw new IllegalStateException("Unable to create lead!");
            }

            log.info("Created lead#".concat(leadID.toString()));

            Long liveFeedMsgID = bitrixAPIService.postMessageInLiveFeed(
                    "Автоматически создан новый лид из Calltracking",
                    "Добавлен новый лид из Calltracking.",
                    BitrixCRMLiveFeedMessage.EntityType.LEAD,
                    leadID
            );

            log.info("Created liveFeedMsg#".concat(liveFeedMsgID.toString()));
        }

        if(leadID != null){
            log.info("searching deal by lead#".concat(leadID.toString()));
            ArrayList<BitrixCRMDeal> dealsByLead = bitrixAPIService.getDealsByRequest(
                    BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("LEAD_ID", leadID)
            );
            if(dealsByLead != null) {
                deals.addAll(dealsByLead);
            }
        }

        if(crmContact != null){
            log.info("searching deal by contact#".concat(crmContact.getId()));
            ArrayList<BitrixCRMDeal> dealsByContacts = bitrixAPIService.getDealsByRequest(
                    BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("CONTACT_ID", Long.decode(crmContact.getId()))
            );
            if(dealsByContacts != null) {
                deals.addAll(dealsByContacts);
            }

        }

        this.marketingChannelToDeals(deals, call.getSource());

        bitrixAPIService.flush();

        call.setState(Call.State.DONE);
        callRepository.save(call);
    }

    @Override
    public void processCall1(Call call) throws APIAuthException {

        try {
            Thread.sleep(3000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }


        if(call.getState().equals(Call.State.DONE)){
            throw new IllegalArgumentException("Call must not be in 'DONE' state!");
        }

        String phone = phoneExec(call.getNumber());


        List<BitrixAPIFindByCommunicationResponse.Result> finds = new ArrayList<>();
        // Ищем сущности Bitrix CRM по телефону
        finds.add(bitrixAPIService.findByCommunication(
                BitrixAPIFindByCommunicationRequest.getInstance()
                        .values(phone)
                        .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
        ));

        finds.add(bitrixAPIService.findByCommunication(
                BitrixAPIFindByCommunicationRequest.getInstance()
                        .values("8".concat(phone))
                        .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
        ));

        finds.add(bitrixAPIService.findByCommunication(
                BitrixAPIFindByCommunicationRequest.getInstance()
                        .values("7".concat(phone))
                        .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
        ));

        finds.add(bitrixAPIService.findByCommunication(
                BitrixAPIFindByCommunicationRequest.getInstance()
                        .values("+7".concat(phone))
                        .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
        ));

        ArrayList<BitrixCRMDeal> deals = new ArrayList<>();

        for(BitrixAPIFindByCommunicationResponse.Result find : finds){
            if(find.getLead() != null) {
                for (Long leadID : find.getLead()) {
                    ArrayList<BitrixCRMDeal> dealsByLead = bitrixAPIService.getDealsByRequest(
                            BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("LEAD_ID", leadID)
                    );
                    if (dealsByLead != null) {
                        deals.addAll(dealsByLead);
                    }
                }
            }

            if(find.getContact() != null) {
                for (Long contactID : find.getContact()) {
                    ArrayList<BitrixCRMDeal> dealsByContacts = bitrixAPIService.getDealsByRequest(
                            BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("CONTACT_ID", contactID)
                    );
                    if (dealsByContacts != null) {
                        deals.addAll(dealsByContacts);
                    }
                }
            }
        }

        this.marketingChannelToDeals(deals, call.getSource());

        bitrixAPIService.flush();

        call.setState(Call.State.DONE);
        callRepository.save(call);
    }

    private BitrixAPIFindByCommunicationResponse.Result communicationByPhone(String phone) throws APIAuthException {
        BitrixAPIFindByCommunicationResponse.Result res = findCommunicationByPhone(phone);
        if(res != null){
            return res;
        }

        BitrixAPIFindByCommunicationResponse.Result res1 = findCommunicationByPhone("7".concat(phone));
        if(res1 != null){
            return res1;
        }

        BitrixAPIFindByCommunicationResponse.Result res2 = findCommunicationByPhone("+7".concat(phone));
        if(res2 != null){
            return res2;
        }

        BitrixAPIFindByCommunicationResponse.Result res3 = findCommunicationByPhone("8".concat(phone));
        if(res3 != null){
            return res3;
        }

        return bitrixAPIService.findByCommunication(
                BitrixAPIFindByCommunicationRequest.getInstance()
                        .values(phone)
                        .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
        );
    }

    private BitrixAPIFindByCommunicationResponse.Result findCommunicationByPhone(String phone) throws APIAuthException {
        // Ищем сущности Bitrix CRM по телефону
        BitrixAPIFindByCommunicationResponse.Result findByPhone =
                bitrixAPIService.findByCommunication(
                        BitrixAPIFindByCommunicationRequest.getInstance()
                                .values(phone)
                                .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
                );

        if(
                (findByPhone.getContact() != null && findByPhone.getContact().size() > 0) ||
                        (findByPhone.getLead() != null && findByPhone.getLead().size() > 0)
                ){
            return findByPhone;
        }
        else return null;
    }

    public LeadFromSite processLeadFromSite(LeadFromSite leadFromSite) throws APIAuthException {
        log.info("start processing lead from site");

        if(leadFromSite.getState().equals(LeadFromSite.State.DONE)){
            throw new IllegalArgumentException("LeadFromSite must not be in 'DONE' state!");
        }

        if(leadFromSite.getLead() == null){
            throw new IllegalArgumentException("leadFromSite.lead is empty!");
        }

        if(leadFromSite.getLead().getOrigin() == null){
            throw new IllegalArgumentException("leadFromSite.origin is empty!");
        }

        String phone = (leadFromSite.getLead().getPhone() != null)? phoneExec(leadFromSite.getLead().getPhone()) : null;
        String email = (leadFromSite.getLead().getEmail() != null)? leadFromSite.getLead().getEmail() : null;

        log.info("phone " + ((phone != null)? phone : " is null"));
        log.info("email " + ((email != null)? email : " is null"));

        Integer projectId = callTrackingAPIService.getProjectIdBySite(leadFromSite.getLead().getOrigin());

        if(projectId == null){
            log.error("failed to determine project id (Calltracking)");
            throw new IllegalArgumentException("projectId was not detected!");
        }

        CallTrackingSourceCondition callTrackingSourceCondition = callTrackingSourceConditionService.getCallTrackingSourceConditionByUtmAndProjectId(
                leadFromSite.getLead().getUtm_source(),
                leadFromSite.getLead().getUtm_medium(),
                leadFromSite.getLead().getUtm_campaign(),
                projectId
        );

        String source = callTrackingSourceCondition.getSourceName();
        if(source == null){
            log.error("failed to determine callTrackingCondition!");
            //throw new IllegalStateException("Unable to detect source!");
            source = "N/A";
        }

        ArrayList<Long> contacts = new ArrayList<>();
        ArrayList<Long> leads = new ArrayList<>();
        ArrayList<BitrixCRMDeal> deals = new ArrayList<>();

        if(phone != null) {
            log.info("phone is not null, so searching communications by phone");
            // Получаем сущности по телефону
            BitrixAPIFindByCommunicationResponse.Result result = bitrixAPIService.findByCommunication(
                    BitrixAPIFindByCommunicationRequest.getInstance()
                            .values(phone)
                            .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
            );

            if(result.getContact() != null && result.getContact().size() > 0) {
                log.info(result.getContact().size() + "contacts found by phone");
                contacts.addAll(result.getContact());
            }

            if(result.getLead() != null && result.getLead().size() > 0) {
                log.info(result.getLead().size() + "leads found by phone");
                leads.addAll(result.getLead());
            }
        }

        if(email != null){
            log.info("email is not null, so searching communications by email");
            // Получаем сущности по email
            BitrixAPIFindByCommunicationResponse.Result result = bitrixAPIService.findByCommunication(
                    BitrixAPIFindByCommunicationRequest.getInstance()
                            .values(email)
                            .type(BitrixAPIFindByCommunicationRequest.Type.EMAIL)
            );

            if(result.getContact() != null && result.getContact().size() > 0) {
                log.info(result.getContact().size() + "contacts found by email");
                contacts.addAll(result.getContact());
            }

            if(result.getLead() != null && result.getLead().size() > 0) {
                log.info(result.getLead().size() + "leads found by email");
                leads.addAll(result.getLead());
            }
        }

        BitrixCRMContact equalContact = null;
        // Проходимся по полученным Контактам и ищем наиболее подходящий по почте/телефону, он будет equalsContact
        for(Long contactID : contacts){
            log.info("working with contact#" + contactID);
            BitrixCRMContact crmContact = bitrixAPIService.getContactByID(contactID);

            equalContact = crmContact;

            Boolean phoneCond = MultiValueEntityField.containsValue(crmContact.getPhone(), phone);
            Boolean emailCond = MultiValueEntityField.containsValue(crmContact.getEmail(), email);

            if(phoneCond && emailCond){
                equalContact = crmContact;

                log.info("contact#" + contactID + " is equal Contact by email and phone!");
                equalContact = crmContact;
            }

            ArrayList<BitrixCRMDeal> crmDeals = bitrixAPIService.getDealsByRequest(
                    BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("CONTACT_ID", contactID)
            );

            if(crmDeals != null && crmDeals.size() > 0){
                log.info(crmDeals.size() + " deals found by contact#" + contactID);
                deals.addAll(crmDeals);
            }
            else{
                log.info("no deals found by contact#" + contactID);
            }
        }

        if(equalContact != null){
            if(equalContact.getMarketingChannel() == null || equalContact.getMarketingChannel().equals("")){
                equalContact.setMarketingChannel(source);
                equalContact.setChanged(true);
            }

            if(phone != null && !MultiValueEntityField.containsValue(equalContact.getPhone(), phone)){
                equalContact.addPhone(phone, "WORK");
                equalContact.setChanged(true);
            }

            if(email != null && !MultiValueEntityField.containsValue(equalContact.getEmail(), email)){
               equalContact.addEmail(email, "WORK");
                equalContact.setChanged(true);
            }
        }

        // Проходимся по полученным Лидам и заполняем поле Рекламный канал там, где оно не заполнено
        for(Long leadID : leads){
            log.info("working with lead#" + leadID);

            ArrayList<BitrixCRMDeal> crmDeals = bitrixAPIService.getDealsByRequest(
                    BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("LEAD_ID", leadID)
            );

            if(crmDeals != null && crmDeals.size() > 0){
                log.info(crmDeals.size() + " deals found by lead#" + leadID);
                deals.addAll(crmDeals);
            }
            else{
                log.info("no deals found by lead#" + leadID);
            }
        }

        this.marketingChannelToDeals(deals, source);

        log.info("Need to create lead!");

        BitrixCRMLead bitrixCRMLead = (BitrixCRMLead) BitrixCRMLead.newInstance()
                .marketingChannel(source)
                .title("Автоматически - Заявка с сайта [через API]: " +
                        ((leadFromSite.getLead().getName() != null) ? leadFromSite.getLead().getName() : null))
                        // == Алена Волошко
                .assignedByID(1l)
                        // == Интернет-реклама
                .sourceID("84996537185");

        if (phone != null) {
            bitrixCRMLead.phones(MultiValueEntityField.arrayList(MultiValueEntityField.newInstance(
                    null, "WORK", "7".concat(phone), null
            )));
        }

        if(email != null){
            bitrixCRMLead.emails(MultiValueEntityField.arrayList(MultiValueEntityField.newInstance(
                    null, "WORK", email, null
            )));
        }

        if(leadFromSite.getLead().getReferer() != null){
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromOriginHeader(leadFromSite.getLead().getReferer());
            UriComponents uriComponents = uriComponentsBuilder.build().encode();
            if(uriComponents != null && uriComponents.getHost() != null && uriComponents.getPath() != null) {
                String site = uriComponents.getScheme().concat("://").concat(uriComponents.getHost()).concat(uriComponents.getPath());

                bitrixCRMLead.webs(MultiValueEntityField.arrayList(MultiValueEntityField.newInstance(
                        null, "WORK", site, "WEB"
                )));
            }
        }

        if(leadFromSite.getLead().getComment() != null){
            bitrixCRMLead.setComments(leadFromSite.getLead().getComment());
        }

        if(equalContact != null){
            bitrixCRMLead.setContact_id(Long.decode(equalContact.getId()));
        }

        Long createdLeadId = bitrixAPIService.createBitrixCRMEntity(
                bitrixCRMLead
        );

        if(createdLeadId == null){
            log.error("failed creating lead!");
            throw new IllegalStateException("Unable to create lead!");
        }

        log.info("created lead#" + createdLeadId);

        Long liveFeedMsgID = bitrixAPIService.postMessageInLiveFeed(
                "Автоматически создан лид по заявке с сайта",
                "Добавлен лид по заявке с сайта. " +
                        ((equalContact != null)? " По указанному email/телефону найден контакт. " : "" ) +
                        (leadFromSite.getLead().getComment() != null? leadFromSite.getLead().getComment() : ""),
                BitrixCRMLiveFeedMessage.EntityType.LEAD,
                createdLeadId
        );

        log.info("Processing lead from site is over.");

        leadFromSite.setState(LeadFromSite.State.DONE);

        bitrixAPIService.flush();

        leadFromSiteRepository.save(leadFromSite);
        return leadFromSite;
    }

    private void marketingChannelToEntities(){

    }

    private void marketingChannelToDeals(ArrayList<BitrixCRMDeal> deals, String marketingChannel) throws APIAuthException {

        for(BitrixCRMDeal deal : deals){
            log.info("working with deal#" + deal.getId());
            if(deal.getMarketingChannel() == null || deal.getMarketingChannel().equals("")){
                log.info("empty marketing channel. updating");
                deal.setMarketingChannel(marketingChannel);
                deal.setChanged(true);
            }
        }
    }

    private String phoneExec(String phone){
        log.info("parsing phone: ".concat(phone));

        if(phone.matches("^[\\d]+$")){
            if(phone.length() >= 11 &&
                    (
                            phone.charAt(0) == '7' ||
                                    phone.charAt(0) == '8'
                        )
                    ){
                return phone.substring(1);
            }
            return phone;
        }
        else{
            String parsed = "";
            for (Integer i = 0; i < phone.length(); i++){
                String ch = String.valueOf(phone.charAt(i));
                if(ch.matches("\\d")){
                    parsed = parsed.concat(ch);
                }
            }

            return phoneExec(parsed);
        }
    }
}
