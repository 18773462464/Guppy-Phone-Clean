package com.guppy.phoneclean.bean;

public class Video {
    private int id = 0;
    private String path = null;
    private String name = null;
    private String resolution = null;// 分辨率
    private long size = 0;
    private long date = 0;
    private long duration = 0;
    private boolean isPlay = false;
    private boolean acctivated = false;
    private int acctivatedCount = 0;


    public Video(int id, String path, String name, String resolution, long size, long date, long duration) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.resolution = resolution;
        this.size = size;
        this.date = date;
        this.duration = duration;
    }

    public Video(int id, String path, String name, String resolution, long size, long date, long duration, boolean acctivated) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.resolution = resolution;
        this.size = size;
        this.date = date;
        this.duration = duration;
        this.acctivated=acctivated;
    }

    public int getAcctivatedCount() {
        return acctivatedCount;
    }

    public void setAcctivatedCount(int acctivatedCount) {
        this.acctivatedCount = acctivatedCount;
    }

    public Video() {
    }

    public boolean isAcctivated() {
        return acctivated;
    }

    public void setAcctivated(boolean acctivated) {
        this.acctivated = acctivated;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Video [id=" + id + ", path=" + path + ", name=" + name + ", resolution=" + resolution + ", size=" + size + ", date=" + date
                + ", duration=" + duration + "]";
    }

}
