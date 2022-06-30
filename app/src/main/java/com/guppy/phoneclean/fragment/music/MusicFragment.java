package com.guppy.phoneclean.fragment.music;

import android.media.MediaPlayer;
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

public class MusicFragment extends BaseFragment<MusicViewModel> {
    private static final String TAG = "MusicFragment";
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerView;
    private ImageView imgDelete;
    private TextView textShow;

    @Override
    protected Class<MusicViewModel> getViewClass() {
        return MusicViewModel.class;
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
        initData();
    }


    private void initView() {
        textShow = getView().findViewById(R.id.text_show);
        recyclerView = getView().findViewById(R.id.video_list);
        mediaPlayer = new MediaPlayer();
        textShow.setText(getResources().getString(R.string.string_name_bar_music));
        ImageView textSelectAll = getView().findViewById(R.id.text_select_all);;
        imgDelete = getView().findViewById(R.id.img_delete);;
        textSelectAll.setOnClickListener(v -> {
            boolean activated = !imgDelete.isActivated();
            viewModel.setDataAdapter(getContext(), mediaPlayer, recyclerView, activated, imgDelete);
            imgDelete.setActivated(activated);
            textSelectAll.setActivated(activated);
        });
        imgDelete.setOnClickListener(v -> {
            //viewModel.deleteSelectFiles();
            textSelectAll.setActivated(false);
            new TipDialog(getContext(),viewModel).show();
        });
    }


    private void initData() {
        //Log.e("=====", TAG + "  loadData");
        viewModel.setDataAdapter(getContext(), mediaPlayer, recyclerView, false, imgDelete);
    }

    private void loadData() {
        viewModel.setDataAdapter(getContext(), mediaPlayer, recyclerView, false, imgDelete);
    }
}
