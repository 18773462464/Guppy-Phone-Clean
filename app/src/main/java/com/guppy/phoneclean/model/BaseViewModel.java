package com.guppy.phoneclean.model;


import android.app.Activity;
import android.os.Bundle;

import com.guppy.phoneclean.interf.MainListenter;

import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {
    private MainListenter mainListenter;

    public void onCreateListenter(Activity mainActivity) {
        mainListenter = (MainListenter)mainActivity;
    }

    public void openActivity(Class<?> pClass, Bundle bundle) {
        if (mainListenter != null) mainListenter.openActivity(pClass, bundle);
    }

}
