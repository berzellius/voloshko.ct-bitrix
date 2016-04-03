package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by berz on 27.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixCRMContact extends BitrixCRMEntityCCL {
    public BitrixCRMContact() {
    }

    @JsonProperty("BIRTHDATE")
    private String birthdate;
    @JsonProperty("EXPORT")
    private String export;
    @JsonProperty("LEAD_ID")
    private Long lead_id;
    @JsonProperty("PHOTO")
    private String photo;
    @JsonProperty("TYPE_ID")
    private String type_id;
    @JsonProperty("UF_CRM_56E58DA18BA6B")
    private String uf_crm_56E58DA18BA6B;

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public Long getLead_id() {
        return lead_id;
    }

    public void setLead_id(Long lead_id) {
        this.lead_id = lead_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getUf_crm_56E58DA18BA6B() {
        return uf_crm_56E58DA18BA6B;
    }

    public void setUf_crm_56E58DA18BA6B(String uf_crm_56E58DA18BA6B) {
        this.uf_crm_56E58DA18BA6B = uf_crm_56E58DA18BA6B;
    }

    @JsonIgnore
    public String getMarketingChannel(){
        return this.getUf_crm_56E58DA18BA6B();
    }

    @JsonIgnore
    public void setMarketingChannel(String marketingChannel){
        this.setUf_crm_56E58DA18BA6B(marketingChannel);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BitrixCRMContact)){
            return false;
        }

        if(obj == this){
            return true;
        }

        if(this.getId() == null){
            return false;
        }

        return this.getId().equals(((BitrixCRMContact) obj).getId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 15).append(this.getId()).toHashCode();
    }
}
