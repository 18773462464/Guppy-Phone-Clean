package com.guppy.phoneclean.model;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.guppy.phoneclean.utils.Score;
import com.jaredrummler.android.processes.AndroidProcesses;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public class ClearViewModel extends BaseViewModel {
    public String TYPE_SCANNING = "TYPE_SCANNING";
    public String TYPE_CLEARING = "TYPE_CLEARING";

    public int getScore(Context context) {
        return Score.getSingleton().allScore(context);
    }

    private long beforeMemory = 0;
    private long endMemory = 0;

    public void startClearUp(@NotNull Activity activity, Handler handler, String stringType) {
        if (stringType.equals(TYPE_SCANNING)) {
            if (handler != null) handler.sendEmptyMessageDelayed(1, 3000);
        } else if (stringType.equals(TYPE_CLEARING)) {
            new Thread(() -> {
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                try {
                    ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                    activityManager.getMemoryInfo(memoryInfo);

                    beforeMemory = memoryInfo.availMem;
                    List<ActivityManager.RunningAppProcessInfo> appProcessList = AndroidProcesses.getRunningAppProcessInfo(activity);
                    for (ActivityManager.RunningAppProcessInfo info : appProcessList) {
                        killBackgroundProcesses(info.processName, activityManager);
                    }
                    activityManager.getMemoryInfo(memoryInfo);
                    endMemory = memoryInfo.availMem;
                    Thread.sleep(2000);
                    long size = Math.abs(endMemory - beforeMemory);
                    //if (handler != null) handler.sendEmptyMessageDelayed(2, 3000);
                    if (handler != null) handler.obtainMessage(2, size).sendToTarget();
                } catch (Exception e) {
                    if (handler != null) {
                        Message message = handler.obtainMessage(2, -1);
                        handler.sendMessageDelayed(message, 3000);
                    }
                }
            }).start();
        }
    }


    public void killBackgroundProcesses(String processName, ActivityManager activityManager) {
        // mIsScanning = true;
        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }
            activityManager.killBackgroundProcesses(packageName);
            //app使用FORCE_STOP_PACKAGES权限，app必须和这个权限的声明者的签名保持一致！
            Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}