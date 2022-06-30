package com.guppy.phoneclean.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.Logger;
import com.guppy.phoneclean.MainActivity;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.AppOpenManager;
import com.guppy.phoneclean.ad.PopAd;
import com.guppy.phoneclean.model.BaseRsp;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.HttpUtils;

import org.json.JSONObject;

import static com.guppy.phoneclean.IConstant.popAd;

public class StartPageActivity extends BaseActivity {

    SysConfig sysConfig = DbUtils.getConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        loadData();
        createTimer();
        popAd = new PopAd(this,false,new AdListener(){});
        popAd.load();
    }

    private void createTimer() {
        if (sysConfig == null) return;
        if (sysConfig.getType() != 1) return;
        if (IConstant.IS_DEBUG) Log.i(TAG, "createTimer: "+sysConfig.getType());
        /*new AppOpenManager(StartPageActivity.this, new AppOpenManager.OnShowAdCompleteListener(){
            @Override
            public void onShowAdComplete() {
                toMain();
            }
        }).loadAd();*/

        toMain();
    }

    private void loadData(){
        try {
            JSONObject params = new JSONObject();
            HttpUtils.newPostStringRequest(this, IConstant.INIT_URL, params, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (IConstant.IS_DEBUG) Logger.log("initConfig success>>>" + response);
                        SysConfig sysConfig = DbUtils.getConfig();
                        BaseRsp baseRsp = new BaseRsp().parser(response);
                        if (baseRsp != null && baseRsp.getRetCode() == 0) {
                            JSONObject data = new JSONObject(baseRsp.getData());
                            String userId = data.optString("userid");
                            int type = data.optInt("type");
                            int notification = data.optInt("notification");
                            int disdesktopalert = data.optInt("disdesktopalert");
                            int interval = data.optInt("interval");
                            int timegap = data.optInt("timegap");
                            int clicklimit = data.optInt("clicklimit");
                            int dis_clicklimit_time = data.optInt("dis_clicklimit_time");
                            sysConfig.setUserId(userId);
                            sysConfig.setType(1);
                            sysConfig.setNotification(1);
                            sysConfig.setDisdesktopalert(disdesktopalert);
                            sysConfig.setInterval(interval);
                            sysConfig.setTimegap(timegap);
                            sysConfig.setClicklimit(clicklimit);
                            sysConfig.setDis_clicklimit_time(dis_clicklimit_time);
                            sysConfig.setNotification_msg(data.optJSONArray("notification_msg"));
                            JSONObject admob_ads = data.optJSONObject("admob_ads");
                            if (admob_ads == null) admob_ads = new JSONObject();
                            sysConfig.setAdmob_banners(admob_ads.optString("banner"));
                            sysConfig.setAdmob_pops(admob_ads.optString("pop"));
                            sysConfig.setAdmob_rewards(admob_ads.optString("rewarded"));
                            sysConfig.setAdmob_nativead(admob_ads.optString("nativead"));
                            sysConfig.setAdmob_open(admob_ads.optString("open"));
                            JSONObject pangle_ads = data.optJSONObject("pangle_ads");
                            if (pangle_ads == null) pangle_ads = new JSONObject();
                            sysConfig.setPangle_appid(pangle_ads.optString("appid"));
                            sysConfig.setPangle_banner50(pangle_ads.optString("banner50"));
                            sysConfig.setPangle_banner250(pangle_ads.optString("banner250"));
                            sysConfig.setPangle_pop(pangle_ads.optString("pop"));
                            sysConfig.setPangle_rewarded(pangle_ads.optString("rewarded"));
                            sysConfig.setPangle_nativead(pangle_ads.optString("nativead"));
                            DbUtils.updateSystemConfig(sysConfig);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (sysConfig != null) {
                        if (sysConfig.getType() != 1)
                            StartPageActivity.this.toMain();
                    } else {
                        StartPageActivity.this.toMain();
                    }

                }
            }, error -> {
                if (IConstant.IS_DEBUG) Logger.log("err>>>" + error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final void toMain() {
        if (isDestroy) return;
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }
}