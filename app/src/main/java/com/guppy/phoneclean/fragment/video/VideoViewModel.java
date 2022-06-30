package com.guppy.phoneclean.fragment.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CommonRecyclerAdapter;
import com.guppy.phoneclean.adapter.ViewHolder;
import com.guppy.phoneclean.bean.Video;
import com.guppy.phoneclean.model.BaseViewModel;
import com.guppy.phoneclean.utils.FileManager;
import com.guppy.phoneclean.utils.OpenFileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewModel extends BaseViewModel {
    private VideolistAdapter videolistAdapter;
    private List<Video> videoList;


    public void setDataAdapter(Context context, RecyclerView recyclerView, boolean allSelect, ImageView imgDelete) {
        videoList = FileManager.getInstance(context).getVideos(allSelect);
        videolistAdapter = new VideolistAdapter(context, videoList, R.layout.list_item_video);
        videolistAdapter.setItemSelectListenter(new ItemSelectListenter() {
            @Override
            public void onChangeSelect(int postion, boolean activated, int selectVideoCount, Video video) {
                videoList.remove(postion);
                video.setAcctivated(activated);
                video.setAcctivatedCount(selectVideoCount);
                videoList.add(postion,video);
                videolistAdapter.notifyItemChanged(postion);
            }

            @Override
            public void onChangeSelect(boolean selectAll) {
                imgDelete.setActivated(selectAll);
            }
        });
        recyclerView.setAdapter(videolistAdapter);
    }

    public void deleteSelectFiles() {
        videolistAdapter.deleteSelectFiles();
    }


    public static class VideolistAdapter extends CommonRecyclerAdapter<Video> {
        private ItemSelectListenter itemSelectListenter;
        private int selectVideoCount = 0;

        public void setItemSelectListenter(ItemSelectListenter itemSelectListenter) {
            this.itemSelectListenter = itemSelectListenter;
            selectVideoCount = 0;
        }

        public VideolistAdapter(Context context, List<Video> data, int layoutId) {
            super(context, data, layoutId);
        }

        public void deleteSelectFiles() {
            for (int i = 0; i < mData.size(); i++) {
                Video video = mData.get(i);
                if (video.isAcctivated()) {
                    boolean delete = FileUtils.delete(new File(video.getPath().trim()));
                    if (delete) {
                        selectVideoCount--;
                        if (selectVideoCount < 0) selectVideoCount = 0;
                        mData.remove(video);
                    }
                }
            }
            if (itemSelectListenter != null) {
                itemSelectListenter.onChangeSelect(mData.size() != 0 && selectVideoCount >= mData.size());
            }
            notifyDataSetChanged();
        }


        private String formatTime(long milliseconds) {
            String format = "HH:mm:ss";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return sdf.format(milliseconds);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void convert(ViewHolder holder, Video item, int position) {
            try {
                String path = item.getPath();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                if (path != null) {
                    mmr.setDataSource(path);
                }
                Bitmap bitmap = mmr.getFrameAtTime();//获取第一帧图片
                TextView videoSize = holder.itemView.findViewById(R.id.video_size);
                String size = Formatter.formatFileSize(mContext, item.getSize());
                String time = formatTime(item.getDuration());
                videoSize.setText(size + "   " + time);
                TextView videoName = holder.itemView.findViewById(R.id.video_name);
                videoName.setText(item.getName());

                ImageView frameAtTime = holder.itemView.findViewById(R.id.frame_attime);
                if (bitmap != null) frameAtTime.setImageBitmap(bitmap);

                FrameLayout play = holder.itemView.findViewById(R.id.play_fram);
                play.setOnClickListener(v -> {
                    //开始播放
                    Log.e("=====", "开始播放");
                    Intent intent = OpenFileUtil.openFile(path);
                    mContext.startActivity(intent);
                });

                LinearLayout videoContent = holder.itemView.findViewById(R.id.video_content);
                ImageView videoSelect = holder.itemView.findViewById(R.id.video_select);
                boolean activated = item.isAcctivated();
                videoSelect.setActivated(activated);
                if (activated) {
                    selectVideoCount++;
                } else {
                    selectVideoCount--;
                    if (selectVideoCount < 0) selectVideoCount = 0;
                }
                if (itemSelectListenter != null) {
                    itemSelectListenter.onChangeSelect(mData.size() != 0 && selectVideoCount >= mData.size());
                }
                View.OnClickListener onClickListener = v -> {
                    boolean activated1 = !item.isAcctivated();
                    if (itemSelectListenter != null) {
                        itemSelectListenter.onChangeSelect(position, activated1, selectVideoCount, item);
                    }
                };
                videoContent.setOnClickListener(onClickListener);
                videoSelect.setOnClickListener(onClickListener);
            } catch (Exception e) {
                Log.e("=====", "===eee:::" + e.toString());
            }
        }
    }

    public interface ItemSelectListenter {
        void onChangeSelect(int postion, boolean activated, int selectVideoCount, Video video);

        void onChangeSelect(boolean selectAll);
    }
}