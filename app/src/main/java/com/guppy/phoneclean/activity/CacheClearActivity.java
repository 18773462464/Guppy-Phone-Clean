package com.guppy.phoneclean.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.PangleAdListener;
import com.guppy.phoneclean.ad.PanglePop;
import com.guppy.phoneclean.ad.Pop;
import com.guppy.phoneclean.bean.CacheInfo;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.FilesUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CacheClearActivity extends BaseActivity {

    private PackageManager packageManager;
    private TextView executeName,executeStatus;
    private ImageView speedGif;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cache_clear);
        initView();
        initData();
    }

    private void initView() {
        speedGif = findViewById(R.id.img_clear);
        executeName = findViewById(R.id.execute_name);
        executeStatus = findViewById(R.id.execute_status);
    }

    private void initData() {
        loadBannerAd();
        packageManager = getPackageManager();
        startAnimator();
        executeName.setText(R.string.string_ready_scan);
        chackCache();
    }

    private void startAnimator() {
        animator = ObjectAnimator.ofFloat(speedGif, "rotation", 0.0f, 360.0f);
        animator.setDuration(1800);
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.start();
    }

    public void chackCache() {
        new Thread(() -> {
            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : installedPackages) {
                getCacheSize(packageInfo);//反射调用getPackageSizeInfo
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //已卸载应用的缓存目录
            FilesUtils.queryAppCacheFiles(getApplicationContext(), file -> {
                if (file!=null&&file.exists()){
                    file.delete();
                }
            });
            Message.obtain(mHandler, HANDLER_CLEAR_CACHE_MSG).sendToTarget();
        }).start();
    }

    /**
     * 获取到缓存的大小
     *
     * @param packageInfo
     */
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //通过反射获取到当前的方法
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            /**
             * 第一个参数表示当前的这个方法由谁调用的
             * 第二个参数表示包名
             */
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packageInfo,packageManager,mHandler));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Bundle bundle = new Bundle();
    private List<CacheInfo> cacheInfoList = new ArrayList<>();
    //更新扫描道德数据
    public static final int HANDLER_UPDATE_CACHE_MSG = 0XF0F1;
    //清理缓存
    public static final int HANDLER_CLEAR_CACHE_MSG = 0XF0F2;
    //完成
    public static final int HANDLER_COMPLETED_CACHE_MSG = 0XF0F3;
    private long allSize = 0;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (HANDLER_UPDATE_CACHE_MSG == msg.what) {
                if (msg.obj != null && msg.obj instanceof CacheInfo) {
                    CacheInfo info = (CacheInfo) msg.obj;
                    cacheInfoList.add(info);
                    executeName.setText(info.getAppName());
                    allSize += info.getCacheSize();
                    executeStatus.setText(info.getPackageName()
                            /* +"  data size:"+info.getCacheSize()*/);
                }
            } else if (HANDLER_CLEAR_CACHE_MSG == msg.what) {
                IConstant.popAd.show1(new AdListener(){
                    @Override
                    public void onClose() {
                        super.onClose();
                        startActivitys(JunkCleanActivity.class, bundle);
                        finish();
                    }
                });
                /*pop.show1(new AdListener(){
                    @Override
                    public void onClose() {
                        super.onClose();
                        startActivitys(JunkCleanActivity.class, bundle);
                        finish();
                    }});*/
                /*SysConfig sysConfig = DbUtils.getConfig();
                showLoading();
                if (sysConfig.getType() == 1){
                    new Pop(CacheClearActivity.this,new AdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();
                            closeLoading();
                            startActivitys(JunkCleanActivity.class, bundle);
                            finish();
                        }
                    }).load();
                }else {
                    new PanglePop(CacheClearActivity.this,new PangleAdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();
                            closeLoading();
                            startActivitys(JunkCleanActivity.class, bundle);
                            finish();
                        }
                    }).loadPopAd();
                }*/


            }
            return true;
        }
    });

    static class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private PackageInfo packageInfo;
        private PackageManager packageManager;
        private Handler mHandler;

        public MyIPackageStatsObserver(PackageInfo packageInfo, PackageManager packageManager, Handler mHandler) {
            this.packageInfo = packageInfo;
            this.packageManager = packageManager;
            this.mHandler = mHandler;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取到当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;// 缓存大小
            long dataSize = pStats.dataSize; // 数据大小
            //如果当前的缓存大小大于0的话，则表示有缓存
            if (cacheSize > 0) {
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                String packageName = packageInfo.packageName;
                cacheInfo.setAppName(appName);
                cacheInfo.setPackageName(packageName);
                cacheInfo.setIcon(icon);
                cacheInfo.setCacheSize(cacheSize);
                cacheInfo.setDataSize(dataSize);
                Message.obtain(mHandler, HANDLER_UPDATE_CACHE_MSG, cacheInfo).sendToTarget();
            }
        }
    }

    public void cleanAll(String packageName) {
        try {
            Method method = PackageManager.class.getDeclaredMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
            method.invoke(packageManager, packageName, new MyIPackageDataObserver());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        //获取到当前应用程序里面所有的方法
//        try {
//            StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
//            Method mFreeStorageAndNotifyMethod = packageManager.getClass().getMethod(
//                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
//            mFreeStorageAndNotifyMethod.invoke(packageManager,
//                    (long) stat.getBlockCount() * (long) stat.getBlockSize(), new MyIPackageDataObserver()
//            );
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    static class MyIPackageDataObserver extends IPackageDataObserver.Stub {
        public MyIPackageDataObserver() {
        }

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}