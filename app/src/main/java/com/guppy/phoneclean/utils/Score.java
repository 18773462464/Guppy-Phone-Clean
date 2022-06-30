package com.guppy.phoneclean.utils;

import android.content.Context;

public class Score {
    private volatile static Score instance; //声明成 volatile

    private Score() {
    }

    public static Score getSingleton() {
        if (instance == null) {
            synchronized (Score.class) {
                if (instance == null) {
                    instance = new Score();
                }
            }
        }
        return instance;
    }
    /**
     * 满分30分
     */
    int memoryScore(Context context) {
        float percent = IConfig.getSingleton().getPercent(context);
        if (percent > 90 && percent <= 100) {
            return 5;
        } else if (percent > 70 && percent <= 80) {
            return 15;
        } else if (percent > 60 && percent <= 70) {
            return 20;
        } else if (percent > 50 && percent <= 60) {
            return 25;
        }
        return 30;
    }

    /**
     * 满分30分
     */
    int cpuScore() {
        String maxCpuFreq = IConfig.getSingleton().getMaxCpuFreq();
        String minCpuFreq = IConfig.getSingleton().getMinCpuFreq();
        String curCpuFreq = IConfig.getSingleton().getCurCpuFreq();
        if (maxCpuFreq.isEmpty()||minCpuFreq.isEmpty()||curCpuFreq.isEmpty())return 20;
        int num = ((Integer.parseInt(maxCpuFreq)+Integer.parseInt(minCpuFreq))/2);
        if (num==Integer.parseInt(curCpuFreq)) {
            return 20;
        }
        if (num<=Integer.parseInt(curCpuFreq)) {
            return 10;
        }
        return 30;
    }

    public int allScore(Context context) {
        //内存评分，总分30分
        int memoryScore = memoryScore(context);
        //是否有存储卡--加20分无-20分
        boolean externalStorageAvailable = IConfig.getSingleton().isExternalStorageAvailable();
        //cpu评分，满分30
        int cpuScore =cpuScore();
//        //电池---状态良好+20分/*
//        BatteryBean first = LitePal.findFirst(BatteryBean.class);
        int healthScore = 10;
//        if (first!=null){
//            int health = first.getHealth();
//            switch(health){
//                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
//                    if (IConstant.IS_DEBUG) Log.e("Batter","未知状况");
//                    healthScore = -20;
//                    break;
//                case BatteryManager.BATTERY_HEALTH_GOOD:
//                    if (IConstant.IS_DEBUG) Log.e("Batter","状态良好");
//                    healthScore = 20;
//                    break;
//                case BatteryManager.BATTERY_HEALTH_DEAD:
//                    if (IConstant.IS_DEBUG)Log.e("Batter","电池没有电");
//                    healthScore = -20;
//                    break;
//                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
//                    if (IConstant.IS_DEBUG)Log.e("Batter","电池电压过高");
//                    healthScore = -20;
//                    break;
//                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
//                    if (IConstant.IS_DEBUG)Log.e("Batter","电池过热");
//                    healthScore = 10;
//                    break;
//            }
//        }
        return memoryScore+cpuScore+(externalStorageAvailable?20:-20)+healthScore;
    }


}
