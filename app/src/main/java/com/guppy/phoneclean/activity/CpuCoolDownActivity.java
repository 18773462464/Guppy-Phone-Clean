package com.guppy.phoneclean.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifDrawable;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
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
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.IConfig;
import com.guppy.phoneclean.widget.CustomSnowView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;

public class CpuCoolDownActivity extends BaseActivity {

    private ImageView speedGif,speedGifBg;
    private TextView executeName,executeStatus;
    private CustomSnowView xueView;
    private GifDrawable gifFromAssets;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cpu_cool_down);
        initView();
        initData();
    }

    private void initView() {
        speedGif = findViewById(R.id.speed_gif);
        speedGifBg = findViewById(R.id.speed_gif_bg);
        xueView = findViewById(R.id.xue_view);
        executeName = findViewById(R.id.execute_name);
        executeStatus = findViewById(R.id.execute_status);
    }

    private void initData(){
        loadBannerAd();
        ObjectAnimator animator = ObjectAnimator.ofFloat(speedGif, "rotation", 0.0f, 360.0f);
        animator.setDuration(1300);
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(speedGifBg, "rotation",0.0f, 360.0f);
        animator2.setDuration(1300);
        animator2.setInterpolator(new LinearInterpolator());//不停顿
        animator2.setRepeatCount(-1);//设置动画重复次数
        animator2.start();

        executeName.setText("Hold on...");

        //检查手机温度
        executeStatus.setText(R.string.string_check_temperature);
        new IHandler(new IHandlerCallcack() {
            @Override
            public void onCoolDownEnd() {
                animator.cancel();
                animator2.cancel();
                xueView.setVisibility(View.GONE);
                executeName.setText(getString(R.string.string_completed));
                //executeStatus.setVisibility(View.INVISIBLE);
                String str = String.format(getResources().getString(R.string.string_cooled), IConstant.temperature_increase);
                executeStatus.setText(str);
                //speedGif.setVisibility(View.INVISIBLE);
                if (!isFinishing()) {
                    IConstant.popAd.show1(new AdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();
                            bundle.putString("TYPE", "COOLDOWN");
                            startActivitys(AllEndActivity.class, bundle);
                            finish();
                        }
                    });
                    /*SysConfig sysConfig = DbUtils.getConfig();
                    showLoading();
                    if (sysConfig.getType() == 1){
                        new Pop(CpuCoolDownActivity.this,new AdListener(){
                            @Override
                            public void onClose() {
                                super.onClose();
                                closeLoading();
                                bundle.putString("TYPE", "COOLDOWN");
                                startActivitys(AllEndActivity.class, bundle);
                                finish();
                            }
                        }).load();
                    }else {
                        new PanglePop(CpuCoolDownActivity.this,new PangleAdListener(){
                            @Override
                            public void onClose() {
                                super.onClose();
                                closeLoading();
                                bundle.putString("TYPE", "COOLDOWN");
                                startActivitys(AllEndActivity.class, bundle);
                                finish();
                            }
                        }).loadPopAd();
                    }*/

                }
            }

            @Override
            public void onNextCoolDown(IHandler handler) {
                executeName.setText(getResources().getString(R.string.string_stoptime_consuming));
                xueView.setVisibility(View.VISIBLE);
                executeStatus.setText(R.string.string_cooldown_temperature);


                new Thread(() -> {
                    killOthers(CpuCoolDownActivity.this);
                    handler.sendEmptyMessageDelayed(MSG_CODE_COOL,3000);
                }).start();
            }

            @Override
            public void onReadBatttery() {
                //CPU散热
                int[] battery = IConfig.getSingleton().getBattery(CpuCoolDownActivity.this);
                int batteryNumber = (battery[0] / 10);
                int batteryNumberAll = IConstant.temperature_increase + batteryNumber;
                String str = String.format(getResources().getString(R.string.string_temperature), batteryNumberAll);
                executeName.setText(str);
            }
        }).sendEmptyMessageDelayed(MSG_CODESTART, 500);
    }

    public interface IHandlerCallcack {
        void onCoolDownEnd();

        void onNextCoolDown(IHandler handler);

        void onReadBatttery();
    }

    static final int MSG_CODESTART = 0x22;
    static final int MSG_CODE_COOL = 0x23;
    private static class IHandler extends Handler {
        private final IHandlerCallcack iHandlerCallcack;

        public IHandler(IHandlerCallcack iHandlerCallcack) {
            this.iHandlerCallcack = iHandlerCallcack;
        }

        private int READBATTERY = 0;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODESTART) {
                if (hasMessages(MSG_CODESTART)) removeMessages(MSG_CODESTART);
                if (READBATTERY >= 5) {
                    READBATTERY = 0;
                    if (iHandlerCallcack != null) {
                        iHandlerCallcack.onNextCoolDown(this);
                    }
                } else {
                    READBATTERY++;
                    if (iHandlerCallcack != null) {
                        iHandlerCallcack.onReadBatttery();
                    }
                    sendEmptyMessageDelayed(MSG_CODESTART, 1000);
                }
            }else if (msg.what == MSG_CODE_COOL){
                if (hasMessages(MSG_CODE_COOL)) removeMessages(MSG_CODE_COOL);
                if (iHandlerCallcack != null) {
                    iHandlerCallcack.onCoolDownEnd();
                }
            }
        }
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

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
*/


    /**

     * 杀死其他正在运行的程序

     *

     * @param context

     */

    private void killOthers(Context context) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {

            ApplicationInfo applicationInfo = null;
            /*try {
                applicationInfo = packageManager.getPackageInfo(info.processName, 0).applicationInfo;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }*/
            if (applicationInfo!=null){
                if (!getPackageName().equals(info.processName)&&filterApp(applicationInfo)) {
                    System.out.println(info.processName + "---" + info.pid);
                    //Process.killProcess(info.pid);

                    /*killProcess(info.pid);
                    killProcess1(info.processName);*/
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        forceStopPackage(info.processName,context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else {
                if (!getPackageName().equals(info.processName)&&filterApp(applicationInfo)) {
                    System.out.println(info.processName + "---" + info.pid);
                    //Process.killProcess(info.pid);
                    /*killProcess(info.pid);
                    killProcess1(info.processName);*/
                    try {
                        forceStopPackage(info.processName,context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**

     *强制停止应用程序

     * @param pkgName

     */

    private void forceStopPackage(String pkgName,Context context) throws Exception{
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
        method.invoke(am, pkgName);

    }

    /**

     * 判断某个应用程序是 不是三方的应用程序

     * @param info

     * @return

     */

    public boolean filterApp(ApplicationInfo info) {
        if (info ==null)return true;
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }






    private void killProcess(int processId) {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write(("kill " + processId).getBytes());
            os.flush();
            os.close();
            Thread.sleep(3000);//这里的sleep的目的让上面的kill命令执行完成
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
        } finally {
            if (process != null) {
                process.destroy();
                process = null;
            }
        }
    }

    private void killProcess1(String packageName) {
        java.lang.Process process = null;
        try {
            String processId = "";
            process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write("ps \n".getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if (inline.contains(packageName)) {
                    StringTokenizer processInfoTokenizer = new StringTokenizer(inline);
                    int count = 0;
                    while (processInfoTokenizer.hasMoreTokens()) {
                        count++;
                        processId = processInfoTokenizer.nextToken();
                        if (count == 2) {
                            break;
                        }
                    }
                    os.write(("kill " + processId).getBytes());
                    os.flush();
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                    br.close();
                    return;
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        } finally {
            if (process != null) {
                process.destroy();
                process = null;
            }
        }

    }
}