package com.guppy.phoneclean.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;

public class PrivacyPolicyActivity extends BaseActivity {

    WebView webView;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        findViewById(R.id.toolbar_left_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "finish: ");
                finish();
            }
        });
        progress_bar = findViewById(R.id.progress_bar);
        try {
            webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    try {
                        progress_bar.setProgress(newProgress);
                        progress_bar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            webView.setWebViewClient(new WebViewClient() {
            });
            webView.loadUrl(IConstant.PRIVACY_POLICY_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadBannerAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}