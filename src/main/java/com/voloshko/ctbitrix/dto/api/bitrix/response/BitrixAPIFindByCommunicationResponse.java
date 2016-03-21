package com.voloshko.ctbitrix.dto.api.bitrix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by berz on 20.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixAPIFindByCommunicationResponse extends BitrixAPIResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Result result;

    @Override
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Result{
        @JsonProperty("LEAD")
        private ArrayList<Long> lead;
        @JsonProperty("CONTACT")
        private ArrayList<Long> contact;
        @JsonProperty("COMPANY")
        private ArrayList<Long> company;

        public ArrayList<Long> getLead() {
            return lead;
        }

        public void setLead(ArrayList<Long> lead) {
            this.lead = lead;
        }

        public ArrayList<Long> getContact() {
            return contact;
        }

        public void setContact(ArrayList<Long> contact) {
            this.contact = contact;
        }

        public ArrayList<Long> getCompany() {
            return company;
        }

        public void setCompany(ArrayList<Long> company) {
            this.company = company;
        }
    }


}
