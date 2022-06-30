package com.guppy.phoneclean.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.utils.PrefUtils;

import androidx.annotation.RequiresApi;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class IAlertDialog extends BaseDialog{
    TextView tip_Txt;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public IAlertDialog(Context context, String tipTxt) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_ialert, null);
        fullWindowCenter(context);
        setContentView(view);
        setCancelable(false);
        tip_Txt = findViewById(R.id.dialog_tip_txt);
        tip_Txt.setText(tipTxt);
        this.context = context;


        findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                IConstant.wtiteTipShow = false;
                PrefUtils.setBoolean(context,context.getString(R.string.app_name),"FIRST",false);
                requestIgnoreBatteryOptimizations();
            }
        });
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                IConstant.wtiteTipShow = false;
            }
        });
    }

    /**
     * 申请加入白名单：
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("===", "e:" + e.toString());
        }
    }
}
