package com.guppy.phoneclean.service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.guppy.phoneclean.ClearServicesResule;
import com.guppy.phoneclean.ClearServicesResuleCallBack;
import com.guppy.phoneclean.IClearApplication;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.MainActivity;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ScanningClearListenter;
import com.guppy.phoneclean.activity.CacheClearActivity;
import com.guppy.phoneclean.activity.OverActivity;
import com.guppy.phoneclean.dialog.InstallUninstallDialog;
import com.guppy.phoneclean.model.NotificationMsg;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.IConfig;
import com.guppy.phoneclean.utils.IObserver;
import com.guppy.phoneclean.utils.MMKVUtil;
import com.guppy.phoneclean.utils.RandomUntil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


public class ServicesMangerHolder {
    private static final String TAG = "ServicesMangerHolder";
    private static ClearServicesResule clearServicesResule;
    private Context application;
    private volatile static ServicesMangerHolder instance;
    private final Handler handler;

    private ServicesMangerHolder() {
        handler = new Handler(Looper.myLooper(), this::handleMessage);
    }

    public static ServicesMangerHolder getSingleton() {
        if (instance == null) {
            synchronized (ServicesMangerHolder.class) {
                if (instance == null) {
                    instance = new ServicesMangerHolder();
                }
            }
        }
        return instance;
    }


    public void initialize() {
        /*//需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);*/
        Intent clearServices = new Intent(IClearApplication.getContext(), CleanerService.class);
        IClearApplication.getContext().bindService(clearServices, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (IConstant.IS_DEBUG) Log.d(TAG, "CleanerService onServiceConnected");
                clearServicesResule = ClearServicesResule.Stub.asInterface(service);

                if (clearServicesResule != null) {
                    try {
                        clearServicesResule.setServicesListenterResult(new ClearServicesResuleCallBack.Stub() {
                            @Override
                            public void onBindServices() throws RemoteException {
                                if (IConstant.IS_DEBUG)
                                    Log.d(TAG, "CleanerService onBindServices  start IServiceImpl");
                                if (handler != null) handler.obtainMessage(0).sendToTarget();
                            }

                            @Override
                            public void onCheckApplicationWhite() throws RemoteException {
                                if (IConstant.IS_DEBUG)
                                    Log.d(TAG, "CleanerService onCheckApplicationWhite");
                                if (handler != null) handler.obtainMessage(1).sendToTarget();
                            }

                            @Override
                            public void onSendNotificationData() throws RemoteException {
                                if (IConstant.IS_DEBUG)
                                    Log.d(TAG, "CleanerService onSendNotificationData");
                                if (handler != null) handler.obtainMessage(2).sendToTarget();
                            }
                        });
                    } catch (RemoteException e) {
                        if (IConstant.IS_DEBUG)
                            Log.d(TAG, "setServicesListenterResult error:" + e.toString());
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (IConstant.IS_DEBUG) Log.d(TAG, "CleanerService onServiceDisconnected");
                clearServicesResule = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    boolean handleMessage(@NonNull Message msg) {
        if (msg.what == 0) {
            if (!IConstant.isRuningIClearServices) {
                try {
                    Intent mForegroundService = new Intent(IClearApplication.getContext(), ITraceServiceImpl.class);
                    //适配8.0以上的服务转前台服务 清单文件AndroidManifest中有配置 android.permission.FOREGROUND_SERVICE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //适配8.0机制
                        IClearApplication.getContext().startForegroundService(mForegroundService);
                    } else {
                        IClearApplication.getContext().startService(mForegroundService);
                    }
                } catch (Exception e) {
                }
            }
        } else if (msg.what == 1) {
            if (IConstant.IS_DEBUG) System.out.println("onCheckApplicationWhite handleMessage");
            IObserver.getInstance().post(IObserver.ObserverType.WHITE_OPEN_TIP);
        } else if (msg.what == 2) {
            List<NotificationMsg> messageData = getMessageData();
            if (messageData != null) {
                if (IConstant.IS_DEBUG)
                    System.out.println("发送随机通知4:" + messageData.size());
                for (int i = 0; i < messageData.size(); i++) {
                    NotificationMsg notificationMsg = messageData.get(i);
                    if (notificationMsg != null) {

                        int id = notificationMsg.getId();
                        String title = notificationMsg.getTitle();
                        String body = notificationMsg.getBody();
                        boolean servicesRun = !isAppForeground();
                        if (IConstant.IS_DEBUG)
                            System.out.println("  notificationManager - id:" + id + " , " + servicesRun + " ,title:" + title + " ,body:" + body);
                        if (servicesRun && title != null && body != null && !title.isEmpty() && !body.isEmpty()) {
                            if (id == 10001) {
                                if (IConstant.IS_DEBUG) System.out.println("10001 - body:" + body);
                                //释放存储空间
                                sendMessages(title, body, id);

                            } else if (id == 10002) {
                                float percent = AppUtils.getPercent(IClearApplication.getContext());
                                if (IConstant.IS_DEBUG)
                                    System.out.println("10002 - body:" + body + " , percent:" + percent);
                                if (percent > 50) {
                                    String s = body.replaceAll("%d", percent + "");
                                    //提升手机性能
                                    sendMessages(title, s, id);
                                }
                            } else if (id == 10003) {
                                //CPU散热
                                int[] battery = IConfig.getSingleton().getBattery(IClearApplication.getContext());
                                if (IConstant.IS_DEBUG)
                                    System.out.println("10003 - body:" + body + " , battery:" + (battery[1] / 10));
                                IConstant.temperature_increase = RandomUntil.getNum(3, 7);
                                int batteryNumber = (battery[0] / 10);
                                int batteryNumberAll = IConstant.temperature_increase + batteryNumber;
                                String s = body.replaceAll("%d", batteryNumberAll + "");
                                sendMessages(title, s, id);
                            } else if (id == 10004) {
                                //低电量
                                int[] battery = IConfig.getSingleton().getBattery(IClearApplication.getContext());
                                if (battery[0] <= 30) {
                                    String s = body.replaceAll("%d", battery[0] + "");
                                    sendMessages(title, s, id);
                                }
                            }
                        }
                    }
                }
            }

        }
        return true;
    }

    private List<NotificationMsg> getMessageData(){
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig != null){
            JSONArray array = sysConfig.getNotification_msg();
            List<NotificationMsg> list = new ArrayList<>();
            try {
                for (int i = 0, leg = array.length(); i < leg; i++) {
                    NotificationMsg item = new NotificationMsg().parser(array.getString(i));
                    list.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }else {
            return null;
        }
    }

    /**
     * 判断当前应用是否处于前台
     */
    private boolean isAppForeground() {
        ActivityManager activityManager = (ActivityManager) IClearApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        /**
         * 存活的App进程
         */
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
            if (appProcess.processName == IClearApplication.getContext().getPackageName() && (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                return true;
            }
        }
        return false;
    }

    //private int NOTI_CODE_SMS = 2;
    private void sendMessages(String title, String message, int code_id/*, int postion*/) {
        //StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (IConstant.IS_DEBUG)
            System.out.println("发送随机通知5  title：" + title + "   " + Thread.currentThread().getName());
        NotificationManager notificationManager = (NotificationManager) IClearApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancel(NOTI_CODE_SMS);
        //notificationManager.cancel(code);
        String notificationChannelId = "notification_channel_id_clearing_tip";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "APPCLEAR";
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent homeIntent = new Intent(IClearApplication.getContext(), OverActivity.class);
        if (code_id == 10001) {
            //释放存储空间
            MMKVUtil.putNotifMessages(IConstant.ACTION_CACHE,message);
        } else if (code_id == 10002) {
            //提升手机性能
            MMKVUtil.putNotifMessages(IConstant.ACTION_SPEED,message);
        } else if (code_id == 10003) {
            //CPU散热
            MMKVUtil.putNotifMessages(IConstant.ACTION_COOLDOWN,message);
        } else if (code_id == 10004) {
            //低电量
            MMKVUtil.putNotifMessages(IConstant.ACTION_BATTERY,message);
        }
        SysConfig sysConfig = DbUtils.getConfig();
        if(sysConfig.getNotification() == 2){
            showDialog(IClearApplication.getContext(), "packageName", "install successfully."/*应用安装*/, "App install,Check for sensitive permissions."/*应用安装，检查敏感权限*/, "added", "EXIT", "CHECK FOR");
            return;
        }
        PendingIntent pendingHomeIntent = PendingIntent.getActivity(IClearApplication.getContext(), 0, homeIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(IClearApplication.getContext().getApplicationContext(), notificationChannelId)
                .setSmallIcon(R.mipmap.ic_small_icon)
                .setAutoCancel(false)
                //.setAutoCancel(true)
                .setOngoing(false)
                .setShowWhen(true)
                //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_large_icon)) // 设置下拉列表中的图标(大图标)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingHomeIntent)
                .setFullScreenIntent(pendingHomeIntent, true)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        notificationManager.notify(code_id, builder.build());
    }

    public void clearTaskStart(ScanningClearListenter scanningClearListenter) {
        if (IConstant.IS_DEBUG)
            System.out.println("startScanningClear   clearTaskStart:" + (clearServicesResule == null));
        if (clearServicesResule != null) {
            try {
                if (IConstant.IS_DEBUG) System.out.println("startScanningClear   clearTaskStart");
                clearServicesResule.startScanningClear(scanningClearListenter);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private InstallUninstallDialog alertDialog;

    public void showDialog(Context context, String packageName, String name, String detailed, String type, String btn1, String btn2) {
        try {
            if (alertDialog == null) {
                alertDialog = new InstallUninstallDialog.Builder(context)
                        .setTitle(name)
                        .setCance(true)
                        .setText(detailed)
                        .setCancelButton(btn1/*稍后处理*/, dialog -> alertDialog = null)
                        .setConfirmButton(btn2/*立即处理*/, new InstallUninstallDialog.IDialogListenter() {
                            @Override
                            public void onClick(Dialog dialog) {
                                alertDialog = null;
                                if (type.equals("added")) {
                                    Intent i1 = new Intent(context, MainActivity.class);
                                    i1.putExtra("PACK", packageName);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i1);
                                } else if (type.equals("removed")) {
                                    Intent i1 = new Intent(context, CacheClearActivity.class);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i1);
                                } else {
                                    Intent i1 = new Intent(context, MainActivity.class);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i1);
                                }
                            }
                        }).create();
            }
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
