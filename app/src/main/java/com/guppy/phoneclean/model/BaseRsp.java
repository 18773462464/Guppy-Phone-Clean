package com.guppy.phoneclean.model;


import org.json.JSONObject;

public final class BaseRsp extends BaseModel<BaseRsp> {
    int retCode;
    String data;

    @Override
    public BaseRsp parser(String str) {
        try {
            JSONObject json = new JSONObject(str);
            setRetCode(json.optInt("code"));
            setData(json.optString("data"));
            return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
