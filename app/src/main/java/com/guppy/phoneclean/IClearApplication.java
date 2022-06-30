package com.guppy.phoneclean;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.service.ServicesMangerHolder;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.LocaleUtil;
import com.guppy.phoneclean.utils.PrefUtils;
import com.tjhello.easy.billing.java.BillingEasy;
import com.tjhello.lib.billing.base.anno.ProductType;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;

public class IClearApplication extends MultiDexApplication {

    private static IClearApplication sInstance;

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static Application getInstance() {
        return sInstance;
    }
    private static DbHelper dbHelper;

    public static final SQLiteDatabase getDb() {
        synchronized (sInstance) {
            if (dbHelper == null) dbHelper = new DbHelper(sInstance);
            return dbHelper.getWritableDatabase();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        context = this.getApplicationContext();

        PrefUtils.setBoolean(this,getString(R.string.app_name),"FIRST",false);
        ServicesMangerHolder.getSingleton().initialize();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PrefUtils.setBoolean(this,getString(R.string.app_name),"successfullyAuthorized", isIgnoringBatteryOptimizations());
        }

        AudienceNetworkAds.initialize(this);
        MobileAds.initialize(this);
        payinit();

    }

    private void payinit() {
        BillingEasy.addProductConfig(ProductType.TYPE_SUBS, "1001", "1002", "1003", "1004");
        BillingEasy.setAutoConsume(false);//关闭自动消耗(可按需打开，默认关闭)
        BillingEasy.setAutoAcknowledge(false);//关闭自动确认购买(可按需打开，默认关闭)
        BillingEasy.init(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            }
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return isIgnoring;
    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged");
        LocaleUtil.setLanguage(context, newConfig);
    }*/
}
