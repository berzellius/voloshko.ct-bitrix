package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIListRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIContactListResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIDealListResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 13.03.2016.
 */
public class BitrixAPICRMDealListFunction extends BitrixAPIFunction {

    protected final String name = "crm.deal.list";
    protected final Class<? extends BitrixAPIResponse> responseClass = BitrixAPIDealListResponse.class;
    protected BitrixAPIListRequest request;
    protected BitrixAPIDealListResponse response;

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
    public BitrixAPIDealListResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPIDealListResponse response) {
        this.response = response;
    }
}
