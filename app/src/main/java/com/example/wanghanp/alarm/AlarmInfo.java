package com.example.wanghanp.alarm;

import java.io.Serializable;

public class AlarmInfo implements Serializable {
    private int Hour;//经度
    private int Minute;//维度
    private int LazyLevel;//地址
    private String Ring ;//内容  铃声
    private String Tag;//1开 0关
    private int[] dayOfWeek;
    private String ringResId;
    private String lon;
    private String lat;
    private String location;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getRingResId() {
        return ringResId;
    }

    public void setRingResId(String ringResId) {
        this.ringResId = ringResId;
    }
    //private String id=

    public String getId(){
        String id=""+Hour+Minute+ AlarmInfoDao.getDataDayofWeek(dayOfWeek);
        return id;
    }
    @Override
    public String toString() {
        return "AlarmInfo{" +
                getId()+
                ", Tag='" + Tag + '\'' +
                ", Ring='" + Ring + '\'' +
                ", LazyLevel=" + LazyLevel +
                '}';
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        Hour = hour;
    }

    public int getMinute() {
        return Minute;
    }

    public void setMinute(int minute) {
        Minute = minute;
    }

    public int getLazyLevel() {
        return LazyLevel;
    }

    public void setLazyLevel(int lazyLevel) {
        LazyLevel = lazyLevel;
    }


    public String getRing() {
        return Ring;
    }

    public void setRing(String ring) {
        Ring = ring;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public int[] getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int[] dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
