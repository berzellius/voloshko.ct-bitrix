package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.Call;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLead;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLiveFeedMessage;
import com.voloshko.ctbitrix.dto.api.bitrix.params.MultiValueEntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIFindByCommunicationRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import com.voloshko.ctbitrix.repository.CallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
        }
        else{
            BitrixCRMLead bitrixCRMLeadToCreate = BitrixCRMLead.newInstance()
                .title("Автоматически - Calltracking [через API] (".concat(call.getNumber()).concat(")"))
                        // == Алена Волошко
                .assignedByID(1l)
                        // == Интернет-реклама
                .sourceID("84996537185")
                .phones(
                        MultiValueEntityField.arrayList(
                                MultiValueEntityField.newInstance(null, "WORK", call.getNumber(), null)
                        )
                )
                .marketingChannel(call.getSource());

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

            call.setState(Call.State.DONE);
            callRepository.save(call);
        }
    }
}
