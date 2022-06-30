package com.guppy.phoneclean.ad;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

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


import androidx.annotation.NonNull;

/**
 * author: His cat
 * date:   On 2021/11/19
 */
public class Pop {
    Activity context;
    AdRequest adRequest = new AdRequest.Builder().build();
    AdListener adListener;
    boolean loadShow;
    InterstitialAd admobPop;
    long mTime;
    private final Handler handler = new Handler();
    boolean isAdd;
    int idx = -1;

    public Pop setIdx(int idx) {
        this.idx = idx;
        return this;
    }

    public Pop(Activity context, boolean loadShow, AdListener adListener) {
        this(context, loadShow, false, adListener);
    }

    public Pop(Activity context, boolean loadShow, boolean isAdd, AdListener adListener) {
        this.context = context;
        this.loadShow = loadShow;
        this.adListener = adListener;
        mTime = System.currentTimeMillis();
        this.isAdd = isAdd;
    }

    public Pop(Activity context, AdListener adListener) {
        this(context, true, adListener);
    }

    public void show() {
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            sysConfig.setLast_pop_show(System.currentTimeMillis());
            DbUtils.updateSystemConfig(sysConfig);
            if (admobPop != null) {
                admobPop.show(context);
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
            if (admobPop != null) {
                admobPop.show(context);
            }else {
                this.adListener.onClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final void onCall(Pop ad) {
        //if (IConstant.IS_DEBUG) show();
        try {
            long delay = IConstant.AD_LOAD_DELAY - (System.currentTimeMillis() - mTime);
            if (delay < 0) delay = 0;
            handler.postDelayed(() -> {
                if (adListener != null) adListener.loaded(ad);
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (IConstant.IS_DEBUG) Logger.log("popad load>>>" + this);
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            /*if ((System.currentTimeMillis() - sysConfig.getLast_pop_show()) / 1000 < sysConfig.getTimegap()) {
                if (IConstant.IS_DEBUG) Logger.log("pop load limit>>>");
                onCall(null);
                adListener.onClose();
                return;
            }*/
            if (sysConfig.getAllow_pop_show()> System.currentTimeMillis()){
                sysConfig.setClickPopCount(0);
                DbUtils.updateSystemConfig(sysConfig);
                onCall(null);
                adListener.onClose();
                return;
            }
            loadADMOB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final void admobErr() {
        try {
            onCall(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadADMOB() {
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getAdmob_pops()), idx);
            //String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getAdmob_pops()), count);
            if (TextUtils.isEmpty(adId)) {
                admobErr();
                adListener.onClose();
                return;
            }
            if (sysConfig.getClickPopCount()>sysConfig.getClicklimit()){
                if (IConstant.IS_DEBUG){
                    sysConfig.setAllow_pop_show(System.currentTimeMillis()+2 * 60 * 1000);
                }else {
                    sysConfig.setAllow_pop_show(System.currentTimeMillis()+sysConfig.getDis_clicklimit_time()* 60 * 60 * 1000);
                }
                DbUtils.updateSystemConfig(sysConfig);
                admobErr();
                adListener.onClose();
                return;
            }
            InterstitialAd.load(context, adId, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    if (IConstant.IS_DEBUG) Logger.log("ADMOB onAdLoaded>>>" + interstitialAd);
                    onCall(Pop.this);
                    admobPop = interstitialAd;
                    admobPop.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            adListener.onClose();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            adListener.onClose();

                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            sysConfig.setClickPopCount(sysConfig.getClickPopCount()+1);
                            DbUtils.updateSystemConfig(sysConfig);
                            adListener.onClose();
                        }
                    });
                    if (loadShow) show();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (IConstant.IS_DEBUG)
                        Logger.log("ADMOB onError>>>" + loadAdError.toString());
                    admobErr();
                    adListener.onClose();
                }
            });
            if (IConstant.IS_DEBUG) Logger.log("load ADMOB Pop>>>" + adId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
