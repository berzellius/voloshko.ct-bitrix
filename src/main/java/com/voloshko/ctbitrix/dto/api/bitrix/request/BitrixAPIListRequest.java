package com.voloshko.ctbitrix.dto.api.bitrix.request;

import com.voloshko.ctbitrix.dto.api.bitrix.params.ArrayEntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.params.RangeEntityField;
import com.voloshko.ctbitrix.dto.api.bitrix.params.SortEntityField;

import java.util.ArrayList;

/**
 * Created by berz on 13.03.2016.
 */
public class BitrixAPIListRequest extends BitrixAPIFunctionRequest {
    private RangeEntityField filter;
    private ArrayEntityField select;
    private SortEntityField order;

    @Override
    public boolean correct() {
        if(this.getAuth() == null || this.getAuth().equals("")) {
            this.setIncorrectMessage("auth field is null");
            return false;
        }

        if(this.getOrder() != null){
            for(String key : this.getOrder().getValue().keySet()){
                if(
                        !this.getOrder().getValue().get(key).equals("asc") &&
                                !this.getOrder().getValue().get(key).equals("desc")
                        ){
                    this.setIncorrectMessage("order fields must have 'asc' or 'desc' value");
                }
            }
        }

        return true;
    }

    public BitrixAPIListRequest select(String... selectFields){
        if(this.getSelect() == null) {
             this.setSelect(new ArrayEntityField());
        }

        if(this.getSelect().getValues() == null){
            this.getSelect().setValues(new ArrayList<>());
        }

        for(String field : selectFields){
            this.getSelect().getValues().add(field);
        }

        return this;
    }

    public BitrixAPIListRequest filterOne(String field, Long value){
        if(this.getFilter() == null) {
            RangeEntityField rangeEntityField = new RangeEntityField();
            this.setFilter(rangeEntityField);
        }
        this.getFilter().setOne(field, value);

        return this;
    }

    public BitrixAPIListRequest filterFrom(String field, Long from, RangeEntityField.BoundType boundType){
        if(this.getFilter() == null) {
            RangeEntityField rangeEntityField = new RangeEntityField();
            this.setFilter(rangeEntityField);
        }

        this.getFilter().fromValue(field, from, boundType);

        return this;
    }

    public BitrixAPIListRequest filterTo(String field, Long to, RangeEntityField.BoundType boundType){
        if(this.getFilter() == null) {
            RangeEntityField rangeEntityField = new RangeEntityField();
            this.setFilter(rangeEntityField);
        }

        this.getFilter().upTo(field, to, boundType);

        return this;
    }

    public  BitrixAPIListRequest range(String field, Long from, Long to, RangeEntityField.BoundType fromBoundType, RangeEntityField.BoundType toBoundType){
        if(this.getFilter() == null) {
            RangeEntityField rangeEntityField = new RangeEntityField();
            this.setFilter(rangeEntityField);
        }

        this.getFilter().setRange(field, from, to, fromBoundType, toBoundType);

        return this;
    }

    public BitrixAPIListRequest sort(String field, SortEntityField.Direction direction){
        if(this.getOrder() == null){
            SortEntityField sortEntityField = new SortEntityField();
            this.setOrder(sortEntityField);
        }

        this.getOrder().sort(field, direction);

        return this;
    }

    public ArrayEntityField getSelect() {
        return select;
    }

    public void setSelect(ArrayEntityField select) {
        this.select = select;
    }

    public SortEntityField getOrder() {
        return order;
    }

    public void setOrder(SortEntityField order) {
        this.order = order;
    }

    public RangeEntityField getFilter() {
        return filter;
    }

    public void setFilter(RangeEntityField filter) {
        this.filter = filter;
    }
}
