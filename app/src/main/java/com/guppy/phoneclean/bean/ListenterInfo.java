package com.guppy.phoneclean.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class ListenterInfo implements Parcelable {
    private int ramSize = 0;
    private int progress= 0;
    private String packageName= "";
    private int max= 0;
    private float percent= 0;
    private byte[] icon= null;


    protected ListenterInfo(Parcel in) {
        ramSize = in.readInt();
        progress = in.readInt();
        packageName = in.readString();
        max = in.readInt();
        percent = in.readFloat();
        icon = in.createByteArray();
    }

    public static final Creator<ListenterInfo> CREATOR = new Creator<ListenterInfo>() {
        @Override
        public ListenterInfo createFromParcel(Parcel in) {
            return new ListenterInfo(in);
        }

        @Override
        public ListenterInfo[] newArray(int size) {
            return new ListenterInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ramSize);
        dest.writeInt(progress);
        dest.writeString(packageName);
        dest.writeInt(max);
        dest.writeFloat(percent);
        dest.writeByteArray(icon);
    }


    public ListenterInfo(int ramSize) {
        this.ramSize = ramSize;
    }

    public ListenterInfo(int progress, String packageName) {
        this.progress = progress;
        this.packageName = packageName;
    }

    public ListenterInfo(int progress, String packageName, int max, float percent, byte[] icon) {
        this.progress = progress;
        this.packageName = packageName;
        this.max = max;
        this.percent = percent;
        this.icon = icon;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public int getRamSize() {
        return ramSize;
    }

    public void setRamSize(int ramSize) {
        this.ramSize = ramSize;
    }


}