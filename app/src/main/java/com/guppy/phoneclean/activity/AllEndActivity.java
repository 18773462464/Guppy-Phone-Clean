package com.guppy.phoneclean.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MuteThisAdListener;
import com.google.android.gms.ads.MuteThisAdReason;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.guppy.phoneclean.DbHelper;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.MainActivity;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CloseAdAdapter;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.TextFormater;

import java.util.List;

public class AllEndActivity extends BaseActivity implements View.OnClickListener{

    private TextView tipTxt;
    private String type;
    private long size;
    private NativeAd nativeAd;
    FrameLayout frameLayout;
    private RecyclerView rv;
    private CloseAdAdapter closeAdAdapter;
    private NativeAdView adView;
    private RelativeLayout go1,go2;
    private TextView tvGo1,tvGo2;
    private ImageView imgAd, ivGo1,ivGo2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_end);

        findViewById(R.id.iv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tipTxt = findViewById(R.id.tv_tip);
        frameLayout = findViewById(R.id.ad);
        go1 = findViewById(R.id.rl_go1);
        go2 = findViewById(R.id.rl_go2);
        tvGo1 = findViewById(R.id.tv_go1);
        tvGo2 = findViewById(R.id.tv_go2);
        ivGo1 = findViewById(R.id.iv_go1);
        ivGo2 = findViewById(R.id.iv_go2);
        imgAd = findViewById(R.id.img_ad);
        go1.setOnClickListener(this);
        go2.setOnClickListener(this);
        initData();
        loadAd();
    }

    private void initData(){
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("TYPE");
        size = bundle.getLong("size");
        switch (type) {
            case "SPEED": {
                tipTxt.setVisibility(View.GONE);
                tvGo1.setText(getString(R.string.lower_the_CPU_temperature));
                tvGo2.setText(getString(R.string.app_junk_cache));
                ivGo1.setImageResource(R.mipmap.ic_cpu_cooldown);
                ivGo2.setImageResource(R.mipmap.ic_cache);
                break;
            }
            case "COOLDOWN":
                tipTxt.setVisibility(View.GONE);
                tvGo1.setText(getString(R.string.clearing_background_software));
                tvGo2.setText(getString(R.string.app_junk_cache));
                ivGo1.setImageResource(R.mipmap.ic_speed);
                ivGo2.setImageResource(R.mipmap.ic_cache);
                break;
            case "CACHE": {
                tipTxt.setVisibility(View.GONE);
                tvGo1.setText(getString(R.string.clearing_background_software));
                tvGo2.setText(getString(R.string.lower_the_CPU_temperature));
                ivGo1.setImageResource(R.mipmap.ic_speed);
                ivGo2.setImageResource(R.mipmap.ic_cpu_cooldown);
                break;
            }
            case "clearUp": {
                if (size == 0) {
                    size = 1500;
                }
                tipTxt.setVisibility(View.VISIBLE);
                tipTxt.setText(getString(R.string.string_clearup_size) + TextFormater.dataSizeFormat(size));
                tvGo1.setText(getString(R.string.clearing_background_software));
                tvGo2.setText(getString(R.string.lower_the_CPU_temperature));
                ivGo1.setImageResource(R.mipmap.ic_speed);
                ivGo2.setImageResource(R.mipmap.ic_cpu_cooldown);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_go1:
                jumpActivity(type);
                break;
            case R.id.rl_go2:
                jumpActivity1(type);
                break;
        }
    }

    private void jumpActivity(String type) {
        switch (type) {
            case "SPEED":
                startActivitys(CpuCoolDownActivity.class, null);
                finish();
                break;
            case "COOLDOWN":
            case "CACHE":
            default: startActivitys(SpeedActivity.class, null);
                finish();
        }
    }

    private void jumpActivity1(String type) {
        switch (type) {
            case "SPEED":
            case "COOLDOWN":
                startActivitys(CacheClearActivity.class, null);
                finish();
                break;
            case "CACHE":
            default:
                startActivitys(CpuCoolDownActivity.class, null);
                finish();
                break;
        }
    }


    private void loadAd(){
        String AD_UNIT_ID = "";
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig != null){
            AD_UNIT_ID= AppUtils.indexList(AppUtils.jsonArray2List(sysConfig.getAdmob_nativead()), -1);
        }
        if (TextUtils.isEmpty(AD_UNIT_ID)) {
            return;
        }
        AdLoader.Builder builder = new AdLoader.Builder(context,AD_UNIT_ID);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                // If this callback occurs after the activity is destroyed, you must call
                // destroy and return or you may get a memory leak.
                boolean isDestroyed = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestroyed = isDestroyed();
                }
                if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (AllEndActivity.this.nativeAd != null) {
                    AllEndActivity.this.nativeAd.destroy();
                }
                AllEndActivity.this.nativeAd = nativeAd;
                adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                populateNativeAdView(nativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
                imgAd.setVisibility(View.GONE);
            }
        });
       // builder.withNativeAdOptions(new NativeAdOptions.Builder().setRequestCustomMuteThisAd(true).build());
        builder.withNativeAdOptions(new NativeAdOptions.Builder()
                .setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build())
                .setReturnUrlsForImageAssets(false)
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                .setRequestCustomMuteThisAd(true).build());

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "onAdFailedToLoad: "+ loadAdError );
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView){
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        rv = adView.findViewById(R.id.rv);
        adView.findViewById(R.id.ddd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativeAd.isCustomMuteThisAdEnabled()) {
                    Log.i(TAG, "onNativeAdLoaded: "+nativeAd.getMuteThisAdReasons() );
                    enableCustomMuteWithReasons(nativeAd.getMuteThisAdReasons());
                    adView.findViewById(R.id.nativeAd).setVisibility(View.GONE);
                } else {
                    nativeAd.destroy();
                    adView.removeAllViews();
                    adView.destroy();
                    imgAd.setVisibility(View.VISIBLE);
                }
            }
        });

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    /*refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");*/
                    super.onVideoEnd();
                }
            });
        } else {
           /* videoStatus.setText("Video status: Ad does not contain a video asset.");
            refresh.setEnabled(true);*/
        }
    }

    private void enableCustomMuteWithReasons(List<MuteThisAdReason> reasons) {
        //TODO: This method should show your custom mute button and provide the list
        // of reasons to the interface that are to be displayed when the user mutes
        // the ad.
        closeAdAdapter = new CloseAdAdapter(reasons,onItemClickLitener);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(closeAdAdapter);
    }

    CloseAdAdapter.OnItemClickLitener onItemClickLitener = new CloseAdAdapter.OnItemClickLitener() {
        @Override
        public void onItemClick(int position, MuteThisAdReason reasons) {
            nativeAd.muteThisAd(reasons);
            nativeAd.destroy();
            adView.removeAllViews();
            adView.destroy();
            imgAd.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

}