package com.guppy.phoneclean.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.guppy.phoneclean.R;

import androidx.annotation.NonNull;

public class BaseDialog extends Dialog implements View.OnClickListener {

    public View view;
    protected Context context;
    protected ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    protected DisplayMetrics displayMetrics;
    public LoadingDialog loadingDialog;

    public BaseDialog(@NonNull Context context) {
        this(context, R.style.CustomLoading);
    }

    public BaseDialog(Context context, int style){
        super(context, style);
        this.context = context;
        displayMetrics = context.getResources().getDisplayMetrics();
    }

    public final void showLoading() {
        closeLoading();
        try {
            loadingDialog = new LoadingDialog(context);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void show() {
        if (context != null && (context instanceof Activity) && !((Activity) context).isFinishing())
            super.show();
    }

    protected void fullWindowCenter(Context context) {
        layoutParams = getWindow().getAttributes();
        Rect rect = new Rect();
        View v = getWindow().getDecorView();
        v.getWindowVisibleDisplayFrame(rect);
        layoutParams.width = displayMetrics.widthPixels;
    }

}
