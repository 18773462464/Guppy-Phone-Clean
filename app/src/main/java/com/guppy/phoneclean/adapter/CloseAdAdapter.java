package com.guppy.phoneclean.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.MuteThisAdReason;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.model.MainFunctionModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CloseAdAdapter extends RecyclerView.Adapter<CloseAdAdapter.ViewHolder>{

    private List<MuteThisAdReason> list;
    private CloseAdAdapter.OnItemClickLitener onItemClickLitener;

    public CloseAdAdapter(List<MuteThisAdReason> list, CloseAdAdapter.OnItemClickLitener onItemClickLitener){
        this.list = list;
        this.onItemClickLitener = onItemClickLitener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_close_ad, parent, false);
        return new CloseAdAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MuteThisAdReason muteThisAdReason = list.get(position);
        holder.txt.setText(muteThisAdReason.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLitener.onItemClick(position, muteThisAdReason);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.item_txt);
        }
    }

    public interface OnItemClickLitener{
        void onItemClick(int position, MuteThisAdReason model);
    }
}
