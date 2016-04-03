package com.voloshko.ctbitrix.dto.site.evrika;

/**
 * Created by berz on 27.03.2016.
 */
public class Result {
    public Result(String status) {
        this.setStatus(status);
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
