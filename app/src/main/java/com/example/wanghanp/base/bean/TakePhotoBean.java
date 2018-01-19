package com.example.wanghanp.base.bean;

/**
 * Created by wanghanping on 2018/1/4.
 */

public class TakePhotoBean {
    private String path;
    private boolean delete;
    private String time;
    private boolean mIsSelf;

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    private double mLat;
    private double mLong;

    public boolean ismIsSelf() {
        return mIsSelf;
    }

    public void setmIsSelf(boolean mIsSelf) {
        this.mIsSelf = mIsSelf;
    }

    public String getmPhoneNum() {
        return mPhoneNum;
    }

    public void setmPhoneNum(String mPhoneNum) {
        this.mPhoneNum = mPhoneNum;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    private String mPhoneNum;
    private String mName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
