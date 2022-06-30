package com.guppy.phoneclean.bean;

/**
 * 文件,可以是文档、apk、压缩包
 */
public class FileBean {
    /**
     * 文件的路径
     */
    public String path;
    /**
     * 文件图片资源的id，drawable或mipmap文件中已经存放doc、xml、xls等文件的图片
     */
    public int iconId;
    private boolean acctivated = false;
    private int acctivatedCount = 0;

    public FileBean(String path, int iconId, boolean acctivated) {
        this.path = path;
        this.iconId = iconId;
        this.acctivated = acctivated;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
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

    @Override
    public String toString() {
        return "FileBean{" +
                "path='" + path + '\'' +
                ", iconId=" + iconId +
                '}';
    }
}
