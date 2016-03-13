package com.voloshko.ctbitrix.dto.api.bitrix.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by berz on 12.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BitrixAPIRequest {
}
