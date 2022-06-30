package com.guppy.phoneclean.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.guppy.phoneclean.R;

import androidx.annotation.NonNull;

/**
 * author: His cat
 * date:   On 2021/11/18
 */
public class LoadingDialog extends BaseDialog{
    public LoadingDialog(@NonNull Context context) {
        this(context, context.getString(R.string.loading));
    }

    public LoadingDialog(Context context, String hintStr){
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null, false);
        fullWindowCenter(context);
        setContentView(view);
        setCancelable(false);
        /*if (!TextUtils.isEmpty(hintStr)) {
            TextView hint = findViewById(R.id.hint);
            hint.setText(hintStr);
            hint.setVisibility(View.VISIBLE);
        }*/
    }
}
