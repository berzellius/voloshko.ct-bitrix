package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voloshko.ctbitrix.dto.api.bitrix.params.MultiValueEntityField;
import lombok.Data;

import java.util.ArrayList;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonDeserialize(using = BitrixEntityDeserializer.class)
public class BitrixCRMLead extends BitrixCRMEntityWithID {
    @JsonProperty("TITLE")
    private String title;
    @JsonProperty("NAME")
    private String name;
    @JsonProperty("SECOND_NAME")
    private String second_name;
    @JsonProperty("LAST_NAME")
    private String last_name;
    @JsonProperty("COMPANY_TITLE")
    private String company_title;
    @JsonProperty("SOURCE_ID")
    private String source_id;
    @JsonProperty("SOURCE_DESCRIPTION")
    private String source_description;
    @JsonProperty("STATUS_ID")
    private String status_id;
    @JsonProperty("STATUS_DESCRIPTION")
    private String status_description;
    @JsonProperty("POST")
    private String post;
    @JsonProperty("ADDRESS")
    private String address;
    @JsonProperty("ADDRESS_2")
    private String address_2;
    @JsonProperty("ADDRESS_CITY")
    private String address_city;
    @JsonProperty("ADDRESS_POSTAL_CODE")
    private String address_postal_code;
    @JsonProperty("ADDRESS_REGION")
    private String address_region;
    @JsonProperty("ADDRESS_PROVINCE")
    private String address_province;
    @JsonProperty("ADDRESS_COUNTRY")
    private String address_country;
    @JsonProperty("ADDRESS_COUNTRY_CODE")
    private String address_country_code;
    @JsonProperty("CURRENCY_ID")
    private String currency_id;
    @JsonProperty("OPPORTUNITY")
    private String opportunity;
    @JsonProperty("OPENED")
    private String opened;
    @JsonProperty("COMMENTS")
    private String comments;
    @JsonProperty("ASSIGNED_BY_ID")
    private Long assigned_by_id;
    @JsonProperty("CREATED_BY_ID")
    private Long created_by_id;
    @JsonProperty("MODIFY_BY_ID")
    private Long modify_by_id;
    @JsonProperty("DATE_CREATE")
    private String date_create;
    @JsonProperty("DATE_MODIFY")
    private String date_modify;
    @JsonProperty("COMPANY_ID")
    private Long company_id;
    @JsonProperty("CONTACT_ID")
    private Long contact_id;
    @JsonProperty("DATE_CLOSED")
    private String date_closed;
    @JsonProperty("PHONE")
    private ArrayList<MultiValueEntityField> phone;
    @JsonProperty("EMAIL")
    private ArrayList<MultiValueEntityField> email;
    @JsonProperty("WEB")
    private ArrayList<MultiValueEntityField> web;
    @JsonProperty("IM")
    private ArrayList<MultiValueEntityField> im;
    @JsonProperty("ORIGINATOR_ID")
    private String originator_id;
    @JsonProperty("ORIGIN_ID")
    private String origin_id;

    // Поле "Рекламный канал"
    @JsonProperty("UF_CRM_1457876076")
    private String uf_crm_1457876076;

    public static BitrixCRMLead newInstance(){
        return new BitrixCRMLead();
    }

    public BitrixCRMLead title(String title){
        this.setTitle(title);
        return this;
    }

    public BitrixCRMLead sourceID(String sourceID){
        this.setSource_id(sourceID);
        return this;
    }

    public BitrixCRMLead assignedByID(Long assignedByID){
        this.setAssigned_by_id(assignedByID);
        return this;
    }

    public BitrixCRMLead phones(ArrayList<MultiValueEntityField> phones){
        this.setPhone(phones);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCompany_title() {
        return company_title;
    }

    public void setCompany_title(String company_title) {
        this.company_title = company_title;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getSource_description() {
        return source_description;
    }

    public void setSource_description(String source_description) {
        this.source_description = source_description;
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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getAddress_city() {
        return address_city;
    }

    public void setAddress_city(String address_city) {
        this.address_city = address_city;
    }

    public String getAddress_postal_code() {
        return address_postal_code;
    }

    public void setAddress_postal_code(String address_postal_code) {
        this.address_postal_code = address_postal_code;
    }

    public String getAddress_region() {
        return address_region;
    }

    public void setAddress_region(String address_region) {
        this.address_region = address_region;
    }

    public String getAddress_province() {
        return address_province;
    }

    public void setAddress_province(String address_province) {
        this.address_province = address_province;
    }

    public String getAddress_country() {
        return address_country;
    }

    public void setAddress_country(String address_country) {
        this.address_country = address_country;
    }

    public String getAddress_country_code() {
        return address_country_code;
    }

    public void setAddress_country_code(String address_country_code) {
        this.address_country_code = address_country_code;
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

    public String getOpened() {
        return opened;
    }

    public void setOpened(String opened) {
        this.opened = opened;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getAssigned_by_id() {
        return assigned_by_id;
    }

    public void setAssigned_by_id(Long assigned_by_id) {
        this.assigned_by_id = assigned_by_id;
    }

    public Long getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(Long created_by_id) {
        this.created_by_id = created_by_id;
    }

    public Long getModify_by_id() {
        return modify_by_id;
    }

    public void setModify_by_id(Long modify_by_id) {
        this.modify_by_id = modify_by_id;
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

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
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

    public ArrayList<MultiValueEntityField> getPhone() {
        return phone;
    }

    public void setPhone(ArrayList<MultiValueEntityField> phone) {
        this.phone = phone;
    }

    public ArrayList<MultiValueEntityField> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<MultiValueEntityField> email) {
        this.email = email;
    }

    public ArrayList<MultiValueEntityField> getWeb() {
        return web;
    }

    public void setWeb(ArrayList<MultiValueEntityField> web) {
        this.web = web;
    }

    public ArrayList<MultiValueEntityField> getIm() {
        return im;
    }

    public void setIm(ArrayList<MultiValueEntityField> im) {
        this.im = im;
    }

    public String getOriginator_id() {
        return originator_id;
    }

    public void setOriginator_id(String originator_id) {
        this.originator_id = originator_id;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(String origin_id) {
        this.origin_id = origin_id;
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
