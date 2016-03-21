package com.voloshko.ctbitrix.dto.api.bitrix.params;

/**
 * Created by berz on 16.03.2016.
 */
public class SortEntityField extends MapEntityField {
    public enum Direction{
        ASC("ASC"),
        DESC("DESC");

        String value;

        public String getValue(){
            return this.value;
        }

        Direction(String value){
            this.value = value;
        }
    }

    public SortEntityField sort(String field, Direction direction){
        this.set(field, direction.getValue());

        return this;
    }
}
