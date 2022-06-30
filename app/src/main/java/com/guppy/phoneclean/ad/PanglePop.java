package com.guppy.phoneclean.ad;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.Logger;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;

/**
 * author: His cat
 * date:   On 2022/1/14
 */
public class PanglePop {
    boolean isAdd;
    long mTime;
    Activity context;
    PangleAdListener pangleAdListener;
    boolean loadShow;
    private TTAdNative mTTAdNative;
    private TTFullScreenVideoAd mttFullVideoAd;
    private static final String TAG = PanglePop.class.getName();
    private final Handler handler = new Handler();
    private int idx = -1;

    public PanglePop setIdx(int idx) {
        this.idx = idx;
        return this;
    }

    public PanglePop(Activity context, boolean loadShow, PangleAdListener pangleAdListener) {
        this(context, loadShow, false, pangleAdListener);
    }

    public PanglePop(Activity context, boolean loadShow, boolean isAdd, PangleAdListener pangleAdListener) {
        this.context = context;
        this.loadShow = loadShow;
        this.pangleAdListener = pangleAdListener;
        mTime = System.currentTimeMillis();
        this.isAdd = isAdd;
    }

    public PanglePop(Activity context, PangleAdListener pangleAdListener) {
        this(context, true, pangleAdListener);
    }

    public void loadPopAd() {
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig.getAllow_pop_show()> System.currentTimeMillis()){
            sysConfig.setClickPopCount(0);
            DbUtils.updateSystemConfig(sysConfig);
            onCall(null);
            pangleAdListener.onClose();
            return;
        }

        if (sysConfig.getClickPopCount()>sysConfig.getClicklimit()){
            if (IConstant.IS_DEBUG){
                sysConfig.setAllow_pop_show(System.currentTimeMillis()+2 * 60 * 1000);
            }else {
                sysConfig.setAllow_pop_show(System.currentTimeMillis()+sysConfig.getDis_clicklimit_time()* 60 * 60 * 1000);
            }
            DbUtils.updateSystemConfig(sysConfig);
            onCall(null);
            pangleAdListener.onClose();
            return;
        }

        if ((System.currentTimeMillis() - sysConfig.getLast_pop_show()) / 1000 < sysConfig.getTimegap()) {
            if (IConstant.IS_DEBUG) Logger.log("pop load limit>>>");
            onCall(null);
            pangleAdListener.onClose();
            return;
        }

        String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getPangle_pop()), idx);
        if (TextUtils.isEmpty(adId)) {
            onCall(null);
            pangleAdListener.onClose();
            return;
        }
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(context);
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;
        adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                //模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
                .setExpressViewAcceptedSize(500,500)
                .build();

        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                onCall(null);
                pangleAdListener.onClose();
                if (IConstant.IS_DEBUG) Log.e(TAG, "Callback --> onError: " + code + ", " + message);
            }

            @Override

            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                if (IConstant.IS_DEBUG) Log.e(TAG, "Callback --> onFullScreenVideoAdLoad");

                mttFullVideoAd = ad;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        if (IConstant.IS_DEBUG) Log.d(TAG, "Callback --> FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        pangleAdListener.onClose();
                        sysConfig.setClickPopCount(sysConfig.getClickPopCount()+1);
                        DbUtils.updateSystemConfig(sysConfig);
                        if (IConstant.IS_DEBUG) Log.d(TAG, "Callback --> FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        pangleAdListener.onClose();
                        if (IConstant.IS_DEBUG) Log.d(TAG, "Callback --> FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        if (IConstant.IS_DEBUG) Log.d(TAG, "Callback --> FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        //pangleAdListener.onClose();
                        if (IConstant.IS_DEBUG) Log.d(TAG, "Callback --> FullVideoAd skipped");

                    }

                });
                onCall(PanglePop.this);
                if (loadShow) {
                    show();
                }
            }

            @Override
            public void onFullScreenVideoCached() {

            }

        });

    }

    private final void onCall(PanglePop ad) {
        if (IConstant.IS_DEBUG) show();
        try {
            long delay = IConstant.AD_LOAD_DELAY - (System.currentTimeMillis() - mTime);
            if (delay < 0) delay = 0;
            handler.postDelayed(() -> {
                if (pangleAdListener != null) pangleAdListener.loaded(ad);
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            sysConfig.setLast_pop_show(System.currentTimeMillis());
            DbUtils.updateSystemConfig(sysConfig);
            if (mttFullVideoAd != null) {
                mttFullVideoAd.showFullScreenVideoAd(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


