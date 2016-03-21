package com.voloshko.ctbitrix.dto.api.bitrix.request;

import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLiveFeedMessage;

/**
 * Created by berz on 20.03.2016.
 */
public class BitrixAPIPostMessageInLiveFeedFunctionRequest extends BitrixAPIFunctionRequest {
    private BitrixCRMLiveFeedMessage fields;

    public BitrixCRMLiveFeedMessage getFields() {
        return fields;
    }

    public void setFields(BitrixCRMLiveFeedMessage fields) {
        this.fields = fields;
    }


    @Override
    public boolean correct() {
        if(this.getFields() == null) {
            this.setIncorrectMessage("Empty request fields");
            return false;
        }

        if(this.getFields().getPost_title() == null || this.getFields().getPost_title().equals("")){
            this.setIncorrectMessage("Empty post title");
            return false;
        }

        if(this.getFields().getMessage() == null || this.getFields().getMessage().equals("")){
            this.setIncorrectMessage("Empty message");
            return false;
        }

        if(this.getFields().getEntitytypeid() == null){
            this.setIncorrectMessage("Entity type is not set");
            return false;
        }

        if(this.getFields().getEntityid() == null){
            this.setIncorrectMessage("Entity id is not set");
            return false;
        }

        return true;
    }
}
