package com.guppy.phoneclean.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IConfig {
    private static final String TAG = "IConfig";
    private volatile static IConfig instance; //声明成 volatile

    private IConfig() {
    }

    public static IConfig getSingleton() {
        if (instance == null) {
            synchronized (IConfig.class) {
                if (instance == null) {
                    instance = new IConfig();
                }
            }
        }
        return instance;
    }

    /**
     * 描述：获取可用内存.
     */
    public long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化
        return memoryInfo.availMem;
    }

    /**
     * 描述：总内存.
     */
    public long getTotalMemory() {
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


    float getPercent(long memory) {
        long y = getTotalMemory();
        final double x = ((memory / (double) y) * 100);
        return new BigDecimal(x).setScale(2, BigDecimal.ROUND_HALF_UP)
                .floatValue();
    }

    /**
     * 获得百分比
     *
     * @param context
     * @return
     */
    public float getPercent(Context context) {
        long l = getAvailMemory(context);
        long y = getTotalMemory();
        return getPercent(y - l);
    }


    /**
     * 外部存储是否可用 (存在且具有读写权限)
     */
    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return
     */
    public String getAvailableInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, availableBlocks * blockSize);
    }

    /**
     * 获取手机内部已用空间大小
     *
     * @return
     */
    public String getUsedInternalMemorySize(Context context){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long totalBlocks = stat.getBlockCount();   //block总数
        return Formatter.formatFileSize(context, blockSize * totalBlocks - availableBlocks * blockSize);
    }

    public float getUsedInternalMemoryPercent(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        float blockSize = stat.getBlockSize();
        float availableBlocks = stat.getAvailableBlocks();
        float totalBlocks = stat.getBlockCount();   //block总数
        return (availableBlocks * blockSize)/(blockSize * totalBlocks) *100;
    }


    /**
     * 获取手机内部空间大小
     *
     * @return
     */
    public String getTotalInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();//Gets the Android data directory
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();      //每个block 占字节数
        long totalBlocks = stat.getBlockCount();   //block总数
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }


    /**
     * 获取手机外部可用空间大小
     *
     * @return
     */
    public long getAvailableExternalMemorySize() {
        if (isExternalStorageAvailable()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    /**
     * 获取手机外部总空间大小
     *
     * @return
     */
    public long getTotalExternalMemorySize() {
        if (isExternalStorageAvailable()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return 0;
        }
    }


    /*****************************************/
    // 获取CPU最大频率（单位KHZ）

    // "/system/bin/cat" 命令行

    // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
    public String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
//            result = "N/A";
        }
        return result.trim();
    }


    // 获取CPU最小频率（单位KHZ）
    public String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
//            result = "N/A";
        }
        return result.trim();
    }


    // 实时获取CPU当前频率（单位KHZ）
    public String getCurCpuFreq() {
        String result = /*"N/A"*/"";
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取cpu使用率
     *
     * @return
     */
    public float getCpuUsed() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            try {
                Thread.sleep(360);
            } catch (Exception e) {
                e.printStackTrace();
            }
            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }


    public double getCPURateDesc_All() {
        String path = "/proc/stat";// 系统CPU信息文件
        long totalJiffies[] = new long[2];
        long totalIdle[] = new long[2];
        int firstCPUNum = 0;//设置这个参数，这要是防止两次读取文件获知的CPU数量不同，导致不能计算。这里统一以第一次的CPU数量为基准
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        Pattern pattern = Pattern.compile(" [0-9]+");
        for (int i = 0; i < 2; i++) {
            totalJiffies[i] = 0;
            totalIdle[i] = 0;
            try {
                fileReader = new FileReader(path);
                bufferedReader = new BufferedReader(fileReader, 8192);
                int currentCPUNum = 0;
                String str;
                while ((str = bufferedReader.readLine()) != null && (i == 0 || currentCPUNum < firstCPUNum)) {
                    if (str.toLowerCase().startsWith("cpu")) {
                        currentCPUNum++;
                        int index = 0;
                        Matcher matcher = pattern.matcher(str);
                        while (matcher.find()) {
                            try {
                                long tempJiffies = Long.parseLong(matcher.group(0).trim());
                                totalJiffies[i] += tempJiffies;
                                if (index == 3) {//空闲时间为该行第4条栏目
                                    totalIdle[i] += tempJiffies;
                                }
                                index++;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (i == 0) {
                        firstCPUNum = currentCPUNum;
                        try {//暂停50毫秒，等待系统更新信息。
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        double rate = -1;
        if (totalJiffies[0] > 0 && totalJiffies[1] > 0 && totalJiffies[0] != totalJiffies[1]) {
            rate = 1.0 * ((totalJiffies[1] - totalIdle[1]) - (totalJiffies[0] - totalIdle[0])) / (totalJiffies[1] - totalJiffies[0]);
        }
        //return String.format("cpu:%.2f",rate);
        return rate;
    }


    // 获取CPU名字
    public String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //获取电池电量  [0]电量 [1]温度
    public int[] getBattery(Context context) {
        try {
            Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryInfoIntent.getIntExtra("level", 0);
            int temperature = batteryInfoIntent.getIntExtra("temperature", 0);
            return new int[]{level, temperature};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{100, 25};
    }



}
