package com.guppy.phoneclean.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guppy.phoneclean.R;
import com.guppy.phoneclean.model.MainFunctionModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainFunctionAdapter extends RecyclerView.Adapter<MainFunctionAdapter.ViewHolder> {

    private List<MainFunctionModel> list;
    private OnItemClickLitener onItemClickLitener;

    public MainFunctionAdapter(List<MainFunctionModel> list, OnItemClickLitener onItemClickLitener){
        this.list = list;
        this.onItemClickLitener = onItemClickLitener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_function, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainFunctionModel model = list.get(position);
        holder.icon.setImageResource(model.getIcon());
        holder.name.setText(model.getName());
        holder.describe.setText(model.getDescribe());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLitener.onItemClick(position,model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name,describe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            name = itemView.findViewById(R.id.tv_name);
            describe = itemView.findViewById(R.id.tv_describe);
        }
    }

    public interface OnItemClickLitener{
        void onItemClick(int position, MainFunctionModel model);
    }
}
