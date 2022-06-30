package com.guppy.phoneclean.adapter;


import com.guppy.phoneclean.fragment.FragmentSystem;
import com.guppy.phoneclean.fragment.FragmentUser;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class IApplicationPagerAdapter extends FragmentPagerAdapter {
    //fragment的数量
    int nNumOfTabs;

    public IApplicationPagerAdapter(FragmentManager fm, int nNumOfTabs) {
        super(fm, nNumOfTabs);
        this.nNumOfTabs = nNumOfTabs;
    }

    /**
     * 重写getItem方法
     * @param position 指定的位置
     * @return 特定的Fragment
     */
    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentUser();
            case 1:
                return new FragmentSystem();
        }
        return new FragmentUser();
    }


    /**
     * 重写getCount方法
     *
     * @return fragment的数量
     */
    @Override
    public int getCount() {
        return nNumOfTabs;
    }
}
