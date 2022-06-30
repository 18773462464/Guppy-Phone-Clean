package com.guppy.phoneclean.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.fragment.FragmentUser;
import com.guppy.phoneclean.utils.ActivityUtils;

public class AppMangerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manger);
        loadBannerAd();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.string_name_appication_manger));
        findViewById(R.id.toolbar_left_img).setOnClickListener(v -> finish());
        ActivityUtils.replaceFragmentToNullBackStack(getSupportFragmentManager(),new FragmentUser(),R.id.fram_content);
    }
}