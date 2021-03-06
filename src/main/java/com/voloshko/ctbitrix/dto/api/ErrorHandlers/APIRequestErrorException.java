package com.voloshko.ctbitrix.dto.api.ErrorHandlers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by berz on 22.10.2015.
 */
public class APIRequestErrorException extends RuntimeException {

    private Map<String, Object> params;

    public APIRequestErrorException(String msg){
        super(msg);
    }

    public APIRequestErrorException(Map<String, Object> properties){
        super("error in request: " + properties.toString());
        this.setParams(properties);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString(){
        return this.getParams().toString();
    }
}
