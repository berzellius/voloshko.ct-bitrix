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
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

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

        // Ищем сущности Bitrix CRM по телефону
        BitrixAPIFindByCommunicationResponse.Result findByPhone =
                bitrixAPIService.findByCommunication(
                        BitrixAPIFindByCommunicationRequest.getInstance()
                                .values(call.getNumber())
                                .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
                );

        if(findByPhone.getLead() != null && findByPhone.getLead().size() > 0){
            // Найдены лиды
            // Обрабатываем лиды
            // TODO нашли лид по номеру телефона - если Источник не заполнен, заполняем
            // TODO надо искать еще сделки по номеру тлф
        }
        else{
            BitrixCRMLead bitrixCRMLeadToCreate = (BitrixCRMLead) BitrixCRMLead.newInstance()
                .marketingChannel(call.getSource())
                .title("Автоматически - Calltracking [через API] (".concat(call.getNumber()).concat(")"))
                        // == Алена Волошко
                .assignedByID(1l)
                        // == Интернет-реклама
                .sourceID("84996537185")
                .phones(
                        MultiValueEntityField.arrayList(
                                MultiValueEntityField.newInstance(null, "WORK", call.getNumber(), null)
                        )
                );

            if(findByPhone.getContact() != null && findByPhone.getContact().size() > 0){
                bitrixCRMLeadToCreate.contactID(findByPhone.getContact().get(0));
            }

            Long createdLeadId = bitrixAPIService.createBitrixCRMEntity(
                    bitrixCRMLeadToCreate
            );

            if(createdLeadId == null){
                throw new IllegalStateException("Unable to create lead!");
            }

            log.info("Created lead#".concat(createdLeadId.toString()));

            Long liveFeedMsgID = bitrixAPIService.postMessageInLiveFeed(
                    "Автоматически создан новый лид из Calltracking",
                    "Добавлен новый лид из Calltracking.",
                    BitrixCRMLiveFeedMessage.EntityType.LEAD,
                    createdLeadId
            );

            log.info("Created liveFeedMsg#".concat(liveFeedMsgID.toString()));
        }

        call.setState(Call.State.DONE);
        callRepository.save(call);
    }

    @Override
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

        if(
                leadFromSite.getLead().getUtm_source() == null ||
                        leadFromSite.getLead().getUtm_medium() == null ||
                        leadFromSite.getLead().getUtm_campaign() == null
                ){
            log.error("failed to determine utm params!");
            throw new IllegalArgumentException("utm: source, medium, campaign must not be null! (may be empty strings)");
        }

        String phone = (leadFromSite.getLead().getPhone() != null)? leadFromSite.getLead().getPhone() : null;
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
            throw new IllegalStateException("Unable to detect source!");
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

        // Проходимся по полученным Контактам и заполняем поле Рекламный канал там, где оно не заполнено
        for(Long contactID : contacts){
            log.info("working with contact#" + contactID);
            BitrixCRMContact crmContact = bitrixAPIService.getContactByID(contactID);
            if(crmContact.getMarketingChannel() == null || crmContact.getMarketingChannel().equals("")){
                crmContact.setMarketingChannel(source);
            }

            bitrixAPIService.updateBitrixCRMEntity(crmContact);

            Boolean phoneCond =
                    (phone != null && MultiValueEntityField.containsValue(crmContact.getPhone(), phone)) ||
                            phone == null;

            Boolean emailCond =
                    (email != null && MultiValueEntityField.containsValue(crmContact.getEmail(), email)) ||
                            email == null;

            if(phoneCond && emailCond){
                log.info("contact#" + contactID + " is equal Contact by email/phone!");
                equalContact = crmContact;
            }

            ArrayList<BitrixCRMDeal> crmDeals = bitrixAPIService.getDealsByRequest(
                    BitrixAPIListRequest.newInstance().filterOne("CONTACT_ID", contactID)
            );

            if(crmDeals != null && crmDeals.size() > 0){
                log.info(crmDeals.size() + " deals found by contact#" + contactID);
                deals.addAll(crmDeals);
            }
            else{
                log.info("no deals found by contact#" + contactID);
            }
        }

        BitrixCRMLead equalLead = null;

        // Проходимся по полученным Лидам и заполняем поле Рекламный канал там, где оно не заполнено
        for(Long leadID : leads){
            log.info("working with lead#" + leadID);
            BitrixCRMLead crmLead = bitrixAPIService.getLeadByID(leadID);
            if(crmLead.getMarketingChannel() == null || crmLead.getMarketingChannel().equals("")){
                log.info("need to update marketing channel...");
                crmLead.setMarketingChannel(source);
                bitrixAPIService.updateBitrixCRMEntity(crmLead);
                log.info("updated!");
            }

            Boolean phoneCond =
                    (phone != null && MultiValueEntityField.containsValue(crmLead.getPhone(), phone)) ||
                            phone == null;

            Boolean emailCond =
                    (email != null && MultiValueEntityField.containsValue(crmLead.getEmail(), email)) ||
                            email == null;

            if(phoneCond && emailCond){
                log.info("lead#" + leadID + " is equal Lead by email/phone");
                equalLead = crmLead;
            }
            else{
                log.info("lead#" + leadID + " is NOT equal Lead by email/phone");
            }

            ArrayList<BitrixCRMDeal> crmDeals = bitrixAPIService.getDealsByRequest(
                    BitrixAPIListRequest.newInstance().filterOne("LEAD_ID", leadID)
            );

            if(crmDeals != null && crmDeals.size() > 0){
                log.info(crmDeals.size() + " deals found by lead#" + leadID);
                deals.addAll(crmDeals);
            }
            else{
                log.info("no deals found by lead#" + leadID);
            }
        }

        for(BitrixCRMDeal deal : deals){
            log.info("working with deal#" + deal.getId());
            if(deal.getMarketingChannel() == null || deal.getMarketingChannel().equals("")){
                log.info("empty marketing channel. updating");
                deal.setMarketingChannel(source);

                bitrixAPIService.updateBitrixCRMEntity(deal);
            }
        }

        if(equalLead == null){
            // Лид, подходящий как по email, так и по телефону, не найден.
            // его нужно создать
            log.info("Need to create lead!");

            BitrixCRMLead bitrixCRMLead = (BitrixCRMLead) BitrixCRMLead.newInstance()
                    .marketingChannel(source)
                    .title("Автоматически - Заявка с сайта [через API]: " +
                            ((leadFromSite.getLead().getName() != null)? leadFromSite.getLead().getName() : null))
                            // == Алена Волошко
                    .assignedByID(1l)
                            // == Интернет-реклама
                    .sourceID("84996537185");

            if (phone != null) {
                bitrixCRMLead.phones(MultiValueEntityField.arrayList(MultiValueEntityField.newInstance(
                        null, "WORK", phone, null
                )));
            }

            if(email != null){
                bitrixCRMLead.emails(MultiValueEntityField.arrayList(MultiValueEntityField.newInstance(
                        null, "WORK", email, null
                )));
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
                    "Автоматически создан новый лид по заявке с сайта",
                    "Добавлен новый лид из по завяке из сайта. " + (leadFromSite.getLead().getComment() != null? leadFromSite.getLead().getComment() : ""),
                    BitrixCRMLiveFeedMessage.EntityType.LEAD,
                    createdLeadId
            );

        }
        else{
            log.info("lead is already exist. Lead#" + equalLead.getId());

            Long liveFeedMsgID = bitrixAPIService.postMessageInLiveFeed(
                    "Повторная заявка с сайта по лиду",
                    "Поступила заявка с сайта, при этом лид уже сущствует. " + (leadFromSite.getLead().getComment() != null? leadFromSite.getLead().getComment() : ""),
                    BitrixCRMLiveFeedMessage.EntityType.LEAD,
                    Long.decode(equalLead.getId())
            );

        }

        log.info("Processing lead from site is over.");

        leadFromSite.setState(LeadFromSite.State.DONE);
        leadFromSiteRepository.save(leadFromSite);
        return leadFromSite;
    }
}
