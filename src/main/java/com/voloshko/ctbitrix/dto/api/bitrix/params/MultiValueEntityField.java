package com.voloshko.ctbitrix.dto.api.bitrix.params;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiValueEntityField extends EntityField {

    @JsonProperty("ID")
    private String id;
    @JsonProperty("VALUE_TYPE")
    private String valueType;
    @JsonProperty("VALUE")
    private String value;
    @JsonProperty("TYPE_ID")
    private String typeId;

    public static ArrayList<MultiValueEntityField> arrayWithOneInstance(String id, String valueType, String value, String typeID){
        ArrayList<MultiValueEntityField> multiValueEntityFields = new ArrayList<>();
        multiValueEntityFields.add(newInstance(id, valueType, value, typeID));
        return multiValueEntityFields;
    }

    public static ArrayList<MultiValueEntityField> arrayList(MultiValueEntityField... multiValueEntityFields){
        return new ArrayList<MultiValueEntityField>(Arrays.asList(multiValueEntityFields));
    }

    public static MultiValueEntityField newInstance(String id, String valueType, String value, String typeID){
        return new MultiValueEntityField(id, valueType, value, typeID);
    }

    public MultiValueEntityField(String ID, String valueType, String value, String typeId) {
        this.id = ID;
        this.valueType = valueType;
        this.value = value;
        this.typeId = typeId;
    }

    public MultiValueEntityField() {
    }

    public boolean equals(Object obj){
        if(this.getValue() == null)
            return false;

        return obj instanceof MultiValueEntityField && this.getValue().equals(((MultiValueEntityField) obj).getValue());
    }

    @Override
    public void addValuesToMultiValueMap(String key, MultiValueMap<String, String> map) {
        if(this.getId() != null){
            map.add(key.concat("[ID]"), this.getId());
        }
        if(this.getTypeId() != null){
            map.add(key.concat("[TYPE_ID]"), this.getTypeId());
        }
        if(this.getValue() != null){
            map.add(key.concat("[VALUE]"), this.getValue());
        }
        if(this.getValueType() != null){
            map.add(key.concat("[VALUE_TYPE]"), this.getValueType());
        }
    }

    @Override
    public void addValuesToMultiValueMap(String field, String key, MultiValueMap<String, String> map) {
        if(this.getId() != null){
            map.add(field.concat(key).concat("[ID]"), this.getId());
        }
        if(this.getTypeId() != null){
            map.add(field.concat(key).concat("[TYPE_ID]"), this.getTypeId());
        }
        if(this.getValue() != null){
            map.add(field.concat(key).concat("[VALUE]"), this.getValue());
        }
        if(this.getValueType() != null){
            map.add(field.concat(key).concat("[VALUE_TYPE]"), this.getValueType());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public static boolean containsValue(ArrayList<MultiValueEntityField> fields, String value) {
        if(
                fields == null ||
                        value == null
                ){
            return false;
        }

        for(MultiValueEntityField multiValueEntityField : fields){
            if(multiValueEntityField.equals(new MultiValueEntityField(null, null, value, null))){
                return true;
            }
        }

        return false;
    }
}
