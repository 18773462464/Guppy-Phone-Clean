package com.guppy.phoneclean.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.UiMessageUtils;
import com.guppy.phoneclean.ClearServicesResule;
import com.guppy.phoneclean.ClearServicesResuleCallBack;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.ScanningClearListenter;
import com.guppy.phoneclean.bean.AppInfo;
import com.guppy.phoneclean.bean.AutoStartInfo;
import com.guppy.phoneclean.bean.ListenterInfo;
import com.guppy.phoneclean.receiver.AppInstallUninstallReceiver;
import com.guppy.phoneclean.utils.IObserver;
import com.guppy.phoneclean.utils.RandomUntil;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CleanerService extends Service implements UiMessageUtils.UiMessageCallback {
    private static final String TAG = "CleanerService";
    private ActivityManager activityManager = null;

    private ClearServicesResuleCallBack clearServicesResuleCallBack;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        } catch (Exception e) {
            System.out.println("ActivityManager getSystemService:" + e.toString());
        }
        registerAppStateChangeReceiver();
        IConstant.isRuningClearServices = true;
        UiMessageUtils.getInstance().addListener(this);
    }

    private AppInstallUninstallReceiver appInstallUninstallReceiver;

    private void registerAppStateChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addDataScheme("package");
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        appInstallUninstallReceiver = new AppInstallUninstallReceiver();
        this.registerReceiver(appInstallUninstallReceiver, intentFilter);
    }

    private void unRegisterAppStateChangeReceiver() {
        if (appInstallUninstallReceiver != null) {
            this.unregisterReceiver(appInstallUninstallReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyServiceImpl();
    }

    public class MyServiceImpl extends ClearServicesResule.Stub {


        @Override
        public void setServicesListenterResult(ClearServicesResuleCallBack callBack) throws RemoteException {
            if (IConstant.IS_DEBUG) System.out.println("onBind setServicesListenterResult");
            clearServicesResuleCallBack = callBack;
            if (clearServicesResuleCallBack != null) {
                clearServicesResuleCallBack.onBindServices();
            }
        }

        @Override
        public void startScanningClear(ScanningClearListenter scanningClearListenter) throws RemoteException {
            CleanerService.this.startScanningClear(scanningClearListenter);
        }
    }


    @Override
    public void handleMessage(@NonNull @NotNull UiMessageUtils.UiMessage localMessage) {
        if (localMessage.getId() == IObserver.ObserverTypePro.CHEACK_WHITE_OPEN.getCode()) {
            if (clearServicesResuleCallBack != null) {
                handler.post(() -> {
                    try {
                        clearServicesResuleCallBack.onCheckApplicationWhite();
                    } catch (RemoteException e) {
                        System.out.println("update handleMessage onCheckApplicationWhite error:" + e.toString());
                    }
                });
            } else {
                if (IConstant.IS_DEBUG) System.out.println("clearServicesResuleCallBack ??????");
            }
        } else if (localMessage.getId() == IObserver.ObserverTypePro.ACTION_TYPE_SEED_OR_ANTIVITRUE_OR_CPUCOOLDOWN.getCode()) {
            if (clearServicesResuleCallBack != null) {
                if (IConstant.IS_DEBUG) System.out.println("??????????????????2");
                handler.post(() -> {
                    try {
                        clearServicesResuleCallBack.onSendNotificationData();
                    } catch (RemoteException e) {
                        System.out.println("update handleMessage onSendNotificationData error:" + e.toString());
                    }
                });
            } else {
                if (IConstant.IS_DEBUG) System.out.println("??????????????????2 clearServicesResuleCallBack ??????");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //START_STICKY
        return START_STICKY_COMPATIBILITY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (IConstant.IS_DEBUG) System.out.println(TAG + " onDestroy");
        activityManager = null;
        unRegisterAppStateChangeReceiver();
        UiMessageUtils.getInstance().removeListener(this);
        stopForeground(true);
    }


    private boolean mIsScanningClear = false;

    public class TaskClear extends AsyncTask<Void, Object, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
            //????????????????????????
            PackageManager packageManager = getApplicationContext().getPackageManager();
            //????????????????????????
            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
            //?????????????????????????????????????????????
            for (PackageInfo packageInfo : installedPackages) {
                AppInfo appInfo = new AppInfo();
                appInfo.setApplicationInfo(packageInfo.applicationInfo);
                appInfo.setVersionCode(packageInfo.versionCode);
                //??????icon
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                appInfo.setIcon(drawable);
                //?????????????????????
                String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                appInfo.setApkName(apkName);
                //?????????????????????
                String packageName = packageInfo.packageName;
                appInfo.setApkPackageName(packageName);
                //??????????????????????????????
                String sourceDir = packageInfo.applicationInfo.sourceDir;
                File file = new File(sourceDir);
                //??????apk?????????
                long size = file.length();
                appInfo.setApkSize(size);
                //??????????????????
                appInfo.setInstallTime(packageInfo.firstInstallTime);
                //????????????????????????????????????
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //????????????app
                    appInfo.setUserApp(false);
                } else {
                    //????????????app
                    appInfo.setUserApp(true);
                }

                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    //?????????sd???
                    appInfo.setRom(false);
                } else {
                    //????????????
                    appInfo.setRom(true);
                }
                appInfos.add(appInfo);
                publishProgress(appInfo);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }
    }

    private ScanningClearListenter scanningClearListenter;

    void setScanningClearListenter(ScanningClearListenter scanningClearListenter) {
        this.scanningClearListenter = scanningClearListenter;
    }

    public void startScanningClear(ScanningClearListenter scanningClearListenter) {
        setScanningClearListenter(scanningClearListenter);
        if (IConstant.IS_DEBUG)
            System.out.println("startScanningClear  scanningClearListenter???" + (scanningClearListenter == null));
        if (!mIsScanningClear) {
            new TaskScanAppsAndClear()
                    .executeOnExecutor(Executors.newCachedThreadPool());
        }
    }


    public class TaskScanAppsAndClear extends AsyncTask<Void, Object, List<AutoStartInfo>> {
        private int mAppCount = 0;
        private int mProgress = 0;

        public TaskScanAppsAndClear() {
        }

        /**
         * ???????????????????????????????????????????????????????????????UI?????????
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsScanningClear = true;
            mAppCount = 0;
            mProgress = 0;
            try {
                if (scanningClearListenter != null) {
                    scanningClearListenter.onCleanStarted();
                }
            } catch (Exception e) {
                if (IConstant.IS_DEBUG) e.printStackTrace();
            }
        }


        /**
         * ?????????????????????????????????????????????????????????publishProgress(values);????????????
         * ?????????????????????????????????????????????
         *
         * @param params
         * @return
         */
        @SuppressLint("WrongConstant")
        @Override
        protected List<AutoStartInfo> doInBackground(Void... params) {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(0);
            Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
            List<ResolveInfo> resolveInfoList = packageManager
                    .queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
            boolean isSystem = false;
            boolean isenable = true;
            ComponentName mComponentName1 = new ComponentName(
                    resolveInfoList.get(0).activityInfo.packageName,
                    resolveInfoList.get(0).activityInfo.name);
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                AutoStartInfo mAutoStartInfo = new AutoStartInfo();

                if ((resolveInfoList.get(0).activityInfo.applicationInfo.flags &
                        ApplicationInfo.FLAG_SYSTEM) != 0) {
                    isSystem = true;
                } else {
                    isSystem = false;
                }
                if (packageManager.getComponentEnabledSetting(mComponentName1) == 2) {
                    isenable = false;
                } else {
                    isenable = true;
                }
                //??????????????????
                mAutoStartInfo.setLabel(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                //???????????????????????????????????????????????????
                mAutoStartInfo.setPackageName(packageInfo.packageName);
                mAutoStartInfo.setSystem(isSystem);
                mAutoStartInfo.setEnable(isenable);
                mAutoStartInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));

                publishProgress(++mAppCount, packages.size(), packageInfo.packageName, packageInfo.applicationInfo.loadIcon(packageManager));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    if (IConstant.IS_DEBUG) e.printStackTrace();
                }
            }
           /* //?????????????????????????????????
            AppCleanMgr.cleanInternalCache(getApplicationContext());
            //?????????????????????????????????
            AppCleanMgr.cleanExternalCache(getApplicationContext());
            //???????????????files??????
            AppCleanMgr.cleanFiles(getApplicationContext());

            AppCleanMgr.cleanInternalCache(getApplicationContext());*/
            getRunning3rdApp(packageManager);
            ProcessUtils.killAllBackgroundProcesses();

            Set<String> allRunningServices = ServiceUtils.getAllRunningServices();
            //??????3 ?????????for????????????
            for (String value : allRunningServices) {
                ServiceUtils.stopService(value);
                //AppApplicationMgr.stopRunningService(getApplicationContext(), value);
            }
            clean();
            return null;
        }

        public void getRunning3rdApp(PackageManager localPackageManager) {
            List localList = localPackageManager.getInstalledPackages(0);
            for (int i = 0; i < localList.size(); i++) {
                PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
                String str1 = localPackageInfo1.packageName.split(":")[0];
                if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0)
                        && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {
                    killBackgroundProcesses(str1);
                    ProcessUtils.killBackgroundProcesses(str1);
                    publishProgress(++mProgress, str1);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        if (IConstant.IS_DEBUG) e.printStackTrace();
                    }
                }
            }
        }

        public void killBackgroundProcesses(String processName) {
            String packageName;
            try {
                if (processName.indexOf(":") == -1) {
                    packageName = processName;
                } else {
                    packageName = processName.split(":")[0];
                }
                activityManager.killBackgroundProcesses(packageName);
                Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
                forceStopPackage.setAccessible(true);
                forceStopPackage.invoke(activityManager, packageName);
            } catch (Exception e) {
                if (IConstant.IS_DEBUG) e.printStackTrace();
            }
        }


        public synchronized void clean() {
            //To change body of implemented methods use File | Settings | File Templates.
            List<ActivityManager.RunningAppProcessInfo> infoList = activityManager.getRunningAppProcesses();
            List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(100);
            long beforeMem = getAvailMemory();
            if (IConstant.IS_DEBUG) System.out.println(TAG + " before memory info :" + beforeMem);
            int count = 0;
            PackageManager pm = getApplicationContext().getPackageManager();
            if (infoList != null) {
                for (int i = 0; i < infoList.size(); ++i) {
                    ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
//                    LogUtils.iTag(TAG, "process name : " + appProcessInfo.processName);
                    //importance ???????????????????????? ????????????????????????????????????????????????
//                    LogUtils.iTag(TAG, "importance : " + appProcessInfo.importance);
                    // ??????????????????RunningAppProcessInfo.IMPORTANCE_SERVICE?????????????????????????????????????????????
                    // ??????????????????RunningAppProcessInfo.IMPORTANCE_VISIBLE????????????????????????????????????????????????????????????
                    if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        String[] pkgList = appProcessInfo.pkgList;
                        for (int j = 0; j < pkgList.length; ++j) {
                            // LogUtils.iTag(TAG, "It will be killed, package name : " + pkgList[j] + " -- " + appName);
                            activityManager.killBackgroundProcesses(pkgList[j]);
                            count++;
                        }
                    }

                }
            }
            long afterMem = getAvailMemory();
            //System.out.println("----------- after memory info : " + afterMem + "  ,  " + "clear " + count + " process, " + (afterMem - beforeMem) + "M");
            float RAM = (afterMem - beforeMem);
            if (RAM <= 0) {
                RAM = (float) RandomUntil.getNum(1, 20);
            }
            publishProgress((int) RAM);
        }

        /**
         * ???doInBackground?????????publishProgress???????????????????????????????????????????????????
         * ?????????UI?????????
         *
         * @param values
         */
        @SuppressLint("WrongThread")
        @Override
        protected void onProgressUpdate(Object... values) {
            try {
                if (values.length == 1) {

                    int ram = 0;
                    try {
                        ram = (int) values[0];
                    } catch (Exception e) {
                    }
                    scanningClearListenter.onScanProgressUpdated(new ListenterInfo(ram), 1);
                } else if (values.length == 2) {
                    int progress = (int) values[0];
                    String packageName = (String) values[1];
                    scanningClearListenter.onScanProgressUpdated(new ListenterInfo(progress, packageName), 2);
                } else {
                    int current = Integer.parseInt(values[0] + "");
                    int max = Integer.parseInt(values[1] + "");
                    String packageName = (String) values[2];
                    Drawable icon = (Drawable) values[3];
                    byte[] bitmapdata = null;
                    try {
                        if (icon != null) {
                            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                            bitmapdata = stream.toByteArray();
                        }
                    } catch (Exception e) {
                    }

                    float percent = (int) (1.0 * current / max * 100);
                    scanningClearListenter.onScanProgressUpdated(new ListenterInfo(current, packageName, max, percent, bitmapdata), 3);
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            /*try {
                if (scanningClearListenter != null) {
                    scanningClearListenter.onScanProgressUpdated(values);
                }
            } catch (Exception e) {
                if (IConstant.IS_DEBUG) e.printStackTrace();
            }*/

        }


        private long getAvailMemory() {
            // ??????android????????????????????????
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(mi);
            //mi.availMem; ???????????????????????????
            //return Formatter.formatFileSize(context, mi.availMem);// ?????????????????????????????????
            //LogUtils.iTag(TAG, "????????????---->>>" + mi.availMem / (1024 * 1024));
            return mi.availMem / (1024 * 1024);
        }

        /**
         * ???doInBackground????????????????????????doInBackground?????????????????????????????????????????????????????????-????????????UI?????????
         *
         * @param result
         */
        @Override
        protected void onPostExecute(List<AutoStartInfo> result) {
            super.onPostExecute(result);
            try {
                if (scanningClearListenter != null) {
                    scanningClearListenter.onCleanCompleted();
                }
                mIsScanningClear = false;
            } catch (Exception e) {
                if (IConstant.IS_DEBUG) e.printStackTrace();
            }
        }

    }
}
