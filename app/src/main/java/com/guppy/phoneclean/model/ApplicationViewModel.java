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
        //????????????????????????
        PackageManager packageManager = context.getPackageManager();
        //????????????????????????
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        //?????????????????????????????????????????????
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();
            appInfo.setApplicationInfo(packageInfo.applicationInfo);
            appInfo.setVersionCode(packageInfo.versionCode);
            //??????icon
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);
            //?????????????????????
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);
            //?????????????????????
            String packageName = packageInfo.packageName;
            appInfo.setApkPackageName(packageName);
            //??????????????????????????????
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //??????apk?????????
            long size = file.length();
            appInfo.setApkSize(size);
            //??????????????????
            appInfo.setInstallTime(packageInfo.firstInstallTime);
            //????????????????????????????????????
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //????????????app
                appInfo.setUserApp(false);
            } else {
                //????????????app
                appInfo.setUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //?????????sd???
                appInfo.setRom(false);
            } else {
                //????????????
                appInfo.setRom(true);
            }
            if (isSystem != appInfo.isUserApp() && !appInfo.getApkName().equals(context.getString(R.string.app_name))) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
}