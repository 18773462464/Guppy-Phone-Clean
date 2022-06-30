package com.guppy.phoneclean.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.ad.Banner;
import com.guppy.phoneclean.ad.PangleBanner;
import com.guppy.phoneclean.dialog.LoadingDialog;
import com.guppy.phoneclean.model.BaseViewModel;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseFragment<B extends BaseViewModel> extends Fragment {
    protected B viewModel;
    //当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
    public boolean isFragmentVisible;
    //是否是第一次开启网络加载
    public boolean isFirst;
    protected String TAG = this.getClass().getSimpleName();
    public Handler handler = new Handler();
    public LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class<B> viewClass = getViewClass();
        if (viewClass != null) {
            viewModel = new ViewModelProvider(this).get(viewClass);
            viewModel.onCreateListenter(getActivity());
        }

    }

    public final void showLoading() {
        closeLoading();
        try {
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void closeLoading() {
        try {
            if (loadingDialog != null) loadingDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract Class<B> getViewClass();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }
        //可见，并且没有加载过
        if (!isFirst && isFragmentVisible) {
            onFragmentVisibleChange(true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    /**
     * 当前fragment可见状态发生变化时会回调该方法
     * <p>
     * 如果当前fragment是第一次加载，等待onCreateView后才会回调该方法，其它情况回调时机跟 {@link #setUserVisibleHint(boolean)}一致
     * 在该回调方法中你可以做一些加载数据操作，甚至是控件的操作.
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    public void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible && getActivity() != null && !getActivity().isFinishing()) {
            //loadData();
        }
    }

    void loadBannerAd() {
        handler.postDelayed(banner,500);
    }

    Runnable banner = () -> {
        try {
            Banner ad_x = getView().findViewById(R.id.ad_x);
            PangleBanner ad = getView().findViewById(R.id.pangle_ad);
            ad.setVisibility(isBannerVisibility() ? View.VISIBLE : View.GONE);
            ad_x.setVisibility(isBannerVisibility() ? View.GONE : View.VISIBLE);
            SysConfig sysConfig = DbUtils.getConfig();
            if (sysConfig.getType() == 2){
                if (ad != null){
                    ad.loadBanner();
                }
            } else {
                if (ad_x != null ) {
                    ad_x.load();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    boolean isBannerVisibility(){
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig.getType() == 2 ){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentVisible = false;
        isFirst = false;
        //handler.removeCallbacks(banner);
    }

    protected int RC_CAMERA_AND_LOCATION = 2022;
    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public boolean checkPermissions() {
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            return true;
        } else {
            // 没有权限，进行权限请求
            EasyPermissions.requestPermissions(this, getString(R.string.string_read_cache), RC_CAMERA_AND_LOCATION, perms);
            return false;
        }
    }
}
