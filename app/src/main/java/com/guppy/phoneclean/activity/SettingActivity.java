package com.guppy.phoneclean.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.PangleAdListener;
import com.guppy.phoneclean.ad.PanglePop;
import com.guppy.phoneclean.ad.Pop;
import com.guppy.phoneclean.ad.PopAd;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.NotificationsUtils;

import static com.guppy.phoneclean.IConstant.popAd;

public class SettingActivity extends BaseActivity {

    private RelativeLayout relaNotification,relaBattery,relaPrivacy;
    private TextView vistinName;
    private ImageView imgNotification,imgBattery;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        loadBannerAd();
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void initView() {
        relaNotification = findViewById(R.id.rela_notification);
        relaBattery = findViewById(R.id.rela_battery);
        relaPrivacy = findViewById(R.id.rela_privacy);
        vistinName = findViewById(R.id.vistin_name);
        imgBattery = findViewById(R.id.img_battery);
        imgNotification = findViewById(R.id.img_notification);

        relaNotification.setOnClickListener(view -> {
            //控制开关字体颜色
            NotificationsUtils.requestNotify(SettingActivity.this);
        });
        relaBattery.setOnClickListener(view -> requestIgnoreBatteryOptimizations());
        findViewById(R.id.toolbar_left_img).setOnClickListener(v -> {
            finish();
        });

        vistinName.setText(getVerName(this));
        relaPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysConfig sysConfig = DbUtils.getConfig();
                /*showLoading();
                if (sysConfig.getType() == 1){
                    new Pop(SettingActivity.this,new AdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();
                            closeLoading();
                            startActivity(new Intent(SettingActivity.this,PrivacyPolicyActivity.class));
                        }
                    }).load();
                }else {
                    new PanglePop(SettingActivity.this,new PangleAdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();

                            startActivity(new Intent(SettingActivity.this,PrivacyPolicyActivity.class));
                        }
                    }).loadPopAd();
                }*/
                if (popAd == null) return;
                popAd.show1(new AdListener(){
                    @Override
                    public void onClose() {
                        super.onClose();
                        startActivity(new Intent(SettingActivity.this,PrivacyPolicyActivity.class));
                    }
                });
            }
        });


    }


    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!NotificationsUtils.isNotificationEnabled(this)) {
            imgNotification.setActivated(false);
        } else {
            imgNotification.setActivated(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imgBattery.setActivated(isIgnoringBatteryOptimizations());
        }
    }


    /**
     * 申请加入白名单：
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        if (isHuawei()) {
            goHuaweiSetting();
        } else if (isXiaomi()) {
            goXiaomiSetting();
        } else if (isOPPO()) {
            goOPPOSetting();
        } else if (isVIVO()) {
            goVIVOSetting();
        } else if (isMeizu()) {
            goMeizuSetting();
        } else if (isSamsung()) {
            goSamsungSetting();
        } else if (isLeTV()) {
            goLetvSetting();
        } else if (isSmartisan()) {
            goSmartisanSetting();
        } else if (isYijia()) {
            goYiJiaSetting();
        } else {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            } catch (Exception e) {
                if (IConstant.IS_DEBUG) e.printStackTrace();
            }
        }

    }

    /**
     * 判断我们的应用是否在白名单中：
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }


    /**
     * 跳转到指定应用的首页
     */
    private void showActivity(@NonNull String packageName) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(intent);
        }catch (Exception e){}
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private void showActivity(@NonNull String packageName, @NonNull String activityDir) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, activityDir));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){}
    }


    public boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    private void goHuaweiSetting() {
        try {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }
    }

    public static boolean isXiaomi() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
    }

    private void goXiaomiSetting() {
        showActivity("com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity");
    }

    public static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }

    private void goOPPOSetting() {
        try {
            showActivity("com.coloros.phonemanager");
        } catch (Exception e1) {
            try {
                showActivity("com.oppo.safe");
            } catch (Exception e2) {
                try {
                    showActivity("com.coloros.oppoguardelf");
                } catch (Exception e3) {
                    showActivity("com.coloros.safecenter");
                }
            }
        }
    }

    public static boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }

    private void goVIVOSetting() {
        showActivity("com.iqoo.secure");
    }

    public static boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
    }

    private void goMeizuSetting() {
        showActivity("com.meizu.safe");
    }

    public static boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
    }

    private void goSamsungSetting() {
        try {
            showActivity("com.samsung.android.sm_cn");
        } catch (Exception e) {
            showActivity("com.samsung.android.sm");
        }
    }

    public static boolean isLeTV() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("letv");
    }

    private void goLetvSetting() {
        showActivity("com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity");
    }

    public static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }

    private void goSmartisanSetting() {
        showActivity("com.smartisanos.security");
    }

    public static boolean isYijia() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oneplus");
    }

    private void goYiJiaSetting() {
        showActivity("com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
    }
}