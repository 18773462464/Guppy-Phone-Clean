package com.guppy.phoneclean.model;

import org.json.JSONObject;

public class NotificationMsg extends BaseModel<NotificationMsg> {

    int id;
    String title;
    String body;
    String url;

    @Override
    public NotificationMsg parser(String str) {
        try {
            JSONObject json = new JSONObject(str);
            setId(json.optInt("id"));
            setTitle(json.optString("title"));
            setBody(json.optString("body"));
            setUrl(json.optString("url"));
            return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
