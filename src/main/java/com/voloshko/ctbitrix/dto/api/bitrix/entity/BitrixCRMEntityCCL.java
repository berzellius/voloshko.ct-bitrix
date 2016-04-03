package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voloshko.ctbitrix.dto.api.bitrix.annotations.RequireByDefault;
import com.voloshko.ctbitrix.dto.api.bitrix.params.MultiValueEntityField;
import lombok.Data;

import java.util.ArrayList;

/**
 * Created by berz on 27.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BitrixCRMEntityCCL extends BitrixCRMEntityCCLD {
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
    @RequireByDefault
    @JsonProperty("COMMENTS")
    private String comments;
    @RequireByDefault
    @JsonProperty("PHONE")
    private ArrayList<MultiValueEntityField> phone;
    @RequireByDefault
    @JsonProperty("EMAIL")
    private ArrayList<MultiValueEntityField> email;
    @RequireByDefault
    @JsonProperty("WEB")
    private ArrayList<MultiValueEntityField> web;
    @RequireByDefault
    @JsonProperty("IM")
    private ArrayList<MultiValueEntityField> im;
    @RequireByDefault
    @JsonProperty("NAME")
    private String name;
    @RequireByDefault
    @JsonProperty("SECOND_NAME")
    private String second_name;
    @RequireByDefault
    @JsonProperty("LAST_NAME")
    private String last_name;
    @RequireByDefault
    @JsonProperty("POST")
    private String post;
    @RequireByDefault
    @JsonProperty("SOURCE_ID")
    private String source_id;
    @RequireByDefault
    @JsonProperty("SOURCE_DESCRIPTION")
    private String source_description;

    public BitrixCRMEntityCCL sourceID(String sourceID){
        this.setSource_id(sourceID);
        return this;
    }

    public BitrixCRMEntityCCL phones(ArrayList<MultiValueEntityField> phones){
        this.setPhone(phones);
        return this;
    }

    public BitrixCRMEntityCCL addPhone(String value, String valueType){
        if(this.getPhone() == null){
            return this.phones(MultiValueEntityField.arrayWithOneInstance(null, valueType, value, null));
        }

        this.getPhone().add(MultiValueEntityField.newInstance(null, valueType, value, null));

        return this;
    }

    public BitrixCRMEntityCCL addEmail(String value, String valueType){
        if(this.getEmail() == null){
            return this.emails(MultiValueEntityField.arrayWithOneInstance(null, valueType, value, null));
        }

        this.getEmail().add(MultiValueEntityField.newInstance(null, valueType, value, null));

        return this;
    }

    public BitrixCRMEntityCCL emails(ArrayList<MultiValueEntityField> emails){
        this.setEmail(emails);
        return this;
    }

    public BitrixCRMEntityCCL webs(ArrayList<MultiValueEntityField> webs){
        this.setWeb(webs);
        return this;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
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
}
