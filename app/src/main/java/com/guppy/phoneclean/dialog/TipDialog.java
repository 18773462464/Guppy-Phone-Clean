package com.guppy.phoneclean.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.activity.CameraListActivity;
import com.guppy.phoneclean.fragment.apk.ApkViewModel;
import com.guppy.phoneclean.fragment.document.DocumentViewModel;
import com.guppy.phoneclean.fragment.music.MusicViewModel;
import com.guppy.phoneclean.fragment.video.VideoViewModel;

public class TipDialog extends BaseDialog {

    private  CameraListActivity.CameralistAdapter cameralistAdapter;
    private DocumentViewModel deleteSelectFiles;
    private ApkViewModel apkViewModel;
    private MusicViewModel musicViewModel;
    private VideoViewModel videoViewModel;

    public TipDialog(Context context, CameraListActivity.CameralistAdapter cameralistAdapter) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        fullWindowCenter(context);
        setContentView(view);
        setCancelable(false);
        this.cameralistAdapter = cameralistAdapter;
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    public TipDialog(Context context, DocumentViewModel deleteSelectFiles) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        fullWindowCenter(context);
        setContentView(view);
        this.deleteSelectFiles = deleteSelectFiles;
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    public TipDialog(Context context, ApkViewModel viewModel) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        fullWindowCenter(context);
        setContentView(view);
        this.apkViewModel = viewModel;
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    public TipDialog(Context context, MusicViewModel musicViewModel) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        fullWindowCenter(context);
        setContentView(view);
        this.musicViewModel = musicViewModel;
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    public TipDialog(Context context, VideoViewModel videoViewModel) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        fullWindowCenter(context);
        setContentView(view);
        this.videoViewModel = videoViewModel;
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.delete_btn:
                dismiss();
                if (cameralistAdapter != null) cameralistAdapter.deleteSelectFiles();
                if (deleteSelectFiles != null) deleteSelectFiles.deleteSelectFiles();
                if (apkViewModel != null) apkViewModel.deleteSelectFiles();
                if (musicViewModel != null) musicViewModel.deleteSelectFiles();
                if (videoViewModel != null) videoViewModel.deleteSelectFiles();
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
        }
    }
}