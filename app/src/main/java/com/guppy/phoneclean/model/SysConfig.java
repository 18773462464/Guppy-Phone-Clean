package com.guppy.phoneclean.model;


import com.guppy.phoneclean.IConstant;

import org.json.JSONArray;
import org.json.JSONObject;

public class SysConfig extends BaseModel<SysConfig> {

    String userId; //用户id
    int type; // 广告类型：1 admob, 2 穿山甲
    int notification;//  0 关闭提醒, 1 通知栏提醒，2 弹框提醒
    int disdesktopalert; // 是否禁用桌面弹框 0 允许, 1 禁止
    int interval; // 通知间隔时间 （notification值为1 才生效）
    int timegap; //插屏间隔时间

    //ad_rules
    int clicklimit; //单用户最大点击广告次数
    int dis_clicklimit_time; // 禁止显示广告的时间（小时）

    //admob广告
    String admob_banners; //banner广告id
    String admob_pops; //插屏广告id
    String admob_rewards; //激励视频广告id
    String admob_nativead;//原生广告id
    String admob_open; //开屏广告id

    //穿山甲广告
    String pangle_appid; //应用广告ID
    String pangle_banner50; //300*50 banner广告id
    String pangle_banner250; //320*250 banner广告id
    String pangle_pop; //插屏广告id
    String pangle_rewarded; //激励视频广告id
    String pangle_nativead; //原生广告id

    JSONArray notification_msg; // 通知内容（数组，随机取一条）
    long last_pop_show; //最后广告弹出时间
    long allow_pop_show; //允许显示广告时间
    int clickPopCount; //点击插屏广告次数
    int clickBannerCount; //点击banner广告次数



    @Override
    public SysConfig parser(String str) {
        try {
            JSONObject json = new JSONObject(str);
            setUserId(json.optString(IConstant.SYSTEM_CONFIG_KEYS[0]));
            setType(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[1]));
            setNotification(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[2]));
            setDisdesktopalert(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[3]));
            setInterval(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[4]));
            setTimegap(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[5]));
            setClicklimit(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[6]));
            setDis_clicklimit_time(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[7]));
            setAdmob_banners(json.optString(IConstant.SYSTEM_CONFIG_KEYS[8]));
            setAdmob_pops(json.optString(IConstant.SYSTEM_CONFIG_KEYS[9]));
            setAdmob_rewards(json.optString(IConstant.SYSTEM_CONFIG_KEYS[10]));
            setAdmob_nativead(json.optString(IConstant.SYSTEM_CONFIG_KEYS[11]));
            setAdmob_open(json.optString(IConstant.SYSTEM_CONFIG_KEYS[12]));
            setPangle_appid(json.optString(IConstant.SYSTEM_CONFIG_KEYS[13]));
            setPangle_banner50(json.optString(IConstant.SYSTEM_CONFIG_KEYS[14]));
            setPangle_banner250(json.optString(IConstant.SYSTEM_CONFIG_KEYS[15]));
            setPangle_pop(json.optString(IConstant.SYSTEM_CONFIG_KEYS[16]));
            setPangle_rewarded(json.optString(IConstant.SYSTEM_CONFIG_KEYS[17]));
            setPangle_nativead(json.optString(IConstant.SYSTEM_CONFIG_KEYS[18]));
            setLast_pop_show(json.optLong(IConstant.SYSTEM_CONFIG_KEYS[19]));
            setNotification_msg(json.optJSONArray(IConstant.SYSTEM_CONFIG_KEYS[20]));
            setClickPopCount(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[21]));
            setClickBannerCount(json.optInt(IConstant.SYSTEM_CONFIG_KEYS[22]));
            setAllow_pop_show(json.optLong(IConstant.SYSTEM_CONFIG_KEYS[23]));
            return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            JSONObject json = new JSONObject();
            json.put(IConstant.SYSTEM_CONFIG_KEYS[0], getUserId());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[1], getType());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[2], getNotification());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[3], getDisdesktopalert());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[4], getInterval());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[5], getTimegap());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[6], getClicklimit());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[7], getDis_clicklimit_time());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[8], getAdmob_banners());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[9], getAdmob_pops());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[10], getAdmob_rewards());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[11], getAdmob_nativead());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[12], getAdmob_open());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[13], getPangle_appid());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[14], getPangle_banner50());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[15], getPangle_banner250());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[16], getPangle_pop());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[17], getPangle_rewarded());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[18], getPangle_nativead());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[19], getLast_pop_show());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[20], getNotification_msg());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[21], getClickPopCount());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[22], getClickBannerCount());
            json.put(IConstant.SYSTEM_CONFIG_KEYS[23], getAllow_pop_show());
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.toJSONObject();
    }

    public int getClicklimit() {
        return clicklimit;
    }

    public void setClicklimit(int clicklimit) {
        this.clicklimit = clicklimit;
    }

    public int getDis_clicklimit_time() {
        return dis_clicklimit_time;
    }

    public void setDis_clicklimit_time(int dis_clicklimit_time) {
        this.dis_clicklimit_time = dis_clicklimit_time;
    }

    public int getTimegap() {
        return timegap;
    }

    public void setTimegap(int timegap) {
        this.timegap = timegap;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getDisdesktopalert() {
        return disdesktopalert;
    }

    public void setDisdesktopalert(int disdesktopalert) {
        this.disdesktopalert = disdesktopalert;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdmob_banners() {
        return admob_banners;
    }

    public void setAdmob_banners(String admob_banners) {
        this.admob_banners = admob_banners;
    }

    public String getAdmob_pops() {
        return admob_pops;
    }

    public void setAdmob_pops(String admob_pops) {
        this.admob_pops = admob_pops;
    }

    public String getAdmob_rewards() {
        return admob_rewards;
    }

    public void setAdmob_rewards(String admob_rewards) {
        this.admob_rewards = admob_rewards;
    }

    public String getAdmob_nativead() {
        return admob_nativead;
    }

    public void setAdmob_nativead(String admob_nativead) {
        this.admob_nativead = admob_nativead;
    }

    public String getAdmob_open() {
        return admob_open;
    }

    public void setAdmob_open(String admob_open) {
        this.admob_open = admob_open;
    }

    public String getPangle_appid() {
        return pangle_appid;
    }

    public void setPangle_appid(String pangle_appid) {
        this.pangle_appid = pangle_appid;
    }

    public String getPangle_banner50() {
        return pangle_banner50;
    }

    public void setPangle_banner50(String pangle_banner50) {
        this.pangle_banner50 = pangle_banner50;
    }

    public String getPangle_banner250() {
        return pangle_banner250;
    }

    public void setPangle_banner250(String pangle_banner250) {
        this.pangle_banner250 = pangle_banner250;
    }

    public String getPangle_nativead() {
        return pangle_nativead;
    }

    public void setPangle_nativead(String pangle_nativead) {
        this.pangle_nativead = pangle_nativead;
    }

    public String getPangle_pop() {
        return pangle_pop;
    }

    public void setPangle_pop(String pangle_pop) {
        this.pangle_pop = pangle_pop;
    }

    public String getPangle_rewarded() {
        return pangle_rewarded;
    }

    public void setPangle_rewarded(String pangle_rewarded) {
        this.pangle_rewarded = pangle_rewarded;
    }

    public long getLast_pop_show() {
        return last_pop_show;
    }

    public void setLast_pop_show(long last_pop_show) {
        this.last_pop_show = last_pop_show;
    }

    public JSONArray getNotification_msg() {
        return notification_msg;
    }

    public void setNotification_msg(JSONArray notification_msg) {
        this.notification_msg = notification_msg;
    }

    public int getClickPopCount() {
        return clickPopCount;
    }

    public void setClickPopCount(int clickPopCount) {
        this.clickPopCount = clickPopCount;
    }

    public int getClickBannerCount() {
        return clickBannerCount;
    }

    public void setClickBannerCount(int clickBannerCount) {
        this.clickBannerCount = clickBannerCount;
    }

    public long getAllow_pop_show() {
        return allow_pop_show;
    }

    public void setAllow_pop_show(long allow_pop_show) {
        this.allow_pop_show = allow_pop_show;
    }
}
