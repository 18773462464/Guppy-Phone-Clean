package com.guppy.phoneclean.activity;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ScanningClearListenter;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.PangleAdListener;
import com.guppy.phoneclean.ad.PanglePop;
import com.guppy.phoneclean.ad.Pop;
import com.guppy.phoneclean.bean.ListenterInfo;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.service.ServicesMangerHolder;
import com.guppy.phoneclean.utils.DbUtils;

import java.io.IOException;

public class SpeedActivity extends BaseActivity {

    private ImageView speed_gif1,speed_gif2,apsLogo,imgSpeed;
    private TextView executeName,executeStatus;
    private FrameLayout img_scanning;
    private ObjectAnimator animator,animator1;
    private GifDrawable gifFromAssets;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_speed);
        initView();
        initData();
    }

    void initView() {
        speed_gif1 = findViewById(R.id.speed_gif1);
        speed_gif2 = findViewById(R.id.speed_gif2);
        apsLogo = findViewById(R.id.aps_logo);
        imgSpeed = findViewById(R.id.img_speed);
        executeName = findViewById(R.id.execute_name);
        executeStatus = findViewById(R.id.execute_status);
        img_scanning = findViewById(R.id.img_scanning);
    }

    void initData() {
        loadBannerAd();
        startAnimator();
        ServicesMangerHolder.getSingleton().clearTaskStart(new ScanningClearListenter.Stub() {
            @Override
            public void onCleanStarted() throws RemoteException {
                apsLogo.post(() -> {
                    //准备扫描
                    apsLogo.setVisibility(View.INVISIBLE);
                    //executeName.setVisibility(View.INVISIBLE);
                    executeStatus.setText(R.string.string_ready_scan);
                    executeName.setText(" ");
                });

            }

            @Override
            public void onScanProgressUpdated(ListenterInfo listenterInfo, int type) throws RemoteException {
                if (!isFinishing() && listenterInfo != null) {
                    if (type == 1) {
                        int ramSize = listenterInfo.getRamSize();
                        //扫描优化完成
                        bundle.putInt("SIZE", ramSize);
                        String str = String.format(getResources().getString(R.string.string_free_memory), listenterInfo.getRamSize());
                        executeStatus.post(() -> {
                            executeStatus.setText(str);
                            apsLogo.setVisibility(View.GONE);
                            imgSpeed.setImageResource(R.mipmap.ic_ql);
                            //executeName.setVisibility(View.INVISIBLE);
                            executeName.setText(getResources().getString(R.string.string_free_complete));
                        });
                    } else if (type == 2) {
                        //正在优化
                        int current = listenterInfo.getProgress();
                        String packageName = listenterInfo.getPackageName();
                        executeStatus.post(() -> {
                            apsLogo.setVisibility(View.GONE);
                            //executeName.setVisibility(View.VISIBLE);
                            executeStatus.setText(getResources().getString(R.string.string_ptimized) + current + "\n" + packageName);
                            executeName.setText(getResources().getString(R.string.string_show_optimizing));
                            //imgSpeed.setImageResource(R.mipmap.ic_ql);
                        });
                    } else {
                        int current = listenterInfo.getProgress();
                        int max = listenterInfo.getMax();
                        float percent = listenterInfo.getPercent();
                        String packageName = listenterInfo.getPackageName();
                        byte[] photoByte = listenterInfo.getIcon();

                        executeStatus.post(() -> {
                            apsLogo.setVisibility(View.VISIBLE);
                            //executeName.setVisibility(View.VISIBLE);
                            //正在扫描
                            executeStatus.setText(getResources().getString(R.string.string_scanning) + current + "/" + max + "  ..." + percent + "%\n" + packageName);
                            if (photoByte != null && photoByte.length > 0) {
                                //Contact对象的图像
                                Bitmap map = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                                /*BitmapDrawable bd = new BitmapDrawable(map);
                                apsLogo.setBackgroundDrawable(bd);*/
                                Glide.with(SpeedActivity.this).load(map).into(apsLogo);
                            }
                            executeName.setText(getResources().getString(R.string.tring_show_scaning));
                        });
                    }
                }
            }

            @Override
            public void onCleanCompleted() throws RemoteException {
                Log.i(TAG, "onCleanCompleted: ");
                //结束了，去跳转吧
                if (!isFinishing()) {
                    //gifFromAssets.stop();
                    animator.setDuration(0);
                    animator1.setDuration(0);
                    executeName.setVisibility(View.VISIBLE);
                    executeName.setText(getString(R.string.string_completed));
                    //((ValueAnimator) animator).reverse();
                    animator1.reverse();

                    if (!isFinishing()) {
                        bundle.putString("TYPE", "SPEED");
                        //startActivitys(AllEndActivity.class, bundle);
                        IConstant.popAd.show1(new AdListener(){
                            @Override
                            public void onClose() {
                                super.onClose();
                                startActivitys(AllEndActivity.class, bundle);
                                finish();
                            }
                        });
                        /*showLoading();
                        SysConfig sysConfig = DbUtils.getConfig();
                        if (sysConfig.getType() == 1){
                            new Pop(SpeedActivity.this,new AdListener(){
                                @Override
                                public void onClose() {
                                    super.onClose();
                                    closeLoading();
                                    startActivitys(AllEndActivity.class, bundle);
                                    finish();
                                }
                            }).load();
                        }else {
                            new PanglePop(SpeedActivity.this,new PangleAdListener(){
                                @Override
                                public void onClose() {
                                    super.onClose();
                                    closeLoading();
                                    startActivitys(AllEndActivity.class, bundle);
                                    finish();
                                }
                            }).loadPopAd();
                        }*/
                    }


                }
            }
        });
    }

    private void startAnimator() {
        animator = ObjectAnimator.ofFloat(speed_gif2, "rotation", 0.0f, 360.0f);
        animator.setDuration(1600);
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.start();

        animator1 = ObjectAnimator.ofFloat(speed_gif1, "rotation", 0.0f, -360.0f);
        animator1.setDuration(1600);
        animator1.setInterpolator(new LinearInterpolator());//不停顿
        animator1.setRepeatCount(-1);//设置动画重复次数
        animator1.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gifFromAssets != null) {
            if (gifFromAssets.isRunning()) {
                gifFromAssets.stop();
            }
            gifFromAssets = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}