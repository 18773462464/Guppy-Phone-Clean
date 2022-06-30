package com.guppy.phoneclean.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.guppy.phoneclean.R;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InstallUninstallDialog extends Dialog {

    public interface IDialogListenter {
        void onClick(Dialog dialog);
    }

    protected InstallUninstallDialog(@NonNull @NotNull Context context) {
        this(context, R.style.InstallUinstallWindowDialog);
    }

    protected InstallUninstallDialog(@NonNull @NotNull Context context, int themeResId) {
        super(context, themeResId);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public static class Builder {
        private boolean flag = false;
        private View view;
        private IDialogListenter listenterPositive, listenterNegative;
        private final InstallUninstallDialog iAlertDialog;

        public TextView dialogTipImg,dialogTipTxt;
        public TextView deleteBtn,cancelBtn;

        public Builder(Context context) {
            iAlertDialog = new InstallUninstallDialog(context);
            view = LayoutInflater.from(context).inflate(R.layout.dialog_install_uninstall, null);
           /* LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = WindowDialogItemBinding.inflate(inflater);*/
            //添加布局文件到 Dialog
            iAlertDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            iAlertDialog.setContentView(view);
            dialogTipImg = view.findViewById(R.id.dialog_tip_title);
            dialogTipTxt = view.findViewById(R.id.dialog_tip_txt);
            deleteBtn = view.findViewById(R.id.delete_btn);
            cancelBtn = view.findViewById(R.id.cancel_btn);

        }


        public Builder setCance(boolean flag) {
            this.flag = flag;
            return this;
        }

        public Builder setText(@Nullable String text) {
            dialogTipTxt.setText(text);
            dialogTipTxt.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setTitle(@Nullable String text) {
            dialogTipImg.setText(text);
            dialogTipImg.setVisibility(View.VISIBLE);
            return this;
        }


        public Builder setConfirmButton(@Nullable String text, final IDialogListenter listener) {
            this.listenterPositive = listener;
            deleteBtn.setText(text);
            if (listenterPositive != null) deleteBtn.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setCancelButton(final IDialogListenter listener) {
            this.listenterNegative = listener;
            if (listener != null) cancelBtn.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setCancelButton(String text, final IDialogListenter listener) {
            this.listenterNegative = listener;
            if (listener != null) {
                cancelBtn.setText(text);
                cancelBtn.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public InstallUninstallDialog create() {
            deleteBtn.setOnClickListener(v -> {
                iAlertDialog.dismiss();
                if (listenterPositive != null) {
                    listenterPositive.onClick(iAlertDialog);
                }
            });
            cancelBtn.setOnClickListener(v -> {
                iAlertDialog.dismiss();
                if (listenterNegative != null) {
                    listenterNegative.onClick(iAlertDialog);
                }
            });
            iAlertDialog.setCancelable(flag);                //用户可以点击后退键关闭 Dialog
            iAlertDialog.setCanceledOnTouchOutside(!flag);   //用户不可以点击外部来关闭 Dialog
            return iAlertDialog;
        }
    }

    @Override
    public void show() {
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        dialogWindow.setType(WindowManager.LayoutParams.TYPE_TOAST);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //设置Dialog从窗体底部弹出
        //dialogWindow.setGravity(Gravity.TOP);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 5;//设置Dialog距离底部的距离
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        super.show();
    }

}

