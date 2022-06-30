package com.guppy.phoneclean.ad;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.Logger;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;

import java.util.Random;

import androidx.annotation.NonNull;

public class PopAd {
    Activity context;
    AdRequest adRequest = new AdRequest.Builder().build();
    AdListener adListener;
    boolean loadShow;
    InterstitialAd admobPop;
    long mTime;
    private final Handler handler = new Handler();
    boolean isAdd;
    private TTAdNative mTTAdNative;
    private TTFullScreenVideoAd mttFullVideoAd;
    private static final String TAG = PopAd.class.getName();
    private SysConfig sysConfig;
    private boolean isLoad;


    public PopAd(Activity context, boolean loadShow, AdListener adListener) {
        this(context, loadShow, true, adListener);
    }

    public PopAd(Activity context, boolean loadShow, boolean isAdd, AdListener adListener) {
        this.context = context;
        this.loadShow = loadShow;
        this.adListener = adListener;
        mTime = System.currentTimeMillis();
        sysConfig = DbUtils.getConfig();
        this.isAdd = isAdd;
    }

    public PopAd(Activity context, AdListener adListener) {
        this(context, true, adListener);
    }

    public void show() {
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            sysConfig.setLast_pop_show(System.currentTimeMillis());
            DbUtils.updateSystemConfig(sysConfig);
            isLoad = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (admobPop != null) {
                admobPop.show(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mttFullVideoAd != null) {
                mttFullVideoAd.showFullScreenVideoAd(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show1(AdListener adListener) {
        this.adListener = adListener;
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            sysConfig.setLast_pop_show(System.currentTimeMillis());
            DbUtils.updateSystemConfig(sysConfig);
            if (admobPop == null) {
                loadPop();
                onCall(null);
                return;
            }
            admobPop.show(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private void loadPop(){
        if (isMainThread()) {
            loadADMOB();
        } else {
            context.runOnUiThread(() -> loadADMOB());
        }
    }


    private final void onCall(PopAd ad) {
        //if (IConstant.IS_DEBUG) show();
        try {
            long delay = IConstant.AD_LOAD_DELAY - (System.currentTimeMillis() - mTime);
            if (delay < 0) delay = 0;
            handler.postDelayed(() -> {
                if (adListener != null) adListener.loaded(ad);
                if (ad == null) adListener.onClose();
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (isLoad) return;
        if (IConstant.IS_DEBUG) Logger.log("popad load>>>" + this);
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            /*if (isAdd) {
                if ((System.currentTimeMillis() - sysConfig.getLast_pop_show()) / 1000 < sysConfig.getTimegap()) {
                    if (IConstant.IS_DEBUG) Logger.log("pop load limit>>>");
                    onCall(null);
                    return;
                }
            }*/
            if (sysConfig.getType() == 1) {
                loadPop();
            } else {
                loadPangle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final void pangleErr() {
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            if (sysConfig.getType() == 2) {
                onCall(null);
            } else {
                onCall(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadPangle() {
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig.getAllow_pop_show()> System.currentTimeMillis()){
            sysConfig.setClickPopCount(0);
            DbUtils.updateSystemConfig(sysConfig);
            onCall(null);
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
            return;
        }

        if ((System.currentTimeMillis() - sysConfig.getLast_pop_show()) / 1000 < sysConfig.getTimegap()) {
            if (IConstant.IS_DEBUG) Logger.log("pop load limit>>>");
            onCall(null);
            return;
        }

        String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getPangle_pop()), -1);
        if (TextUtils.isEmpty(adId)) {
            onCall(null);
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
                adListener.onClose();
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
                        adListener.onClose();
                        sysConfig.setClickPopCount(sysConfig.getClickPopCount()+1);
                        DbUtils.updateSystemConfig(sysConfig);
                        if (IConstant.IS_DEBUG) Log.d(TAG, "Callback --> FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        adListener.onClose();
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
                onCall(PopAd.this);
                if (loadShow) {
                    show();
                }
            }

            @Override
            public void onFullScreenVideoCached() {

            }

        });

    }


    private final void admobErr() {
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            if (sysConfig.getType() == 1) {
                //loadFB();
                onCall(null);
            } else {
                onCall(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadADMOB() {
        try {
            String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getAdmob_pops()), -1);
            if (TextUtils.isEmpty(adId)) {
                admobErr();
                adListener.onClose();
                return;
            }
            InterstitialAd.load(context, adId, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    if (IConstant.IS_DEBUG) Logger.log("ADMOB onAdLoaded>>>" + interstitialAd);
                    onCall(PopAd.this);
                    admobPop = interstitialAd;
                    isLoad = true;
                    admobPop.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            onCall(null);
                            loadPop();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            onCall(null);
                            loadPop();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            sysConfig.setClickPopCount(sysConfig.getClickPopCount()+1);
                            DbUtils.updateSystemConfig(sysConfig);
                            onCall(null);
                        }
                    });
                    if (loadShow) show();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (IConstant.IS_DEBUG)
                        Logger.log("ADMOB onError>>>" + loadAdError.toString());
                    //admobErr();
                    //adListener.onClose();
                }
            });
            if (IConstant.IS_DEBUG) Logger.log("load ADMOB Pop>>>" + adId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
