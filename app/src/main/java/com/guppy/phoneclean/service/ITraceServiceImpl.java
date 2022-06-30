package com.guppy.phoneclean.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.blankj.utilcode.util.NotificationUtils;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.activity.OverActivity;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.IObserver;
import com.guppy.phoneclean.utils.MMKVUtil;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ITraceServiceImpl extends Service {

    private static final String TAG = "TraceServiceImpl";
    private String[] itemString = {IConstant.ACTION_HOME, IConstant.ACTION_HOME, IConstant.ACTION_CACHE, IConstant.ACTION_COOLDOWN, IConstant.ACTION_BATTERY, IConstant.ACTION_SPEED};
    public ButtonBroadcastReceiver bReceiver;
    private boolean isRungNotification = false;
    private int NotificationStartId = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            initButtonReceiver();
            if (IConstant.IS_DEBUG) System.out.println("startWork - startId:" + startId);
            Notification notification = createForegroundNotification();
            NotificationStartId = startId;
            if (!NotificationUtils.areNotificationsEnabled()) {
                if (isRungNotification) {
                    stopForeground(true);
                }
                isRungNotification = false;
            } else {
                startForeground(NotificationStartId, notification);
                isRungNotification = true;
            }
            if (IConstant.IS_DEBUG)
                System.out.println(TAG + "  startForeground--：" + isRungNotification);
            startTimer(notification, startId);
        } catch (Exception e) {
        }
        IConstant.isRuningIClearServices = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Disposable disposable;

    /**
     * 开始定时执行
     */
    private void startTimer(Notification notification, int startId) {
        stopTimer();
        // 每隔1000毫秒执行一次逻辑代码
        SysConfig sysConfig = DbUtils.getConfig();
        disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                //.doOnDispose(AbsWorkService::cancelJobAlarmSub)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        //if (IConstant.IS_DEBUG)System.out.println("startForeground--：" + isRungNotification);
                        if (!NotificationUtils.areNotificationsEnabled()) {
                            if (isRungNotification) {
                                ITraceServiceImpl.this.stopForeground(true);
                            }
                            isRungNotification = false;
                        } else {
                            if (!isRungNotification) {
                                ITraceServiceImpl.this.startForeground(startId, notification);
                                if (!NotificationUtils.areNotificationsEnabled()) {
                                    isRungNotification = false;
                                } else {
                                    isRungNotification = true;
                                }
                            }
                        }

                        int interval = sysConfig.getInterval();
                        if (IConstant.IS_DEBUG) interval = 2*60;
                        int time = interval;
                        if (aLong == 0) {
                            if (IConstant.IS_DEBUG) System.out.println("发生消息检测白名单");
                            //发生消息检测白名单
                            IObserver.getInstance().postSM(IObserver.ObserverTypePro.CHEACK_WHITE_OPEN);
                            return;
                        }
                        if (aLong % time == 0) {
                            //int notification = ((IClearApplication) getApplication()).getNotification();

                            boolean openNotification = ITraceServiceImpl.this.isOpenNotification();
                            if (openNotification) {
                                if (IConstant.IS_DEBUG)
                                    System.out.println("未打开通知开关--后端控制啊：" + notification);
                                return;
                            }
                            //发送随机通知
                            if (IConstant.IS_DEBUG) System.out.println("发送随机通知");
                            IObserver.getInstance().postSM(IObserver.ObserverTypePro.ACTION_TYPE_SEED_OR_ANTIVITRUE_OR_CPUCOOLDOWN);
                        }
                    }
                }, throwable -> {
                    if (IConstant.IS_DEBUG)
                        System.out.println("startTimer--Error：重新 startTimer())");
                    startTimer(notification, startId);
                });
    }

    public boolean isOpenNotification() {
        //0 关闭提醒, 1 通知栏提醒，2 弹框提醒
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig != null) {
            return sysConfig.getNotification() == 0;
        }
        return false;
    }

    /**
     * 初始化广播，需要在Service或者Activity开始的时候就调用
     */
    private void initButtonReceiver() {
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IConstant.ACTION_NOTIFICATION);
        /*intentFilter.addAction(IConstant.ACTION_HOME);
        intentFilter.addAction(IConstant.ACTION_CACHE);
        intentFilter.addAction(IConstant.ACTION_COOLDOWN);
        intentFilter.addAction(IConstant.ACTION_BATTERY);*/
        registerReceiver(bReceiver, intentFilter);
    }

    /**
     * 停止定时执行
     */
    protected void stopTimer() {
        if (null != disposable) {
            disposable.dispose();
            disposable = null;
        }
    }


    /**
     * 按钮点击广播
     */
    public class ButtonBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IConstant.IS_DEBUG) System.out.println(TAG + " action：" + action);
            if (action != null && action.equals(IConstant.ACTION_NOTIFICATION)) {
                int actionsJump = intent.getIntExtra("COME_CODE", 0);
                if (IConstant.IS_DEBUG)
                    System.out.println(TAG + " actionsJump：" + actionsJump + "  " + itemString[actionsJump]);
                MMKVUtil.putNotifMessages(itemString[actionsJump],"");
                Intent i1 = new Intent(context, OverActivity.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(i1);
            }
            NotificationUtils.setNotificationBarVisibility(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        IConstant.isRuningIClearServices = false;
        try {
            if (bReceiver != null) unregisterReceiver(bReceiver);
            stopTimer();
            if (IConstant.IS_DEBUG) System.out.println(TAG + "  stopWork");
        } catch (Exception e) {
        }
    }


    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.item_clear_notification);
        Intent intent = new Intent(IConstant.ACTION_NOTIFICATION);
        intent.putExtra("COME_CODE", 0);
        PendingIntent pendingHomeIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        intent.putExtra("COME_CODE", 1);
        PendingIntent intent_iv1 = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.action_home, intent_iv1);

        intent.putExtra("COME_CODE", 2);
        PendingIntent intent_iv2 = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.action_clear, intent_iv2);

        intent.putExtra("COME_CODE", 3);
        PendingIntent intent_iv3 = PendingIntent.getBroadcast(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.action_cooldown, intent_iv3);

        intent.putExtra("COME_CODE", 4);
        PendingIntent intent_iv4 = PendingIntent.getBroadcast(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.action_battery, intent_iv4);

        String notificationChannelId = "notification_channel_id_clearing";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "APPCLEAR";
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }

        return new NotificationCompat.Builder(getApplicationContext(), notificationChannelId)
                .setSmallIcon(R.mipmap.ic_small_icon)
                .setAutoCancel(false)
                .setOngoing(true)
                .setShowWhen(false)
                //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_large_icon))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                //.setContentTitle("正在守护您的设备...")
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setContentIntent(pendingHomeIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
    }
}