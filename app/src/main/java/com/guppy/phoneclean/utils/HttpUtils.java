package com.guppy.phoneclean.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.NotificationUtils;
import com.blankj.utilcode.util.StringUtils;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.Logger;
import com.guppy.phoneclean.model.SysConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.Nullable;

public final class HttpUtils {

    public static final void newPostStringRequest(Context context, String url, JSONObject params, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        try {
            if (context == null) return;
            Map<String, String> mParams = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            headers.put("X-requested-with",context.getPackageName());
            if(IConstant.IS_DEBUG) Logger.log("req->"+url+","+params);
            Locale locale = context.getResources().getConfiguration().locale;
            String lang = locale.getLanguage() + "-" + locale.getCountry();
            boolean i = NotificationUtils.areNotificationsEnabled();
            SysConfig sysConfig = DbUtils.getConfig();
            String userId = sysConfig.getUserId();
            if (params != null) {
                //mParams.put("data", HttpUtils.encodeString(params.toString()));
                if (!StringUtils.isEmpty(userId))
                    mParams.put("userid", userId);
                mParams.put("lang", lang);
                mParams.put("appver", getVerName(context));
            }
            Request request = new PostStringRequest(url, mParams,headers, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static final void newPostStringRequest1(Context context, String url, Map<String, String> mParams, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        try {
            if (context == null) return;
            Map<String, String> headers = new HashMap<>();
            headers.put("X-requested-with",context.getPackageName());

            Request request = new PostStringRequest(url, mParams,headers, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static final byte[] decode(byte[] input) {
        byte[] output = input;
        try {
            output = Base64.decode(input, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


    public static final byte[] decode(String input) {
        return decode(input.getBytes());
    }


    public static final String decodeString(String input) {
        return new String(decode(input));
    }

    public static final String decodeString(byte[] input) {
        return new String(decode(input));
    }


    public static final String encodeString(String input) {
        return new String(encode(input));
    }

    public static final String encodeString(byte[] input) {
        return new String(encode(input));
    }

    public static final byte[] encode(byte[] input) {
        byte[] output = input;
        try {
            output = Base64.encode(input, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public static final byte[] encode(String input) {
        return encode(input.getBytes());
    }
}
