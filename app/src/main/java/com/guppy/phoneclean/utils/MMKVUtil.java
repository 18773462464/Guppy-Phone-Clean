package com.guppy.phoneclean.utils;

import android.content.Context;

import com.guppy.phoneclean.IClearApplication;
import com.guppy.phoneclean.R;

public class MMKVUtil {

    public static void putNotifMessages(String str1, String str2) {
        Context context =  IClearApplication.getContext();
        PrefUtils.setString(context,context.getString(R.string.app_name),"ACTION",str1);
        PrefUtils.setString(context,context.getString(R.string.app_name),"MESSAGE",str2);
    }

    public static String getNotifMessages(int postion) {
        Context context =  IClearApplication.getContext();
        if (postion == 0) {
            return PrefUtils.getString(context,context.getString(R.string.app_name),"ACTION","");
        } else {
            return PrefUtils.getString(context,context.getString(R.string.app_name),"MESSAGE","");
        }
    }

}
