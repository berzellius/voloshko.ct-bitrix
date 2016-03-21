package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIListRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIPostMessageInLiveFeedFunctionRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIEntityCreatedResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPILeadListResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 20.03.2016.
 */
public class BitrixAPICRMPostMessageInLiveFeedFunction extends BitrixAPIFunction {
    protected final String name = "crm.livefeedmessage.add";
    protected final Class<? extends BitrixAPIResponse> responseClass = BitrixAPIEntityCreatedResponse.class;
    protected BitrixAPIPostMessageInLiveFeedFunctionRequest request;
    protected BitrixAPIEntityCreatedResponse response;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends BitrixAPIResponse> getResponseClass() {
        return responseClass;
    }

    @Override
    public BitrixAPIPostMessageInLiveFeedFunctionRequest getRequest() {
        return request;
    }

    public void setRequest(BitrixAPIPostMessageInLiveFeedFunctionRequest request) {
        this.request = request;
    }

    @Override
    public BitrixAPIEntityCreatedResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPIEntityCreatedResponse response) {
        this.response = response;
    }
}
