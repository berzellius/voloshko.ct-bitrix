package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIListRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPILeadListResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 13.03.2016.
 */
public class BitrixAPICRMLeadListFunction extends BitrixAPIFunction {

    protected final String name = "crm.lead.list";
    protected final Class<? extends BitrixAPIResponse> responseClass = BitrixAPILeadListResponse.class;
    protected BitrixAPIListRequest request;
    protected BitrixAPILeadListResponse response;

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
    public BitrixAPILeadListResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPILeadListResponse response) {
        this.response = response;
    }
}
