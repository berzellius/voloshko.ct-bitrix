package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voloshko.ctbitrix.dto.api.bitrix.annotations.RequireByDefault;
import lombok.Data;

/**
 * Created by berz on 27.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixCRMEntityCCLD extends BitrixCRMEntityWithID {
    @RequireByDefault
    @JsonProperty("ASSIGNED_BY_ID")
    private Long assigned_by_id;
    @RequireByDefault
    @JsonProperty("COMPANY_ID")
    private Long company_id;
    @RequireByDefault
    @JsonProperty("CREATED_BY_ID")
    private Long created_by_id;
    @RequireByDefault
    @JsonProperty("DATE_CREATE")
    private String date_create;
    @RequireByDefault
    @JsonProperty("DATE_MODIFY")
    private String date_modify;
    @RequireByDefault
    @JsonProperty("MODIFY_BY_ID")
    private Long modify_by_id;
    @RequireByDefault
    @JsonProperty("OPENED")
    private String opened;
    @RequireByDefault
    @JsonProperty("ORIGIN_ID")
    private String origin_id;
    @RequireByDefault
    @JsonProperty("ORIGINATOR_ID")
    private String originator_id;

    public Long getAssigned_by_id() {
        return assigned_by_id;
    }

    public void setAssigned_by_id(Long assigned_by_id) {
        this.assigned_by_id = assigned_by_id;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Long getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(Long created_by_id) {
        this.created_by_id = created_by_id;
    }

    public String getDate_create() {
        return date_create;
    }

    public void setDate_create(String date_create) {
        this.date_create = date_create;
    }

    public String getDate_modify() {
        return date_modify;
    }

    public void setDate_modify(String date_modify) {
        this.date_modify = date_modify;
    }

    public Long getModify_by_id() {
        return modify_by_id;
    }

    public void setModify_by_id(Long modify_by_id) {
        this.modify_by_id = modify_by_id;
    }

    public String getOpened() {
        return opened;
    }

    public void setOpened(String opened) {
        this.opened = opened;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(String origin_id) {
        this.origin_id = origin_id;
    }

    public String getOriginator_id() {
        return originator_id;
    }

    public void setOriginator_id(String originator_id) {
        this.originator_id = originator_id;
    }
}
