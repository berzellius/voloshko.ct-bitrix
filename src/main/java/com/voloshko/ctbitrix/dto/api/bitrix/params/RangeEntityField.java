package com.voloshko.ctbitrix.dto.api.bitrix.params;

/**
 * Created by berz on 15.03.2016.
 */
public class RangeEntityField extends MapEntityField {
    public enum BoundType{
        STRICT("<=", ">="),
        UNSTRICT("<", ">");

        private String symbolLt;
        private String symbolGt;

        public String getSymbolGt(){
            return this.symbolGt;
        }

        public String getSymbolLt(){
            return this.symbolLt;
        }

        BoundType(String symbolLt, String symbolGt){
            this.symbolLt = symbolLt;
            this.symbolGt = symbolGt;
        }
    }

    public RangeEntityField setRange(String field, Long from, Long to, BoundType fromBoundType, BoundType toBoundType){
        if(this.getValue() != null)
            this.getValue().clear();

        this.set(fromBoundType.getSymbolGt().concat(field), from.toString());
        this.set(fromBoundType.getSymbolLt().concat(field), to.toString());

        return this;
    }

    public RangeEntityField setOne(String field, Long one){
        if(this.getValue() != null)
            this.getValue().clear();
        this.set("=".concat(field), one.toString());

        return this;
    }

    public RangeEntityField upTo(String field, Long to, BoundType toBoundType){
        if(
                this.getValue() != null &&
                        (this.getValue().containsKey("=".concat(field)) || this.getValue().size() == 2)
                ){
            this.getValue().clear();
        }

        this.set(toBoundType.getSymbolLt().concat(field), to.toString());

        return this;
    }

    public RangeEntityField fromValue(String field, Long from, BoundType fromBoundType){
        if(
                this.getValue() != null &&
                        (this.getValue().containsKey("=".concat(field)) || this.getValue().size() == 2)
                ){
            this.getValue().clear();
        }

        this.set(fromBoundType.getSymbolGt().concat(field), from.toString());

        return this;
    }
}
