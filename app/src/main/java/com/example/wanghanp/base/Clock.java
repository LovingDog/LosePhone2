package com.example.wanghanp.base;

public class Clock {
    private String clockIconUrl;
    private String clockType;
    private String clockTime;
    private String clockStatus;
    private boolean clockShow;

    public boolean isClockShow() {
        return clockShow;
    }

    public void setClockShow(boolean clockShow) {
        this.clockShow = clockShow;
    }

    public String getClockIconUrl() {
        return clockIconUrl;
    }

    public void setClockIconUrl(String clockIconUrl) {
        this.clockIconUrl = clockIconUrl;
    }

    public String getClockType() {
        return clockType;
    }

    public void setClockType(String clockType) {
        this.clockType = clockType;
    }

    public String getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }

    public String getClockStatus() {
        return clockStatus;
    }

    public void setClockStatus(String clockStatus) {
        this.clockStatus = clockStatus;
    }
}
