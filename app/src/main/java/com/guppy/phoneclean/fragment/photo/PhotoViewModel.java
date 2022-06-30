package com.guppy.phoneclean.fragment.photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CommonRecyclerAdapter;
import com.guppy.phoneclean.adapter.OnItemClickListener;
import com.guppy.phoneclean.adapter.ViewHolder;
import com.guppy.phoneclean.bean.ImgFolderBean;
import com.guppy.phoneclean.model.BaseViewModel;
import com.guppy.phoneclean.utils.FileManager;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PhotoViewModel extends BaseViewModel {
    private MusiclistAdapter musiclistAdapter;
    private List<ImgFolderBean> imageFolders;


    public void setDataAdapter(Context context, RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
        imageFolders = FileManager.getInstance(context).getImageFolders();
        musiclistAdapter = new MusiclistAdapter(context, imageFolders, R.layout.list_item_photo);
        musiclistAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(musiclistAdapter);
    }
    public static class MusiclistAdapter extends CommonRecyclerAdapter<ImgFolderBean> {

        public MusiclistAdapter(Context context, List<ImgFolderBean> data, int layoutId) {
            super(context, data, layoutId);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void convert(ViewHolder holder, ImgFolderBean item, int position) {
            try {
                ImageView firstImage = holder.itemView.findViewById(R.id.first_image);
                Glide.with(mContext).load(item.getFistImgPath()).into(firstImage);
                TextView photoCameraFile = holder.itemView.findViewById(R.id.photo_camera_file);
                photoCameraFile.setText(item.getName()+" ("+item.getCount()+")");
            } catch (Exception e) {
                Log.e("=====", "===eee:::" + e.toString());
            }
        }
    }
}