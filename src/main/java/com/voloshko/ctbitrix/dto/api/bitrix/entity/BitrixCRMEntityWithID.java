package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by berz on 20.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BitrixCRMEntityWithID extends BitrixCRMEntity {
    @JsonProperty("ID")
    private String id;

    @JsonIgnore
    private boolean changed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    @JsonIgnore
    public boolean isChanged() {
        return changed;
    }

    @JsonIgnore
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
