package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by berz on 27.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixCRMDeal extends BitrixCRMEntityCCLD {
    public BitrixCRMDeal() {
    }

    @JsonProperty("ADDITIONAL_INFO")
    private String additional_info;
    @JsonProperty("BEGINDATE")
    private String begindate;
    @JsonProperty("CLOSED")
    private String closed;
    @JsonProperty("CLOSEDATE")
    private String closedate;
    @JsonProperty("COMMENTS")
    private String comments;
    @JsonProperty("CONTACT_ID")
    private Long contact_id;
    @JsonProperty("CURRENCY_ID")
    private String currency_id;
    @JsonProperty("LEAD_ID")
    private Long lead_id;
    @JsonProperty("PROBABILITY")
    private String probability;
    @JsonProperty("STAGE_ID")
    private String stage_id;
    @JsonProperty("TITLE")
    private String title;
    @JsonProperty("TYPE_ID")
    private String type_id;
    /* Рекламный канал */
    @JsonProperty("UF_CRM_1458500417")
    private String uf_crm_1458500417;


    public String getAdditional_info() {
        return additional_info;
    }

    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }

    public String getBegindate() {
        return begindate;
    }

    public void setBegindate(String begindate) {
        this.begindate = begindate;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getClosedate() {
        return closedate;
    }

    public void setClosedate(String closedate) {
        this.closedate = closedate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getContact_id() {
        return contact_id;
    }

    public void setContact_id(Long contact_id) {
        this.contact_id = contact_id;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public Long getLead_id() {
        return lead_id;
    }

    public void setLead_id(Long lead_id) {
        this.lead_id = lead_id;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getStage_id() {
        return stage_id;
    }

    public void setStage_id(String stage_id) {
        this.stage_id = stage_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getUf_crm_1458500417() {
        return uf_crm_1458500417;
    }

    public void setUf_crm_1458500417(String uf_crm_1458500417) {
        this.uf_crm_1458500417 = uf_crm_1458500417;
    }

    @JsonIgnore
    public String getMarketingChannel(){
        return this.getUf_crm_1458500417();
    }

    @JsonIgnore
    public void setMarketingChannel(String marketingChannel){
        this.setUf_crm_1458500417(marketingChannel);
    }
}
