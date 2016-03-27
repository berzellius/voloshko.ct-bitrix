package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMContact;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMDeal;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMEntity;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLead;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIEntityCreatedResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 18.03.2016.
 */
public class BitrixAPICRMUpdateEntityFunction extends BitrixAPIFunction {
    protected final Class<? extends BitrixAPIResponse> responseClass = BitrixAPIEntityCreatedResponse.class;
    protected BitrixAPIEntityCreatedResponse response;

    public static String getFunctionNameByEntity(BitrixCRMEntity bitrixCRMEntity){
        if(bitrixCRMEntity instanceof BitrixCRMLead){
            return "crm.lead.update";
        }

        if(bitrixCRMEntity instanceof BitrixCRMDeal){
            return "crm.deal.update";
        }

        if(bitrixCRMEntity instanceof BitrixCRMContact){
            return "crm.contact.update";
        }

        return null;
    }

    @Override
    public Class<? extends BitrixAPIResponse> getResponseClass() {
        return responseClass;
    }

    @Override
    public BitrixAPIEntityCreatedResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPIEntityCreatedResponse response) {
        this.response = response;
    }
}
