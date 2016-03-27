package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIListRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIContactListResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPILeadListResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 13.03.2016.
 */
public class BitrixAPICRMContactListFunction extends BitrixAPIFunction {

    protected final String name = "crm.contact.list";
    protected final Class<? extends BitrixAPIResponse> responseClass = BitrixAPIContactListResponse.class;
    protected BitrixAPIListRequest request;
    protected BitrixAPIContactListResponse response;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends BitrixAPIResponse> getResponseClass() {
        return responseClass;
    }

    @Override
    public BitrixAPIListRequest getRequest() {
        return request;
    }

    public void setRequest(BitrixAPIListRequest request) {
        this.request = request;
    }

    @Override
    public BitrixAPIContactListResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPIContactListResponse response) {
        this.response = response;
    }
}
