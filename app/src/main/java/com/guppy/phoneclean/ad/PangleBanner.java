package com.guppy.phoneclean.ad;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * author: His cat
 * date:   On 2022/1/17
 */
public class PangleBanner extends FrameLayout{
    public PangleBanner(@NonNull  Context context) {
        super(context);
    }

    public PangleBanner(@NonNull  Context context, @Nullable  AttributeSet attrs) {
        super(context, attrs);
    }

    public PangleBanner(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean isLoaded;
    private long startTime = 0;
    TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAd;
    private static final String TAG = PangleBanner.class.getName();

    public final void loadBanner() {
        if (isLoaded) return;
        removeAllViews();
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
        String adId = AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getPangle_banner50()), -1);
        if (TextUtils.isEmpty(adId)) {
            return;
        }
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getContext());
        //step3:Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId) //广告位id
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(320,50) //期望模板广告view的size,单位dp
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                if (IConstant.IS_DEBUG) Log.e(TAG,  "load error : " + code + ", " + message);
                removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> bannerAds) {
                if (IConstant.IS_DEBUG) Log.d(TAG, "onNativeExpressAdLoad: "+bannerAds.get(0));
                if (bannerAds == null || bannerAds.size() == 0){
                    return;
                }
                mTTAd = bannerAds.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                if (mTTAd!=null) mTTAd.render();
                //bannerAds.get(0).render();
            }
        });
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View bannerView, int type) {
                if (IConstant.IS_DEBUG) Log.d(TAG, "Ad clicked: ");
                SysConfig sysConfig = DbUtils.getConfig();
                sysConfig.setClickPopCount(sysConfig.getClickPopCount()+1);
                DbUtils.updateSystemConfig(sysConfig);
            }

            @Override
            public void onAdShow(View bannerView, int type) {
                if (IConstant.IS_DEBUG) Log.d(TAG, "Ad showed");
            }

            @Override
            public void onRenderFail(View bannerView, String msg, int code) {
                if (IConstant.IS_DEBUG) Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                if (IConstant.IS_DEBUG) Log.e(TAG, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View bannerView, float width, float height) {
                if (IConstant.IS_DEBUG) Log.i("ExpressView", "Render success::" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                removeAllViews();
                addView(bannerView);
                isLoaded = true;
            }

        });

        ad.setDislikeCallback((Activity)getContext(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                //用户选择不喜欢原因后，移除广告展示
                removeAllViews();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onRefuse() {

            }

        });
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isLoaded = false;
        if (mTTAd != null) {
            removeAllViews();
            mTTAd.destroy();
        }
    }

}
