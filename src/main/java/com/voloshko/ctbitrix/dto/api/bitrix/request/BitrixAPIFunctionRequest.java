package com.voloshko.ctbitrix.dto.api.bitrix.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMEntity;
import com.voloshko.ctbitrix.dto.api.bitrix.params.ArrayEntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.params.EntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.params.SimpleEntityField;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BitrixAPIFunctionRequest {
    private String auth;
    private String incorrectMessage;

    protected List<Field> getFields(Class<? extends Object> cl) {
        List<Field> f = new ArrayList<Field>();
        f.addAll(Arrays.asList(cl.getDeclaredFields()));

        Class s = cl.getSuperclass();
        if (s != null) {
            List<Field> sf = getFields(s);
            f.addAll(sf);
        }

        return f;
    }

    /*
    *
    * Для запросов, не поддерживающих отправку запросов в json,
    * необходимо преобразование POJO->MultiValueMap
    * MultiValueMap - набор пар ключ-значение, которые можно передать GET или POST(x-www-form-urlencoded) запросом
     */
    public MultiValueMap<String, String> entityFieldsToMultiValueMap(){
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        try {
            // Проходимся по полям объекта
            for(Field f : this.getFields(this.getClass())){
                f.setAccessible(true);
                if(EntityField.class.isAssignableFrom(f.getType())){
                    EntityField ef = (EntityField) f.get(this);
                    if(ef != null) {
                        // Тип EntityField реализует метод добавления данных в MultiValueMap
                        ef.addValuesToMultiValueMap(f.getName(), map);
                    }
                }

                if(Collection.class.isAssignableFrom(this.getClass())){
                    // Здесь проходимся по элементам коллекции и ищем там либо
                    // рассмотренный уже EntityField,
                    // либо Comparable, который может представлять из себя String, Long, Integer и т.д.
                    Collection collection = (Collection) f.get(this);
                    Iterator iterator = collection.iterator();
                    Integer i = 0;
                    while (iterator.hasNext()){
                        Object obj = iterator.next();
                        String name = f.getName().toUpperCase().concat("[").concat(i.toString()).concat("]");
                        if(EntityField.class.isAssignableFrom(obj.getClass())){
                            ((EntityField) obj).addValuesToMultiValueMap(name, map);
                        }
                        if(Comparable.class.isAssignableFrom(obj.getClass())){
                            map.add(name, obj.toString());
                        }
                        i++;
                    }
                }

                if(BitrixCRMEntity.class.isAssignableFrom(f.getType())) if(f.get(this) != null){
                    // Если в запросе хотим передать сущность из таблицы типов Bitrix CRM,
                    // то нужно преобразовать ее в набор значений с ключами вида
                    // название_поля_содержащего_сущность[поле_внутри_сущности]...
                    // Здесь ожидаем 3 типа полей - EntityField, Comparable и Collection
                    // Collection может содержать как EntityField, так и Comparable
                    for(Field bef : this.getFields(f.get(this).getClass())){
                        bef.setAccessible(true);
                        if(EntityField.class.isAssignableFrom(bef.getType())){
                            EntityField ef = (EntityField) bef.get(f.get(this));
                            if(ef != null){
                                ef.addValuesToMultiValueMap(f.getName(), bef.getName(), map);
                            }
                        }

                        if(Comparable.class.isAssignableFrom(bef.getType())){
                            // String, Long, etc
                            Object o = bef.get(f.get(this));
                            if(o != null) {
                                String val = o.toString();
                                map.add(f.getName().concat("[").concat(bef.getName().toUpperCase()).concat("]"), val);
                            }
                        }

                        if(Collection.class.isAssignableFrom(bef.getType())){
                            System.out.println("Collection field!");
                            Collection collection = (Collection) bef.get(f.get(this));
                            if(collection != null) {
                                Iterator iterator = collection.iterator();
                                Integer i = 0;
                                while (iterator.hasNext()) {
                                    Object obj = iterator.next();
                                    String name = f.getName().concat("[").concat(bef.getName().toUpperCase()).concat("]").concat("[").concat(i.toString()).concat("]");
                                    if (EntityField.class.isAssignableFrom(obj.getClass())) {
                                        ((EntityField) obj).addValuesToMultiValueMap(name, map);
                                    }
                                    if (Comparable.class.isAssignableFrom(obj.getClass())) {
                                        map.add(name, obj.toString());
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return map;
    }

    public abstract boolean correct();

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getIncorrectMessage() {
        return incorrectMessage;
    }

    public void setIncorrectMessage(String incorrectMessage) {
        this.incorrectMessage = incorrectMessage;
    }
}
