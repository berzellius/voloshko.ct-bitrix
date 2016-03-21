package com.voloshko.ctbitrix.dto.api.bitrix.functions;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIFunctionRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;

/**
 * Created by berz on 13.03.2016.
 */
public abstract class BitrixAPIFunction {

    protected String name;
    protected Class responseClass;
    protected BitrixAPIFunctionRequest request;
    protected BitrixAPIResponse response;

    public boolean correct(){
        if(this.getRequest() == null){
            return false;
        }

        return this.getRequest().correct();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getResponseClass() {
        return this.responseClass;
    }

    public void setResponseClass(Class responseClass) {
        this.responseClass = responseClass;
    }

    public BitrixAPIFunctionRequest getRequest() {
        return request;
    }

    public void setRequest(BitrixAPIFunctionRequest request) {
        this.request = request;
    }

    public BitrixAPIResponse getResponse() {
        return response;
    }

    public void setResponse(BitrixAPIResponse response) {
        this.response = response;
    }
}
