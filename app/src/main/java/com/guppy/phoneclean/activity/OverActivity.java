package com.guppy.phoneclean.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.MainActivity;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.PangleAdListener;
import com.guppy.phoneclean.ad.PanglePop;
import com.guppy.phoneclean.ad.Pop;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.MMKVUtil;

import java.util.List;

public class OverActivity extends BaseActivity {

    private static final String TAG = "OverActivity";
    private String action;
    private TextView executeName;
    private ProgressBar executeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over);
        initView();
    }

    protected void initView() {
        loadBannerAd();
        executeName = findViewById(R.id.execute_name);
        executeStatus = findViewById(R.id.execute_status);
        action = MMKVUtil.getNotifMessages(0);
        if (IConstant.IS_DEBUG) System.out.println("over action:" + action);
        if (action.equals(IConstant.ACTION_HOME)) {
            executeName.setText(getResources().getString(R.string.string_over_open_main));
        } else if (action.equals(IConstant.ACTION_CACHE)) {
            //释放存储空间
            executeName.setText(getResources().getString(R.string.tring_over_open_cache));
        } else if (action.equals(IConstant.ACTION_COOLDOWN)) {
            //CPU散热
            executeName.setText(getResources().getString(R.string.string_over_open_cool_down));
        } else if (action.equals(IConstant.ACTION_BATTERY)) {
            //低电量
            executeName.setText(getResources().getString(R.string.string_over_open_battery));
        } else if (action.equals(IConstant.ACTION_SPEED)) {
            //提升手机性能
            executeName.setText(getResources().getString(R.string.string_over_open_speed));
        }
        executeStatus.setMax(5);

        MMKVUtil.putNotifMessages("", "");
        new IHandler(new IHandlerCallcack() {
            @Override
            public void onLoadEnd() {
                jumpActivity();
            }

            @Override
            public void onLoadProgress(int progress) {
                executeStatus.setProgress(progress);
            }
        }).obtainMessage(MSG_CODE).sendToTarget();
    }


    public interface IHandlerCallcack {
        void onLoadEnd();

        void onLoadProgress(int progress);
    }

    static final int MSG_CODE = 0x22;

    private static class IHandler extends Handler {
        private final IHandlerCallcack iHandlerCallcack;

        public IHandler(IHandlerCallcack iHandlerCallcack) {
            this.iHandlerCallcack = iHandlerCallcack;
        }

        private int timesCount = 0;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE) {
                if (hasMessages(MSG_CODE)) removeMessages(MSG_CODE);
                if (timesCount >= 5) {
                    timesCount = 0;
                    if (iHandlerCallcack != null) {
                        iHandlerCallcack.onLoadEnd();
                    }
                    /*if (checkPermissions()) {
                        jumpActivity(openIds);
                    }*/
                } else {
                    timesCount++;
                    if (iHandlerCallcack != null) {
                        iHandlerCallcack.onLoadProgress(timesCount);
                    }
                    /*executeStatus.setProgress(times);*/
                    sendEmptyMessageDelayed(MSG_CODE, 1000);
                }
            }
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
        if (IConstant.IS_DEBUG)
            System.out.println(TAG + "  onPermissionsGranted   requestCode：" + requestCode + " ，list：" + list.toString());
        if (requestCode == 2022) {
            jumpActivity();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        if (IConstant.IS_DEBUG)
            System.out.println(TAG + "  onPermissionsDenied   requestCode：" + requestCode + " ，list：" + list.toString());
        if (requestCode == 2022) {
            jumpActivity();
        }
    }

    private void jumpActivity() {
        IConstant.popAd.show1(new AdListener(){
            @Override
            public void onClose() {
                super.onClose();
                jump();
            }
        });
        /*showLoading();
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig.getType() == 1){
            new Pop(this,new AdListener(){
                @Override
                public void onClose() {
                    super.onClose();
                    closeLoading();
                    jump();
                }
            }).load();
        }else {
            new PanglePop(this,new PangleAdListener(){
                @Override
                public void onClose() {
                    super.onClose();
                    closeLoading();
                    jump();
                }
            }).loadPopAd();
        }*/

    }

    private void jump(){
        //防止不会到主界面--这个方法欠妥当--临时处理办法
        startActivitys(MainActivity.class, null);
        if (IConstant.IS_DEBUG) System.out.println(TAG + "  action == " + action);

        if (action.equals(IConstant.ACTION_HOME)) {
            startActivitys(MainActivity.class, null);
            finish();
        } else if (action.equals(IConstant.ACTION_CACHE)) {
            //释放存储空间
            startActivitys(CacheClearActivity.class, null);
            finish();
        } else if (action.equals(IConstant.ACTION_COOLDOWN)) {
            //CPU散热
            startActivitys(CpuCoolDownActivity.class, null);
            finish();
        } else if (action.equals(IConstant.ACTION_BATTERY)) {
            //低电量
            startActivitys(BatteryActivity.class, null);
            finish();
        } else if (action.equals(IConstant.ACTION_SPEED)) {
            //提升手机性能
            startActivitys(SpeedActivity.class, null);
            finish();
        }
    }

}