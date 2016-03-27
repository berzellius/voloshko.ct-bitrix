package com.voloshko.ctbitrix.dto.api.bitrix.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BitrixCRMEntity {


}
