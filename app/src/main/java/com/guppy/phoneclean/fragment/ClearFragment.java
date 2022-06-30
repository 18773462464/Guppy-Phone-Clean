package com.guppy.phoneclean.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.activity.AllEndActivity;
import com.guppy.phoneclean.activity.AppMangerActivity;
import com.guppy.phoneclean.activity.CacheClearActivity;
import com.guppy.phoneclean.activity.CpuCoolDownActivity;
import com.guppy.phoneclean.activity.JunkCleanActivity;
import com.guppy.phoneclean.activity.SettingActivity;
import com.guppy.phoneclean.activity.SpeedActivity;
import com.guppy.phoneclean.activity.StartPageActivity;
import com.guppy.phoneclean.ad.AdListener;
import com.guppy.phoneclean.ad.PangleAdListener;
import com.guppy.phoneclean.ad.PanglePop;
import com.guppy.phoneclean.ad.Pop;
import com.guppy.phoneclean.adapter.MainFunctionAdapter;
import com.guppy.phoneclean.model.ClearViewModel;
import com.guppy.phoneclean.model.MainFunctionModel;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.AppUtils;
import com.guppy.phoneclean.utils.DbUtils;
import com.guppy.phoneclean.utils.TextFormater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifDrawable;

public class ClearFragment extends BaseFragment<ClearViewModel>
        implements MainFunctionAdapter.OnItemClickLitener, View.OnClickListener{

    private RecyclerView recyclerView;
    private GifDrawable gifFromAssets;
    private MainFunctionAdapter adapter;
    private List<MainFunctionModel> list;
    private ImageView clearUp,imgStatus1;
    private TextView textProgress,textProgressRemaining;
    private FrameLayout imgStatus;
    private LinearLayout lineNumber,lineNumber1;
    private boolean scanning = false;
    AnimatorSet bouncer = new AnimatorSet();//创建一个动画集合类

    @Override
    protected Class<ClearViewModel> getViewClass() {
        return ClearViewModel.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clear,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private long sum, available;
    private int percent;
    private static final int IS_NORMAL = 101;
    private Timer mTimer;

    public void setTimeTask() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                try {
                    sum = AppUtils.getTotalMemory();
                    available = AppUtils.getAvailMemory(getContext());
                    percent = (int) AppUtils.getPercent(getContext());
                    msg.what = IS_NORMAL;
                    if (handler != null) handler.sendMessage(msg);
                } catch (Exception e) {
                    msg.what = 888;
                    msg.obj = e.toString();
                    if (handler != null) handler.sendMessage(msg);
                }
            }
        }, 0, 3000);
    }


    private void initView(){
        recyclerView = getView().findViewById(R.id.recyclerview);
        textProgress = getView().findViewById(R.id.text_progress);
        clearUp = getView().findViewById(R.id.clearUp);
        imgStatus1 = getView().findViewById(R.id.imgStatus1);
        imgStatus = getView().findViewById(R.id.imgStatus);
        lineNumber = getView().findViewById(R.id.line_number);
        lineNumber1 = getView().findViewById(R.id.line_number1);
        textProgressRemaining = getView().findViewById(R.id.text_progress_remaining);
        clearUp.setOnClickListener(this);
        imgStatus.setOnClickListener(this);
        getView().findViewById(R.id.more).setOnClickListener(this);
    }

    private void initData(){
        loadBannerAd();
        initAnm();
        setTimeTask();
        list = loadMainFunctionList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        adapter = new MainFunctionAdapter(list, this);
        recyclerView.setAdapter(adapter);
    }

    private List<MainFunctionModel> loadMainFunctionList() {
        List<MainFunctionModel> modelList = new ArrayList<>();
        modelList.add(new MainFunctionModel(R.mipmap.ic_speed,getString(R.string.mobile_booster),getString(R.string.clearing_background_software)));
        modelList.add(new MainFunctionModel(R.mipmap.ic_app_management,getString(R.string.app_management),getString(R.string.view_uninstalled_apps)));
        modelList.add(new MainFunctionModel(R.mipmap.ic_cpu_cooldown,getString(R.string.cpu_cool_down),getString(R.string.lower_the_CPU_temperature)));
       // modelList.add(new MainFunctionModel(R.mipmap.ic_picture,getString(R.string.picture_cleaning),getString(R.string.view_cached_thumbnails)));
        modelList.add(new MainFunctionModel(R.mipmap.ic_cache,getString(R.string.cache_cleaning),getString(R.string.app_junk_cache)));
        return modelList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clearUp :
            case R.id.imgStatus:
                clearStart();
                imgStatus.setEnabled(false);
                clearUp.setEnabled(false);
                break;
            case R.id.more :
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(int position, MainFunctionModel model) {
        //Toast.makeText(getActivity(), model.getName(),Toast.LENGTH_LONG).show();
        if (!checkPermissions()) return;
        switch (position) {
            case 0 :
                startActivity(new Intent(getActivity(), SpeedActivity.class));
                break;
            case 1 :
                startActivity(new Intent(getActivity(), AppMangerActivity.class));
                break;
            case 2 :
                startActivity(new Intent(getActivity(), CpuCoolDownActivity.class));
                break;
            case 3 :
                startActivity(new Intent(getActivity(), CacheClearActivity.class));
                break;
            case 4 :
                //startActivity(new Intent(getActivity(), JunkCleanActivity.class));
                startActivity(new Intent(getActivity(), CacheClearActivity.class));
                break;
            default:
                break;
        }
    }

    private void initAnm() {
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(clearUp, "scaleY", 1f, 1.1f, 1f);//沿着Y轴放大
        animatorY.setRepeatCount(-1);//设置动画重复次数
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(clearUp, "scaleX", 1f, 1.1f, 1f);//沿着X轴放大
        animatorX.setRepeatCount(-1);//设置动画重复次数

        bouncer.play(animatorY).with(animatorX);//play:先播放animator with:同时播放animator2 after:在某动画后播放 before:再某动画前播放
        bouncer.setDuration(1600);//持续时间
        bouncer.setInterpolator(new LinearInterpolator());//不停顿
        bouncer.start();//开始动画


       /* animator2 = ObjectAnimator.ofFloat(imgStatus2, "rotation", 0.0f, -360.0f);
        animator2.setDuration(1600);
        animator2.setInterpolator(new LinearInterpolator());//不停顿
        animator2.setRepeatCount(-1);//设置动画重复次数
        //animator2.start();

        animator3 = ObjectAnimator.ofFloat(imgStatus3, "rotation", 0.0f, 360.0f);
        animator3.setDuration(1800);
        animator3.setInterpolator(new LinearInterpolator());//不停顿
        animator3.setRepeatCount(-1);//设置动画重复次数
        //animator3.start();*/
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return true;
            }
            if (msg.what == IS_NORMAL) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    textProgress.setText(percent + "");
                    textProgressRemaining.setText(getString(R.string.string_user_memory) + TextFormater.dataSizeFormat(sum - available) + "\n" + getString(R.string.string_remaining_memory)  + TextFormater.dataSizeFormat(sum));
                }
            } else if (msg.what == 0) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    //assets 文件
                    try {
                        gifFromAssets = new GifDrawable(getActivity().getAssets(), "img_clean.gif");
                        imgStatus1.setImageDrawable(gifFromAssets);
                        gifFromAssets.setLoopCount(3);
                        gifFromAssets.addAnimationListener(loopNumber -> {
                            /*if (gifFromAssets != null) {
                                gifFromAssets.stop();

                            }*/
                        });
                        gifFromAssets.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (getActivity() != null) {
                    //清理第一步
                    //changeStatus(1);
                    viewModel.startClearUp(getActivity(), handler, viewModel.TYPE_SCANNING);
                }
            } else if (msg.what == 1) {
                if (getActivity() != null) {
                    //清理第二步
                    //changeStatus(2);
                    viewModel.startClearUp(getActivity(), handler, viewModel.TYPE_CLEARING);
                }
            } else if (msg.what == 2) {
                //清理第三步
                //changeStatus(3);
                long size = (long) msg.obj;
                /*if (size > 0) {
                    Formatter.formatFileSize(getContext(),size);
                    Toast.makeText(getContext(), getActivity().getString(R.string.string_clearup_size) + TextFormater.dataSizeFormat(size),Toast.LENGTH_LONG).show();
                    //Toast.makeText(getContext(),Formatter.formatFileSize(getContext(),size) ,Toast.LENGTH_LONG).show();

                }*/
                SysConfig sysConfig = DbUtils.getConfig();
                //showLoading();
                clearUp.setEnabled(true);
                imgStatus.setEnabled(true);
                bouncer.start();
                gifFromAssets.stop();
                IConstant.popAd.show1(new AdListener(){
                    @Override
                    public void onClose() {
                        super.onClose();
                        Intent intent = new Intent(getActivity(), AllEndActivity.class);
                        intent.putExtra("TYPE","clearUp");
                        intent.putExtra("size",size);
                        startActivity(intent);
                    }
                });
                /*pop.show1(new AdListener(){
                    @Override
                    public void onClose() {
                        super.onClose();
                        Intent intent = new Intent(getActivity(), AllEndActivity.class);
                        intent.putExtra("TYPE","clearUp");
                        intent.putExtra("size",size);
                        startActivity(intent);
                    }});*/
                /*if (sysConfig.getType() == 1){
                    new Pop(getActivity(),new AdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();
                            closeLoading();
                            Intent intent = new Intent(getActivity(), AllEndActivity.class);
                            intent.putExtra("TYPE","clearUp");
                            intent.putExtra("size",size);
                            startActivity(intent);
                        }
                    }).load();
                }else {
                    new PanglePop(getActivity(),new PangleAdListener(){
                        @Override
                        public void onClose() {
                            super.onClose();
                            closeLoading();
                            Intent intent = new Intent(getActivity(), AllEndActivity.class);
                            intent.putExtra("TYPE","clearUp");
                            intent.putExtra("size",size);
                            startActivity(intent);
                        }
                    }).loadPopAd();
                }*/

                /*clearUp.setOnClickListener(v -> {
                    if (handler != null) handler.sendEmptyMessageDelayed(3, 100);
                });*/

            } else if (msg.what == 3) {
                //changeStatus(4);
                scanning = false;
            }
            return true;
        }
    });

    void changeStatus(int step) {
        if (getActivity() == null || getActivity().isFinishing() ) {
            return;
        }
        if (step == 1) {
            clearUp.setVisibility(View.INVISIBLE);
            imgStatus1.setVisibility(View.GONE);
            imgStatus.setVisibility(View.VISIBLE);
        } else if (step == 2) {
            imgStatus1.setVisibility(View.GONE);
            imgStatus.setVisibility(View.VISIBLE);
        } else if (step == 3) {
            //文字
            lineNumber.setVisibility(View.INVISIBLE);
            lineNumber1.setVisibility(View.INVISIBLE);
            clearUp.setVisibility(View.VISIBLE);
            //1
            imgStatus1.setVisibility(View.VISIBLE);
            imgStatus.setVisibility(View.GONE);
        } else if (step == 4) {
            //文字
            lineNumber.setVisibility(View.VISIBLE);
            lineNumber1.setVisibility(View.VISIBLE);
            //默认背景
            imgStatus1.setVisibility(View.GONE);
            imgStatus.setVisibility(View.VISIBLE);
            clearUp.setImageResource(R.mipmap.ic_clean_up);
            //清理按钮
            clearUp.setOnClickListener(this);
        } else if (step == 5) {
            //异常--或者退出界面
            if (gifFromAssets != null && gifFromAssets.isRunning()) {
                gifFromAssets.stop();
            }
            //默认背景
            clearUp.setVisibility(View.VISIBLE);
            //文字
            lineNumber.setVisibility(View.INVISIBLE);
            lineNumber1.setVisibility(View.INVISIBLE);
            //1
            imgStatus1.setVisibility(View.GONE);
            imgStatus.setVisibility(View.VISIBLE);
        }
    }

    public void clearStart() {
        if (!scanning && getActivity() != null) {
            //scanning = true;
            bouncer.end();
            if (handler != null) handler.obtainMessage(0).sendToTarget();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scanning) {
            //changeStatus(5);
            scanning = false;
        }
        if (gifFromAssets != null && gifFromAssets.isPlaying()) {
            gifFromAssets.stop();
        }
        /*if (animator2.isRunning()) {
            animator2.cancel();
        }
        if (animator3.isRunning()) {
            animator3.cancel();
        }*/
        gifFromAssets = null;
        /*animator2 = null;
        animator3 = null;*/
        handler = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
