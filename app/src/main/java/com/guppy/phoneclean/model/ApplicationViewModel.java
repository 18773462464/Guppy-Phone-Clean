package com.guppy.phoneclean.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CommonRecyclerAdapter;
import com.guppy.phoneclean.adapter.OnItemClickListener;
import com.guppy.phoneclean.adapter.ViewHolder;
import com.guppy.phoneclean.bean.AppInfo;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

public class ApplicationViewModel extends ViewModel {
    private ApplistAdapter applistAdapter;
    private List<AppInfo> appInfoList;


    public void setDataAdapter(Context context, RecyclerView recyclerView, boolean isSystem) {
        appInfoList = getApp(context, isSystem);
        applistAdapter = new ApplistAdapter(isSystem, context, appInfoList, R.layout.list_item_manger_apk);
        if (!isSystem){
            applistAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //new APPTipDialog(context,applistAdapter,position);
                    applistAdapter.unInstall(position);
                }
            });
        }
        recyclerView.setAdapter(applistAdapter);
    }

    public static class ApplistAdapter extends CommonRecyclerAdapter<AppInfo> {
        private boolean isSystem;

        public ApplistAdapter(boolean isSystem, Context context, List<AppInfo> data, int layoutId) {
            super(context, data, layoutId);
            this.isSystem = isSystem;
        }

        public void unInstall(int position){
            AppUtils.uninstallApp(mData.get(position).getApkPackageName());
            mData.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public void convert(ViewHolder holder, AppInfo item, int position) {
            ImageView videoIcon = holder.itemView.findViewById(R.id.apk_icon);
            Glide.with(mContext).load(item.getIcon()).into(videoIcon);
            TextView videoName = holder.itemView.findViewById(R.id.apk_name);
            videoName.setText(item.getApkName());
            TextView videoSize = holder.itemView.findViewById(R.id.apk_size);
            String size = Formatter.formatFileSize(mContext, item.getApkSize());
            videoSize.setText(size);
            ImageView videoDelete = holder.itemView.findViewById(R.id.apk_delete);
            if (isSystem) videoDelete.setVisibility(View.GONE);
            else videoDelete.setVisibility(View.VISIBLE);
        }
    }

    public List<AppInfo> getApp(Context context, boolean isSystem) {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        //获取到包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获得所有的安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        //遍历每个安装包，获取对应的信息
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();
            appInfo.setApplicationInfo(packageInfo.applicationInfo);
            appInfo.setVersionCode(packageInfo.versionCode);
            //得到icon
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);
            //得到程序的名字
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);
            //得到程序的包名
            String packageName = packageInfo.packageName;
            appInfo.setApkPackageName(packageName);
            //得到程序的资源文件夹
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //得到apk的大小
            long size = file.length();
            appInfo.setApkSize(size);
            //设置安装日期
            appInfo.setInstallTime(packageInfo.firstInstallTime);
            //获取到安装应用程序的标记
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表示系统app
                appInfo.setUserApp(false);
            } else {
                //表示用户app
                appInfo.setUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //表示在sd卡
                appInfo.setRom(false);
            } else {
                //表示内存
                appInfo.setRom(true);
            }
            if (isSystem != appInfo.isUserApp() && !appInfo.getApkName().equals(context.getString(R.string.app_name))) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
}