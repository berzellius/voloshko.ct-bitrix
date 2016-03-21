package com.voloshko.ctbitrix.dto.api.bitrix.params;

import org.springframework.util.MultiValueMap;

/**
 * Created by berz on 13.03.2016.
 */
public abstract class EntityField {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void addValuesToMultiValueMap(String key, MultiValueMap<String, String> map);

    public abstract void addValuesToMultiValueMap(String field, String key, MultiValueMap<String, String> map);

    public static <T extends EntityField> EntityField biuld(Class<T> cl) throws IllegalAccessException, InstantiationException {
        return cl.newInstance();
    }
}
