package com.example.finder.dto.input;

public class RequestRecordStatus {
    private String recordStatus;

    public RequestRecordStatus() {
    }

    public RequestRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
}
