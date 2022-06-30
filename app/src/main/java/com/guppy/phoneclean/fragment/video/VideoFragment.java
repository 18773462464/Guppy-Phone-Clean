package com.guppy.phoneclean.fragment.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.dialog.TipDialog;
import com.guppy.phoneclean.fragment.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class VideoFragment extends BaseFragment<VideoViewModel> {
    private static final String TAG = "VideoFragment";
    private RecyclerView recyclerView;
    private  ImageView imgDelete;
    private TextView textShow;

    @Override
    protected Class<VideoViewModel> getViewClass() {
        return VideoViewModel.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_videos,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadData();
    }

    protected void initView() {
        //Log.e("=====", TAG + "  initView");
        textShow = getView().findViewById(R.id.text_show);
        textShow.setText(getResources().getString(R.string.string_name_bar_video));
        recyclerView = getView().findViewById(R.id.video_list);
        ImageView textSelectAll = getView().findViewById(R.id.text_select_all);
        imgDelete = getView().findViewById(R.id.img_delete);
        textSelectAll.setOnClickListener(v -> {
            boolean activated = !imgDelete.isActivated();
            viewModel.setDataAdapter(getContext(), recyclerView, activated, imgDelete);
            imgDelete.setActivated(activated);
            textSelectAll.setActivated(activated);
        });
        imgDelete.setOnClickListener(v -> {
            //viewModel.deleteSelectFiles();
            textSelectAll.setActivated(false);
            new TipDialog(getContext(),viewModel).show();
        });
    }

    protected void loadData() {
        viewModel.setDataAdapter(getContext(), recyclerView, false, imgDelete);
    }

}
