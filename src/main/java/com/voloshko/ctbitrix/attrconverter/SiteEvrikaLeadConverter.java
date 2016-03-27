package com.voloshko.ctbitrix.attrconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voloshko.ctbitrix.dto.site.evrika.Lead;

import javax.persistence.AttributeConverter;
import java.io.IOException;

/**
 * Created by berz on 27.03.2016.
 */
public class SiteEvrikaLeadConverter implements AttributeConverter<Lead, String> {
    @Override
    public String convertToDatabaseColumn(Lead lead) {
        ObjectMapper objectMapper = new ObjectMapper();
        String s;

        // На null и суда null
        if(lead == null) return null;

        try {
            s = objectMapper.writeValueAsString(lead);
        } catch (IOException e) {
            s = null;
        }

        return s;
    }

    @Override
    public Lead convertToEntityAttribute(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        Lead lead;

        // просто null
        if(s == null) return null;

        try {
            lead = objectMapper.readValue(s, Lead.class);
        } catch (IOException e) {
            lead = null;
        }

        return lead;
    }
}
