package com.guppy.phoneclean.receiver;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import com.guppy.phoneclean.IClearApplication;
import com.guppy.phoneclean.MainActivity;
import com.guppy.phoneclean.activity.CacheClearActivity;
import com.guppy.phoneclean.dialog.InstallUninstallDialog;
import com.guppy.phoneclean.model.SysConfig;
import com.guppy.phoneclean.utils.DbUtils;

public class AppInstallUninstallReceiver extends BroadcastReceiver {
    private String type = null;
    private static final String TAG = AppInstallUninstallReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getSchemeSpecificPart();
        String action = intent.getAction();
        type = null;

        boolean disdesktopalert = isDisdesktopalert();
        if (!disdesktopalert) {
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                type = "added"; // 安装成功
                showDialog(IClearApplication.getContext(), packageName, "install successfully."/*应用安装*/, "App install,Check for sensitive permissions."/*应用安装，检查敏感权限*/, type, "EXIT", "CHECK FOR");
                //new InstallUninstallDialog1(context, packageName, "install successfully."/*应用安装*/, "App install,Check for sensitive permissions."/*应用安装，检查敏感权限*/, type, "EXIT", "CHECK FOR").show();
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                type = "removed"; // 卸载成功
                showDialog(IClearApplication.getContext(), packageName, "uninstall successfully."/*应用卸载*/, "App uninstall,clear up app residuals."/*应用卸载，清除应用残留*/, type, "EXIT", "CLEAR NOW");
                //new InstallUninstallDialog1(context, packageName, "uninstall successfully."/*应用卸载*/, "App uninstall,clear up app residuals."/*应用卸载，清除应用残留*/, type, "EXIT", "CLEAR NOW").show();
            }
            if (type != null) Log.i(TAG, "type:" + type + ",packageName:" + packageName);
        }
    }

    public boolean isDisdesktopalert() {
        // 是否禁用桌面弹框 0 允许; 1 禁止
        SysConfig sysConfig = DbUtils.getConfig();
        if (sysConfig != null) {
            return sysConfig.getDisdesktopalert() == 1;
        }
        return false;
    }

    private String getName(Context context, Intent intent) {
        return getName(context, intent.getDataString().substring(8));
    }

    private String getName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        String name = packageName;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                name = packageManager.getApplicationLabel(applicationInfo).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    private InstallUninstallDialog alertDialog;

    public void showDialog(Context context, String packageName, String name, String detailed, String type, String btn1, String btn2) {
        try {
            if (alertDialog == null) {
                alertDialog = new InstallUninstallDialog.Builder(context)
                        .setTitle(name)
                        .setCance(true)
                        .setText(detailed)
                        .setCancelButton(btn1/*稍后处理*/, dialog -> alertDialog = null)
                        .setConfirmButton(btn2/*立即处理*/, new InstallUninstallDialog.IDialogListenter() {
                            @Override
                            public void onClick(Dialog dialog) {
                                alertDialog = null;
                                if (type.equals("added")) {
                                    Intent i1 = new Intent(context, MainActivity.class);
                                    i1.putExtra("PACK", packageName);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i1);
                                } else if (type.equals("removed")) {
                                    Intent i1 = new Intent(context, CacheClearActivity.class);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i1);
                                } else {
                                    Intent i1 = new Intent(context, MainActivity.class);
                                    i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i1);
                                }
                            }
                        }).create();
            }
            alertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}


