package com.voloshko.ctbitrix.dto.api.bitrix.request;

import com.voloshko.ctbitrix.dto.api.bitrix.params.ArrayEntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.params.EntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.params.SimpleEntityField;

import java.util.ArrayList;

/**
 * Created by berz on 20.03.2016.
 */
public class BitrixAPIFindByCommunicationRequest extends BitrixAPIFunctionRequest {

    public static BitrixAPIFindByCommunicationRequest getInstance(){
        return new BitrixAPIFindByCommunicationRequest();
    }

    public static enum Type{
        EMAIL,
        PHONE
    }

    public static enum EntityType{
        LEAD,
        CONTACT,
        COMPANY
    }

    private SimpleEntityField type;
    private ArrayEntityField values;
    private SimpleEntityField entity_type;

    public BitrixAPIFindByCommunicationRequest type(Type t){
        this.setType(new SimpleEntityField(t.toString()));
        return this;
    }

    public BitrixAPIFindByCommunicationRequest values(String... values){
        if(this.getValues() == null){
            this.setValues(new ArrayEntityField());
        }

        if(this.getValues().getValues() == null){
            this.getValues().setValues(new ArrayList<>());
        }

        for(String v : values){
            this.getValues().getValues().add(v);
        }

        return this;
    }

    public BitrixAPIFindByCommunicationRequest entityType(EntityType et){
        this.setEntity_type(new SimpleEntityField(et.toString()));
        return this;
    }

    @Override
    public boolean correct() {
        if(this.getAuth() == null || this.getAuth().equals("")) {
            this.setIncorrectMessage("auth field is null");
            return false;
        }

        if(this.getType() == null || this.getType().getValue() == null){
            this.setIncorrectMessage("'type' field requested!");
            return false;
        }

        if(Type.valueOf(this.getType().getValue()) == null){
            this.setIncorrectMessage(this.getType().getValue().concat(" value is incorrect for 'type' field"));
            return false;
        }

        if(this.getValues() == null || this.getValues().getValues() == null || this.getValues().getValues().size() == 0){
            this.setIncorrectMessage("At least one 'value' field needed");
        }

        if(
                this.getEntity_type() != null &&
                        this.getEntity_type().getValue() != null &&
                        EntityType.valueOf(this.getEntity_type().getValue()) == null
                ){
            this.setIncorrectMessage(this.getEntity_type().getValue().concat(" value is incorrect for 'entity_type' field"));
            return false;
        }

        return true;
    }

    public ArrayEntityField getValues() {
        return values;
    }

    public void setValues(ArrayEntityField values) {
        this.values = values;
    }

    public SimpleEntityField getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(SimpleEntityField entity_type) {
        this.entity_type = entity_type;
    }

    public SimpleEntityField getType() {
        return type;
    }

    public void setType(SimpleEntityField type) {
        this.type = type;
    }
}
