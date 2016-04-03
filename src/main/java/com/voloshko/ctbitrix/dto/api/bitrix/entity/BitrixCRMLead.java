package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voloshko.ctbitrix.dto.api.bitrix.annotations.RequireByDefault;
import com.voloshko.ctbitrix.dto.api.bitrix.params.MultiValueEntityField;
import lombok.Data;

import java.util.ArrayList;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonDeserialize(using = BitrixEntityDeserializer.class)
public class BitrixCRMLead extends BitrixCRMEntityCCL {
    @RequireByDefault
    @JsonProperty("TITLE")
    private String title;
    @RequireByDefault
    @JsonProperty("COMPANY_TITLE")
    private String company_title;
    @RequireByDefault
    @JsonProperty("STATUS_ID")
    private String status_id;
    @RequireByDefault
    @JsonProperty("STATUS_DESCRIPTION")
    private String status_description;
    @RequireByDefault
    @JsonProperty("CURRENCY_ID")
    private String currency_id;
    @RequireByDefault
    @JsonProperty("OPPORTUNITY")
    private String opportunity;
    @RequireByDefault
    @JsonProperty("CONTACT_ID")
    private Long contact_id;
    @RequireByDefault
    @JsonProperty("DATE_CLOSED")
    private String date_closed;
    // Поле "Рекламный канал"
    @RequireByDefault
    @JsonProperty("UF_CRM_1457876076")
    private String uf_crm_1457876076;

    public static BitrixCRMLead newInstance(){
        return new BitrixCRMLead();
    }

    public BitrixCRMLead title(String title){
        this.setTitle(title);
        return this;
    }

    public BitrixCRMLead assignedByID(Long assignedByID){
        this.setAssigned_by_id(assignedByID);
        return this;
    }

    public BitrixCRMLead marketingChannel(String marketingChannel){
        this.setMarketingChannel(marketingChannel);
        return this;
    }

    public BitrixCRMLead contactID(Long contactID){
        this.setContact_id(contactID);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany_title() {
        return company_title;
    }

    public void setCompany_title(String company_title) {
        this.company_title = company_title;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getStatus_description() {
        return status_description;
    }

    public void setStatus_description(String status_description) {
        this.status_description = status_description;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(String opportunity) {
        this.opportunity = opportunity;
    }

    public Long getContact_id() {
        return contact_id;
    }

    public void setContact_id(Long contact_id) {
        this.contact_id = contact_id;
    }

    public String getDate_closed() {
        return date_closed;
    }

    public void setDate_closed(String date_closed) {
        this.date_closed = date_closed;
    }

    @JsonIgnore
    public String getMarketingChannel(){
        return this.getUf_crm_1457876076();
    }

    @JsonIgnore
    public void setMarketingChannel(String marketingChannel){
        this.setUf_crm_1457876076(marketingChannel);
    }

    public String getUf_crm_1457876076() {
        return uf_crm_1457876076;
    }

    public void setUf_crm_1457876076(String uf_crm_1457876076) {
        this.uf_crm_1457876076 = uf_crm_1457876076;
    }
}
