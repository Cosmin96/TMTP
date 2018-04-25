package com.tmtp.web.TMTP.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class AppResponse {

    private static final long serialVersionUID = 7789284253047806745L;
    private Object data;
    private boolean success = true;
    private String detail = "Request Processed Successfully";

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date timestamp = new Date();

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AppResponse{" +
                "data=" + data +
                ", success=" + success +
                ", detail='" + detail + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
