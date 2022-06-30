package com.guppy.phoneclean.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by towave on 2016/4/8.
 */
public class AppUtils {

    public static final List<String> jsonArray2List(String str) {
        List<String> list = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(str)) {
                JSONArray array = new JSONArray(str);
                for (int i = 0; i < array.length(); i++) {
                    String item = array.optString(i);
                    if (TextUtils.isEmpty(item)) continue;
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static final String indexList(List<String> list, int idx) {
        if (list == null || list.size() == 0) return null;
        if (idx < 0 ) {
            idx=new Random().nextInt(list.size());
        }
        if (idx >= list.size()) return null;
        return list.get(idx);
    }

    /**
     * 描述：获取可用内存.
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化
        return memoryInfo.availMem;
    }


    /**
     * 描述：总内存.
     */
    public static long getTotalMemory() {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader,
                    8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return memory * 1024;
    }


    public static float getPercent(long memory) {
        long y = getTotalMemory();
        final double x = ((memory / (double) y) * 100);
        return new BigDecimal(x).setScale(2, BigDecimal.ROUND_HALF_UP)
                                .floatValue();
    }

    public static float getPercent(Context context) {
        long l = getAvailMemory(context);
        long y = getTotalMemory();
        return getPercent(y - l);
    }
}
