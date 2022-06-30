package com.guppy.phoneclean.ad;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;

public class AppOpenManager {
    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;

    private boolean isShowingAd = false;
    private Activity activity;
    private OnShowAdCompleteListener onShowAdCompleteListener;

    /** Constructor */
    public AppOpenManager(Activity activity,OnShowAdCompleteListener onShowAdCompleteListener) {
        this.activity = activity;
        this.onShowAdCompleteListener = onShowAdCompleteListener;
    }


    /** Request an ad */
    public void loadAd() {
        SysConfig sysConfig = DbUtils.getConfig();
        String AD_UNIT_ID = "";
        if (sysConfig != null){
            AD_UNIT_ID= AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getAdmob_open()), -1);
        }

        if (TextUtils.isEmpty(AD_UNIT_ID)) {
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(
                activity,
                AD_UNIT_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {

                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        if (IConstant.IS_DEBUG) Log.d(LOG_TAG, "onAdLoaded.");
                        appOpenAd = ad;
                        showAdIfAvailable();

                        appOpenAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    /** Called when full screen content is dismissed. */
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Set the reference to null so isAdAvailable() returns false.
                                        appOpenAd = null;
                                        isShowingAd = false;

                                        if (IConstant.IS_DEBUG) Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");
                                        onShowAdCompleteListener.onShowAdComplete();
                                    }

                                    /** Called when fullscreen content failed to show. */
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        appOpenAd = null;
                                        isShowingAd = false;

                                        if (IConstant.IS_DEBUG) Log.e(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());

                                        onShowAdCompleteListener.onShowAdComplete();
                                    }

                                    /** Called when fullscreen content is shown. */
                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        if (IConstant.IS_DEBUG) Log.d(LOG_TAG, "onAdShowedFullScreenContent.");

                                    }
                                });
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        onShowAdCompleteListener.onShowAdComplete();
                        ResponseInfo responseInfo = loadAdError.getResponseInfo();
                        if (IConstant.IS_DEBUG) Log.d(LOG_TAG, responseInfo.toString());
                        if (IConstant.IS_DEBUG) Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                    }
                });
    }

    /**
     * Interface definition for a callback to be invoked when an app open ad is complete
     * (i.e. dismissed or fails to show).
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }


    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (isShowingAd) {
            return;
        }

        isShowingAd = true;
        if (isAdAvailable())
        appOpenAd.show(activity);

    }

    /** Utility method that checks if ad exists and can be shown. */
    public boolean isAdAvailable() {
        return appOpenAd != null ;
    }

}
