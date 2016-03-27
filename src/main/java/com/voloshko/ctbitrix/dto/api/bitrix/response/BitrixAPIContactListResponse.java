package com.voloshko.ctbitrix.dto.api.bitrix.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMContact;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMEntityCCL;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLead;
import lombok.Data;

import java.util.List;

/**
 * Created by berz on 13.03.2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitrixAPIContactListResponse extends BitrixAPIResponse {
    private List<BitrixCRMContact> result;
    private Long total;
    private Long next;

    @Override
    public List<BitrixCRMContact> getResult() {
        return result;
    }

    public void setResult(List<BitrixCRMContact> result) {
        this.result = result;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getNext() {
        return next;
    }

    public void setNext(Long next) {
        this.next = next;
    }
}
