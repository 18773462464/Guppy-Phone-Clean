package com.guppy.phoneclean.ad;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.Logger;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.coder.vincent.series.common_lib.ToolkitKt.getWindowManager;

/**
 * author: His cat
 * date:   On 2021/11/18
 */
public class Banner extends FrameLayout {
    public Banner(@NonNull @NotNull Context context) {
        super(context);
    }

    public Banner(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Banner(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean isLoaded;
    com.google.android.gms.ads.AdView admobad;
    AdRequest adRequest = new AdRequest.Builder().build();
    int idx = -1;

    public Banner setIdx(int idx) {
        this.idx = idx;
        return this;
    }

    public final void load() {
        if (isLoaded) return;
        if (IConstant.IS_DEBUG) Logger.log("banner load>>>" + this);
        try {
            loadAdmob();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isLoaded = false;
        try {
            if (admobad != null) admobad.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final void loadAdmob() {
        removeAllViews();
        try {
            SysConfig sysConfig = DbUtils.getConfig();
            if (sysConfig.getAllow_pop_show()> System.currentTimeMillis()){
                sysConfig.setClickPopCount(0);
                DbUtils.updateSystemConfig(sysConfig);
                return;
            }
            if (sysConfig.getClickPopCount()>sysConfig.getClicklimit()){
                if (IConstant.IS_DEBUG){
                    sysConfig.setAllow_pop_show(System.currentTimeMillis()+2 * 60 * 1000);
                }else {
                    sysConfig.setAllow_pop_show(System.currentTimeMillis()+sysConfig.getDis_clicklimit_time()* 60 * 60 * 1000);
                }
                DbUtils.updateSystemConfig(sysConfig);
                return;
            }
            String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getAdmob_banners()), idx);
            if (TextUtils.isEmpty(adId)) return;
            admobad = new com.google.android.gms.ads.AdView(getContext());
            admobad.setAdSize(getAdSize(getContext()));
            admobad.setAdUnitId(adId);
            admobad.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    if (IConstant.IS_DEBUG)
                        Logger.log("load admob banner err>>>" + loadAdError.toString());
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    isLoaded = true;
                    if (IConstant.IS_DEBUG)
                        Logger.log("load admob banner success>>>");
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    sysConfig.setClickPopCount(sysConfig.getClickPopCount()+1);
                    DbUtils.updateSystemConfig(sysConfig);
                }

            });
            admobad.loadAd(adRequest);
            addView(admobad);
            if (IConstant.IS_DEBUG) Logger.log("load admob banner >>>" + adId);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private AdSize getAdSize(Context context) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
