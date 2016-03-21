package com.voloshko.ctbitrix.dto.api.bitrix.params;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 13.03.2016.
 */
public class MapEntityField extends EntityField {
    private HashMap<String, String> value;

    public HashMap<String, String> getValue() {
        return value;
    }

    public void setValue(HashMap<String, String> value) {
        this.value = value;
    }

    public void set(String key, String value){
        if(this.getValue() == null){
            HashMap<String, String> hm = new LinkedHashMap<>();
            hm.put(key, value);
            this.setValue(hm);
        }
        else{
            this.getValue().put(key, value);
        }
    }

    @Override
    public void addValuesToMultiValueMap(String key, MultiValueMap<String, String> map) {
        // KEY[ARG1] = VAL1
        // KEY[ARG2] = VAL2
        for(String k : this.getValue().keySet()){
            map.add(key.toUpperCase().concat("[").concat(k.toUpperCase()).concat("]"), this.getValue().get(k));
        }
    }

    @Override
    public void addValuesToMultiValueMap(String field, String key, MultiValueMap<String, String> map) {
        // field[KEY][ARG1] = VAL1
        // field[KEY][ARG2] = VAL2
        for(String k : this.getValue().keySet()){
            map.add(field.concat(key.toUpperCase()).concat("[").concat(k.toUpperCase()).concat("]"), this.getValue().get(k));
        }
    }
}
