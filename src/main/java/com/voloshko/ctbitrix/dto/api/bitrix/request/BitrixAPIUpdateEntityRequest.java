package com.voloshko.ctbitrix.dto.api.bitrix.request;

import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMEntity;

/**
 * Created by berz on 18.03.2016.
 */
public class BitrixAPIUpdateEntityRequest extends BitrixAPIFunctionRequest {
    private BitrixCRMEntity fields;
    private String id;

    @Override
    public boolean correct() {
        if(this.getAuth() == null || this.getAuth().equals("")) {
            this.setIncorrectMessage("auth field is null");
            return false;
        }

        if(this.getFields() == null){
            this.setIncorrectMessage("crmEntity field is empty!");
        }

        return true;
    }

    public BitrixCRMEntity getFields() {
        return fields;
    }

    public void setFields(BitrixCRMEntity fields) {
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
