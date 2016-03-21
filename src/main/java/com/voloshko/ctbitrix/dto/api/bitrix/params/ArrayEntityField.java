package com.voloshko.ctbitrix.dto.api.bitrix.params;

import org.springframework.util.MultiValueMap;

import java.util.ArrayList;

/**
 * Created by berz on 13.03.2016.
 */
public class ArrayEntityField extends EntityField {
    private ArrayList<String> values;

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    @Override
    public void addValuesToMultiValueMap(String key, MultiValueMap<String, String> map) {
        // KEY[0] = VAL1
        // KEY[1] = VAL2
        Integer i = 0;
        for(String value : this.getValues()){
            map.add(key.toUpperCase().concat("[").concat(i.toString()).concat("]"), value);
            i++;
        }
    }

    @Override
    public void addValuesToMultiValueMap(String field, String key, MultiValueMap<String, String> map) {
        // field[KEY][0] = VAL1
        // field[KEY][1] = VAL2
        Integer i = 0;
        for(String value : this.getValues()){
            map.add(field.concat("[").concat(key.toUpperCase()).concat("]").concat("[").concat(i.toString()).concat("]"), value);
            i++;
        }
    }
}
