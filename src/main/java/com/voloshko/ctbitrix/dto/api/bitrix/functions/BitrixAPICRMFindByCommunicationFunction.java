package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIFindByCommunicationRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 20.03.2016.
 */
public class BitrixAPICRMFindByCommunicationFunction extends BitrixAPIFunction {
    protected final String name = "crm.duplicate.findbycomm";
    protected final Class<? extends BitrixAPIResponse> responseClass = BitrixAPIFindByCommunicationResponse.class;
    protected BitrixAPIFindByCommunicationRequest request;
    protected BitrixAPIFindByCommunicationResponse response;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends BitrixAPIResponse> getResponseClass() {
        return responseClass;
    }

    @Override
    public BitrixAPIFindByCommunicationRequest getRequest() {
        return request;
    }

    public void setRequest(BitrixAPIFindByCommunicationRequest request) {
        this.request = request;
    }

    @Override
    public BitrixAPIFindByCommunicationResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPIFindByCommunicationResponse response) {
        this.response = response;
    }
}
