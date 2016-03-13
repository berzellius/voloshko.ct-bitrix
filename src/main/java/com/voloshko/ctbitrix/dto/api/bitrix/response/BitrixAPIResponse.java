package com.voloshko.ctbitrix.dto.api.bitrix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by berz on 12.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BitrixAPIResponse {

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
