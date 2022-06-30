package com.guppy.phoneclean.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MainFunctionModel implements Parcelable {
    private int icon;
    private String name;
    private String describe;

    protected MainFunctionModel(Parcel in) {
        icon = in.readInt();
        name = in.readString();
        describe = in.readString();
    }

    public MainFunctionModel(int icon, String name , String describe){
        this.icon = icon;
        this.name = name;
        this.describe = describe;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeString(name);
        dest.writeString(describe);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainFunctionModel> CREATOR = new Creator<MainFunctionModel>() {
        @Override
        public MainFunctionModel createFromParcel(Parcel in) {
            return new MainFunctionModel(in);
        }

        @Override
        public MainFunctionModel[] newArray(int size) {
            return new MainFunctionModel[size];
        }
    };

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
