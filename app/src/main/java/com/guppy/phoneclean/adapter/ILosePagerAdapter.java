package com.guppy.phoneclean.adapter;


import com.guppy.phoneclean.fragment.apk.ApkFragment;
import com.guppy.phoneclean.fragment.document.DocumentFragment;
import com.guppy.phoneclean.fragment.music.MusicFragment;
import com.guppy.phoneclean.fragment.photo.PhotoFragment;
import com.guppy.phoneclean.fragment.video.VideoFragment;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ILosePagerAdapter extends FragmentPagerAdapter {
    //fragment的数量
    int nNumOfTabs;

    public ILosePagerAdapter(FragmentManager fm, int nNumOfTabs) {
        super(fm, nNumOfTabs);
        this.nNumOfTabs = nNumOfTabs;
    }

    /**
     * 重写getItem方法
     *
     * @param position 指定的位置
     * @return 特定的Fragment
     */
    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PhotoFragment();
            case 1:
                return new VideoFragment();
            case 2:
                return new MusicFragment();
            case 3:
                return new DocumentFragment();
            case 4:
                return new ApkFragment();
        }
        return new PhotoFragment();
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
