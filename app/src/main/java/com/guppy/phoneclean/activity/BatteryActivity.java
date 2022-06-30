package com.guppy.phoneclean.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.utils.BatteryListener;
import com.guppy.phoneclean.utils.IConfig;
import com.guppy.phoneclean.widget.Battery;

public class BatteryActivity extends BaseActivity {

    private BatteryListener listener;
    private Battery battery;
    private TextView tipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        initView();
    }

    protected void initView() {
        battery = findViewById(R.id.battery);
        tipText = findViewById(R.id.tip_text);

        int[] batterys = IConfig.getSingleton().getBattery(BatteryActivity.this);
        battery.setPower(batterys[0]);
        tipText.setText("");
        if (batterys[0] <= 30) {
            tipText.setText(R.string.string_low_battery);
        }
        listener = new BatteryListener(this);

        // 设置属性动画并播放
        ObjectAnimator animator = ObjectAnimator.ofInt(battery, "power", 0, 100);
        animator.setDuration(10000);
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        listener.register(new BatteryListener.BatteryStateListener() {
            @Override
            public void onStateChanged() { //电量发生改变
                int[] batterys = IConfig.getSingleton().getBattery(BatteryActivity.this);
                battery.setPower(batterys[0]);
            }

            @Override
            public void onStateLow() { //电量低
                if (batterys[0] <= 30) {
                    tipText.setText(R.string.string_low_battery);
                }
            }

            @Override
            public void onStateOkay() { //电量充满
                battery.setPower(100);
                tipText.setText(R.string.string_finished_battery);
                if (animator.isRunning()) {
                    animator.cancel();
                }
            }

            @Override
            public void onStatePowerConnected() { //接通电源

                tipText.setText(R.string.string_charging_battery);
                animator.start();
            }

            @Override
            public void onStatePowerDisconnected() { //拔出电源
                if (batterys[0] <= 30) {
                    tipText.setText(R.string.string_low_battery);
                }
                if (animator.isRunning()) {
                    animator.cancel();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener!=null) listener.unregister();
    }
}