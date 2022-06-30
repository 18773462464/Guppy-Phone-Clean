package com.guppy.phoneclean.fragment.document;

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

public class DocumentFragment extends BaseFragment<DocumentViewModel> {
    private static final String TAG = "DocumentFragment";
    private RecyclerView recyclerView;
    private ImageView imgDelete;
    private TextView textShow;
    @Override
    protected Class<DocumentViewModel> getViewClass() {
        return DocumentViewModel.class;
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
        textShow.setText(getResources().getString(R.string.string_name_bar_document));
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

    private void initData() {
        //Log.e("=====", TAG + "  loadData");
        viewModel.setDataAdapter(getContext(), recyclerView, false, imgDelete);
    }

    /*protected void loadData() {
        viewModel.setDataAdapter(getContext(), recyclerView, false, imgDelete);
    }*/
}
