package com.patent.patentsmanager.enums;

public enum Status {
    NEW("N"),
    PROCESSED("P"),
    LOCKED("L");

    private String status;

    public String getStatus()
    {
        return this.status;
    }

    private Status(String status)
    {
        this.status = status;
    }
}
