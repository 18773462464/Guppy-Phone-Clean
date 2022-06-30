package com.guppy.phoneclean.model;

import org.json.JSONObject;

public abstract class BaseModel<T> {

    public abstract T parser(String str);

    public JSONObject toJSONObject() {
        return null;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = toJSONObject();
        if (jsonObject != null) return jsonObject.toString();
        return super.toString();
    }
}
