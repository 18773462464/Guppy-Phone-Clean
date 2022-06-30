package com.guppy.phoneclean.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.ILosePagerAdapter;
import com.guppy.phoneclean.utils.IConfig;
import com.guppy.phoneclean.widget.NoScrollViewPager;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoseFragment extends BaseFragment{

    private ILosePagerAdapter pagerAdapter;
    private NoScrollViewPager viewPager;
    private TextView textProgressRemaining,textProgress;

    @Override
    protected Class getViewClass() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lose,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    public void initView() {
        textProgressRemaining = getView().findViewById(R.id.text_progress_remaining);
        textProgress = getView().findViewById(R.id.text_progress);
        viewPager = getView().findViewById(R.id.view_pagers);
    }

    private void initData() {
        loadBannerAd();
        int percent = (int) IConfig.getSingleton().getUsedInternalMemoryPercent();
        if (percent<1) percent=1;
        String availableInternalMemorySize = IConfig.getSingleton().getAvailableInternalMemorySize(getContext());
        String used = IConfig.getSingleton().getUsedInternalMemorySize(getContext());
        textProgressRemaining.setText(getString(R.string.string_user_memory) + used + "\n"+getString(R.string.string_usable)  + availableInternalMemorySize);
        textProgress.setText(100-percent + "%");

        viewPager.setOffscreenPageLimit(0);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_name_bar_photo)).setIcon(R.drawable.ic_lose_photo));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_name_bar_video)).setIcon(R.drawable.ic_lose_video));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_name_bar_music)).setIcon(R.drawable.ic_lose_music));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_name_bar_document)).setIcon(R.drawable.ic_lose_document));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.string_name_bar_apk)).setIcon(R.drawable.ic_lose_apk));
        pagerAdapter = new ILosePagerAdapter( getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        //为TabLayout添加Tab选择事件监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {//当标签被选择时回调
                viewPager.setCurrentItem(tab.getPosition(), false);
                checkPermissions();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {//当标签从选择变为非选择时回调

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {//当标签被重新选择时回调

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pagerAdapter = null;
        viewPager = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
