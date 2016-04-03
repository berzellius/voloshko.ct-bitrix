package com.voloshko.ctbitrix.dto.site.evrika;

import java.util.List;

/**
 * Created by berz on 27.03.2016.
 */
public class LeadRequest {
    public LeadRequest() {
    }

    private List<Lead> leads;
    private String origin;
    private String password;

    public List<Lead> getLeads() {
        return leads;
    }

    public void setLeads(List<Lead> leads) {
        this.leads = leads;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
