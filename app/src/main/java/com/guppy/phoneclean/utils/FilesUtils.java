package com.guppy.phoneclean.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;

import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.interf.FilesLister;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;

public class FilesUtils {
    //统计垃圾数据


    public static final long MIN_BIG_FILE_SIZE = 20 * 1024 * 1024;

    public static final long MIN_BIG_FILE_TIME = 60 * 24 * 60 * 60 * 1000;


    //图像去重
    public static final ArrayMap<String, List<File>> loadImageList(Context context, FilesLister filesLister) {
        try {
            ArrayMap<String, List<File>> arrayMap = new ArrayMap<>();
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA},
                    null, null, null);
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(0));
                if (!file.exists()) continue;
                String md5 = Bxesjjwk.getFileMD5(file);
                List<File> stringList = arrayMap.get(md5);
                if (stringList == null) stringList = new ArrayList<>();
                if (filesLister != null) filesLister.onScanFile(file);
                stringList.add(file);
                arrayMap.put(md5, stringList);
            }
            cursor.close();
            for (int i = 0; i < arrayMap.size(); i++) {
                List<File> stringList = arrayMap.valueAt(i);
                if (stringList == null || stringList.size() <= 1) {
                    arrayMap.removeAt(i);
                    i--;
                }
            }
            return arrayMap;
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return null;

    }

    //图像去重
    public static final ArrayMap<String, List<File>> loadImageList2(Context context) {
        try {
            ArrayMap<String, List<File>> arrayMap = new ArrayMap<>();
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA},
                    null, null, null);
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(0));
                if (!file.exists()) continue;
                String md5 = Bxesjjwk.getFileMD5(file);
                List<File> stringList = arrayMap.get(md5);
                if (stringList == null) stringList = new ArrayList<>();
                stringList.add(file);
                arrayMap.put(md5, stringList);
            }
            cursor.close();
            for (int i = 0; i < arrayMap.size(); i++) {
                List<File> stringList = arrayMap.valueAt(i);
                if (stringList == null || stringList.size() <= 1) {
                    arrayMap.removeAt(i);
                    i--;
                }
            }
            return arrayMap;
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return null;

    }


    public static final byte[] decode(byte[] input, String key) {
        byte[] output = input;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, Base64.NO_WRAP));
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return output;
    }


    //获取久未使用的大文件
    public static final void loadBigFile(@NonNull File file, /*@NonNull List<File> fileList,*/ FilesLister filesLister) {
        try {
            if (file.isFile()) {
                if (System.currentTimeMillis() - MIN_BIG_FILE_TIME > file.lastModified()
                        && file.length() > MIN_BIG_FILE_SIZE /*&& fileList != null*/) {
                    if (filesLister!=null){
                        filesLister.onScanFile(file);
                    }
                    /*fileList.add(file);*/
                }
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        loadBigFile(f, /*fileList,*/ filesLister);
                    }
                }
            }
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
    }


    public static final int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    //垃圾数据来源1   SD卡目录下不再使用的apk包
    public static final void queryApkFiles(@NonNull Context context, @NonNull File file, @NonNull List<File> fileList,
                                           @NonNull List<File> fileList1, FilesLister zzbxevsz) {
        try {
            if (context == null || file == null || fileList == null) return;
            if (zzbxevsz != null) zzbxevsz.onScanFile(file);
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(".apk")) {//apk文件
                    PackageInfo packageInfo = getApkFileInfo(context, file.getAbsolutePath());
                    if (packageInfo != null && !isAppInstalled(context, packageInfo.packageName)) {
                        return;
                    }
                    //损坏 或者已经安装的安装包
                    fileList.add(file);
                }
                if (System.currentTimeMillis() - MIN_BIG_FILE_TIME > file.lastModified()
                        && file.length() > MIN_BIG_FILE_SIZE && (fileList != null && !fileList.contains(file)) && fileList1 != null) {
                    fileList1.add(file);
                }
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        queryApkFiles(context, f, fileList, fileList1, zzbxevsz);
                    }
                }
            }
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
    }

    //垃圾数据来源2   SD卡/Android/data/包名   当找不到该安装应用时   表明其已经卸载 存在垃圾数据
    public static final void queryAppCacheFiles(@NonNull Context context, FilesLister  filesLister) {
        try {
            File file = new File(Environment.getDataDirectory(), "Android");
            if (!file.exists()) return;
            file = new File(file, "data");
            if (!file.exists()) return;
            File[] files = file.listFiles();
            for (File f : files) {
                if (!isAppInstalled(context, f.getName())) {
                    if (filesLister!=null){
                        filesLister.onScanFile(f);
                    }
                }
            }
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
    }

    public static final boolean isAppInstalled(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception e) {
            packageInfo = null;
        }
        return packageInfo != null;
    }


    //垃圾数据来源3    图像目录的缩略图缓存文件  SD卡/DCIM/.thumbnails
    public static final void queryThumbnailsFiles(/*@NonNull List<File> fileList,*/ FilesLister filesLister) {
        try {
            File file = new File(Environment.getDataDirectory(), "DCIM");
            if (!file.exists()) return;
            file = new File(file, ".thumbnails");
            if (!file.exists()) return;
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (filesLister!=null){
                    filesLister.onScanFile(files[i]);
                }
            }
            //fileList.addAll(Arrays.asList(files));
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
    }


    //计算文件尺寸
    public static final long fileSize(List<File> fileList) {
        try {
            if (fileList == null || fileList.isEmpty()) return 0;
            long size = 0;
            for (File file : fileList) {
                size += file.length();
            }
            return size;
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return 0;
    }


    public static final long fileSize(File file) {
        try {
            if (file == null) return 0;
            return file.length();
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return 0;
    }


    //获得SD卡总大小
    public static final long getSDTotalSize() {
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize, totalBlocks;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
            }
            return blockSize * totalBlocks;
        } catch (Exception e) {
            return -1;
        }
    }

    //获得sd卡剩余容量，即可用大小
    public static final long getSDAvailableSize() {
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize, availableBlocks;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
            return blockSize * availableBlocks;
        } catch (Exception e) {
            return 0;
        }
    }

    //获取内存情况 [0]当前可用 [1]总内存
    public static final Long[] getAvailableMemory(Context context) {
        try {
            ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
            ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(outInfo);
            return new Long[]{outInfo.availMem, outInfo.totalMem};
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return new Long[]{0l, 0l};
    }


    public static final String fileSize(float size) {
        if (size < 1024) {
            return numberFormat(size) + "B";
        } else if (size < 1024 * 1024) {
            return numberFormat(size / 1024) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            return numberFormat(size / (1024 * 1024)) + "M";
        } else {
            return numberFormat(size / (1024 * 1024 * 1024)) + "G";
        }
    }

    public static final String numberFormat(float v) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(v);
    }

    //是否图像文件
    public static final boolean isImg(File file) {
        if (file == null) return false;
        String path = file.getAbsolutePath().toLowerCase();
        return path.endsWith(".png") || path.endsWith(".jpeg") || path.endsWith("jpg");
    }

    //是否apk文件
    public static final boolean isApk(File file) {
        if (file == null) return false;
        return file.getAbsolutePath().endsWith(".apk");
    }

    public static final String dateFormat(long time, String format) {
        if (TextUtils.isEmpty(format)) format = "yyyy/MM/dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(time));
    }


    //获取电池电量  [0]电量 [1]温度
    public static final int[] getBattery(Context context) {
        try {
            Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryInfoIntent.getIntExtra("level", 0);
            int temperature = batteryInfoIntent.getIntExtra("temperature", 0);
            return new int[]{level, temperature};
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return new int[]{100, 25};
    }

//    //扫描非市场的应用
//    public static final List<AppInfos> queryUnPlayAppList(Context context) {
//        try {
//            PackageManager pm = context.getPackageManager();
//            List<PackageInfo> mPacks = pm.getInstalledPackages(0);
//            List<AppInfos> infoList = new ArrayList<>();
//            String unknow = context.getString(R.string.string_unknow);
//            for (PackageInfo info : mPacks) {
//                if (info.packageName.equals(context.getPackageName())) continue;
//                String installPackage = pm.getInstallerPackageName(info.packageName);
//                //if (installPackage.equals("com.android.vending")) continue;
//                AppInfos clearAppBean = new AppInfos();
//                clearAppBean.setSystem((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
//                clearAppBean.setPkgName(info.packageName);
//                clearAppBean.setAppName(info.applicationInfo.loadLabel(pm));
//                clearAppBean.setVaersionName(info.versionName);
//                long installTime = System.currentTimeMillis();
//                if (info != null) installTime = info.firstInstallTime;
//                clearAppBean.setInstallTime(installTime);
//                clearAppBean.setIcon(info.applicationInfo.loadIcon(pm));
//                if (!TextUtils.isEmpty(installPackage))
//                    clearAppBean.setInstallPkg(installPackage);
//                PackageInfo pi = getAppInfo(context, installPackage);
//                if (pi != null)
//                    clearAppBean.setInstallName(pi.applicationInfo.loadLabel(pm));
//                else clearAppBean.setInstallName(unknow);
//                infoList.add(clearAppBean);
//            }
//            return infoList;
//        } catch (Exception e) {
//            if (IConstant.IS_DEBUG) e.printStackTrace();
//        }
//        return null;
//    }


    public static final PackageInfo getApkFileInfo(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath) || context == null) return null;
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) return null;
            PackageManager pm = context.getPackageManager();
            return pm.getPackageArchiveInfo(filePath, 0);
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return null;
    }

    public static final PackageInfo getAppInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName) || context == null) return null;
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packageName, 0);
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
        return null;
    }


    //启动app
    public static final void openApp(Context context, String pkgName) {
        try {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            context.startActivity(launchIntentForPackage);
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
    }

    //卸载应用
    public static final void uninstallApp(Context context, String pkgName) {
        try {
            Uri packageURI = Uri.parse("package:" + pkgName);
            Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
            context.startActivity(intent);
        } catch (Exception e) {
            if (IConstant.IS_DEBUG) e.printStackTrace();
        }
    }


    public static synchronized Drawable byteToDrawable(String icon) {
        byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        }
        return null;
    }

    public static synchronized String drawableToByte(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();

            String icon = Base64.encodeToString(imagedata, Base64.DEFAULT);
            return icon;
        }
        return null;
    }

}
