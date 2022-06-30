package com.guppy.phoneclean.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.model.ApplicationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class FragmentSystem extends BaseFragment2<ApplicationViewModel> {
    private RecyclerView recyclerView;
    @Override
    protected Class<ApplicationViewModel> getViewClass() {
        return ApplicationViewModel.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_application,container,false);
    }

    @Override
    protected void initView() {
        recyclerView = getView().findViewById(R.id.application_list);
    }

    @Override
    protected void loadData() {
        viewModel.setDataAdapter(getContext(),  recyclerView, true);
    }
}
