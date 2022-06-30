package com.guppy.phoneclean.fragment.photo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.activity.CameraListActivity;
import com.guppy.phoneclean.adapter.OnItemClickListener;
import com.guppy.phoneclean.fragment.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoFragment extends BaseFragment<PhotoViewModel> implements OnItemClickListener {
    private static final String TAG = "PhotoFragment";
    private RecyclerView recyclerView;

    @Override
    protected Class<PhotoViewModel> getViewClass() {
        return PhotoViewModel.class;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_photo,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        //Log.e("=====", TAG + "  initView:");
        recyclerView = getView().findViewById(R.id.photo_list);
    }


    protected void initData() {
        //Log.e("=====", TAG + "  loadData");
        viewModel.setDataAdapter(getContext(), recyclerView, this);
    }


    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("POSTION_FILE", position);
        viewModel.openActivity(CameraListActivity.class, bundle);
    }


    protected void loadData() {
        viewModel.setDataAdapter(getContext(), recyclerView, this);
    }
}
