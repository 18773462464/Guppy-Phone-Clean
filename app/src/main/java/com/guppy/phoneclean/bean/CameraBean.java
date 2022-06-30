package com.guppy.phoneclean.bean;

public class CameraBean {
    private String path;
    private boolean acctivated = false;
    private int acctivatedCount = 0;

    public CameraBean(String path, boolean acctivated) {
        this.path = path;
        this.acctivated = acctivated;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAcctivated() {
        return acctivated;
    }

    public void setAcctivated(boolean acctivated) {
        this.acctivated = acctivated;
    }

    public int getAcctivatedCount() {
        return acctivatedCount;
    }

    public void setAcctivatedCount(int acctivatedCount) {
        this.acctivatedCount = acctivatedCount;
    }
}
