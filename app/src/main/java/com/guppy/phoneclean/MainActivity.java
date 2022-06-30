package com.guppy.phoneclean;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.NotificationUtils;
import com.google.android.material.tabs.TabLayout;
import com.guppy.phoneclean.activity.BaseActivity;
import com.guppy.phoneclean.adapter.BasicFragmentAdapter;
import com.guppy.phoneclean.dialog.IAlertDialog;
import com.guppy.phoneclean.fragment.ClearFragment;
import com.guppy.phoneclean.fragment.LoseFragment;
import com.guppy.phoneclean.interf.MainListenter;
import com.guppy.phoneclean.utils.IObserver;
import com.guppy.phoneclean.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends BaseActivity implements MainListenter, Observer,View.OnClickListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView notifClose,notifOpen;
    private LinearLayout notificationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPage);
        notifClose = findViewById(R.id.notif_close);
        notifOpen = findViewById(R.id.notif_open);
        notificationStatus = findViewById(R.id.notification_status);
        notifOpen.setOnClickListener(this);
        notifClose.setOnClickListener(this);

        IObserver.getInstance().post(IObserver.ObserverType.WHITE_OPEN_TIP);

        if (!NotificationUtils.areNotificationsEnabled()) {
            notificationStatus.setVisibility(View.VISIBLE);
        } else {
            notificationStatus.setVisibility(View.GONE);
        }
    }

    private void initData() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new ClearFragment());
        fragmentList.add(new LoseFragment());
        BasicFragmentAdapter adapter = new BasicFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.setSelected(true);
                tabLayout.setScrollPosition(position,positionOffset,true);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!NotificationUtils.areNotificationsEnabled()) {
            notificationStatus.setVisibility(View.VISIBLE);
        } else {
            notificationStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        executeActionOnclick(v);
    }

    private void executeActionOnclick(View view) {
        if (view.getId() == R.id.notif_open) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= 26) {
                // android 8.0引导
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            } else if (Build.VERSION.SDK_INT >= 21) {
                // android 5.0-7.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
            } else {
                // 其他
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view.getId() == R.id.notif_close) {
            notificationStatus.setVisibility(View.GONE);
        }
    }


    @Override
    public void openActivity(Class<?> pClass, Bundle bundle) {
        startAdActivitys(pClass, bundle,false);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void update(Observable o, Object arg) {
        if (IConstant.IS_DEBUG) System.out.println("onCheckApplicationWhite  update1");
        if (arg instanceof IObserver.ObserverType) {
            if (IConstant.IS_DEBUG) System.out.println("onCheckApplicationWhite  update2");
            IObserver.ObserverType observerType = (IObserver.ObserverType) arg;
            if (observerType == IObserver.ObserverType.WHITE_OPEN_TIP) {
                if (IConstant.IS_DEBUG) System.out.println("onCheckApplicationWhite  update");
                boolean first = PrefUtils.getBoolean(this,getString(R.string.app_name),"FIRST", true);
                boolean successfullyAuthorized = PrefUtils.getBoolean(this,getString(R.string.app_name),"successfullyAuthorized", true);
                if (first && !successfullyAuthorized) {
                    boolean ignoringBatteryOptimizations = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ignoringBatteryOptimizations = isIgnoringBatteryOptimizations();
                    }
                    if (!ignoringBatteryOptimizations) {
                        //请求白名单
                        runOnUiThread(() -> {
                            if (!IConstant.wtiteTipShow) {
                                IConstant.wtiteTipShow = true;
                                new IAlertDialog(context, getString(R.string.string_tip_needopen)).show();
                            }
                        });

                    }
                }
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
}