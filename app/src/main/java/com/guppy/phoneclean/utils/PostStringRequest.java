package com.guppy.phoneclean.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public final class PostStringRequest extends StringRequest {
    Map<String, String> params;
    Map<String, String> headers;

    public PostStringRequest(String url, Map<String, String> params, Map<String, String> headers, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.params=params;
        this.headers=headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> mHeaders = new HashMap<>();
        if (headers != null) mHeaders.putAll(headers);
        return mHeaders;
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
