package com.voloshko.ctbitrix.dto.api.bitrix.params;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Created by berz on 13.03.2016.
 */
public class SimpleEntityField extends EntityField {
    public SimpleEntityField() {
    }

    public SimpleEntityField(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void addValuesToMultiValueMap(String key, MultiValueMap<String, String> map) {
        map.add(key, this.getValue());
    }

    @Override
    public void addValuesToMultiValueMap(String field, String key, MultiValueMap<String, String> map) {
        map.add(field.concat("[").concat(key).concat("]"), this.getValue());
    }
}
