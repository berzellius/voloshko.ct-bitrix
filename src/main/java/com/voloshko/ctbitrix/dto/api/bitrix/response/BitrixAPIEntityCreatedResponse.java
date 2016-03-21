package com.voloshko.ctbitrix.dto.api.bitrix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixAPIEntityCreatedResponse extends BitrixAPIResponse {
    private Long result;

    @Override
    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }
}
