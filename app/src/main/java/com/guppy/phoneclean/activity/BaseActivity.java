package com.guppy.phoneclean.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.Banner;
import com.guppy.phoneclean.ad.PangleBanner;
import com.guppy.phoneclean.ad.PopAd;
import com.guppy.phoneclean.dialog.LoadingDialog;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.EasyPermissions;

public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    protected String TAG = this.getClass().getSimpleName();
    public boolean isDestroy;
    public Context context;
    public Handler handler = new Handler();
    public LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }


    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    protected void startActivitys(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void startAdActivitys(Class<?> pClass, Bundle bundle, boolean isfinish) {
        if (checkPermissions()) {
            startActivitys(pClass, bundle);
            if (isfinish) finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    protected int RC_CAMERA_AND_LOCATION = 2022;
    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public boolean checkPermissions() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            // 没有权限，进行权限请求
            EasyPermissions.requestPermissions(this, getString(R.string.string_read_cache), RC_CAMERA_AND_LOCATION, perms);
            return false;
        }
    }

    public final void showLoading() {
        closeLoading();
        try {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void closeLoading() {
        try {
            if (loadingDialog != null) loadingDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadBannerAd() {
        handler.postDelayed(banner,500);
    }

    Runnable banner = () -> {
        try {
            Banner ad_x = findViewById(R.id.ad_x);
            PangleBanner ad = findViewById(R.id.pangle_ad);
            ad.setVisibility(isBannerVisibility() ? View.VISIBLE : View.GONE);
            ad_x.setVisibility(isBannerVisibility() ? View.GONE : View.VISIBLE);
            SysConfig sysConfig = DbUtils.getConfig();
            if (sysConfig.getType() == 2){
                if (ad != null){
                    ad.loadBanner();
                }
            } else {
                if (ad_x != null ) {
                    ad_x.load();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    boolean isBannerVisibility(){
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig.getType() == 2 ){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        handler.removeCallbacks(banner);
    }
}
