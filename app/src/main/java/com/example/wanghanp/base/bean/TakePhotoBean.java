package com.example.wanghanp.base.bean;

/**
 * Created by wanghanping on 2018/1/4.
 */

public class TakePhotoBean {
    private String path;
    private boolean delete;
    private String time;

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
