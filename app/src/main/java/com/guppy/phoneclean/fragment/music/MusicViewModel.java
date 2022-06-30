package com.guppy.phoneclean.fragment.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.guppy.phoneclean.IConstant;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CommonRecyclerAdapter;
import com.guppy.phoneclean.adapter.ViewHolder;
import com.guppy.phoneclean.bean.Music;
import com.guppy.phoneclean.model.BaseViewModel;
import com.guppy.phoneclean.utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;

public class MusicViewModel extends BaseViewModel {
    private MusiclistAdapter musiclistAdapter;
    private List<Music> musicList;


    public void setDataAdapter(Context context, MediaPlayer mediaPlayer, RecyclerView recyclerView, boolean allSelect, ImageView imgDelete) {
        musicList = FileManager.getInstance(context).getMusics(allSelect);
        musiclistAdapter = new MusiclistAdapter(mediaPlayer,context, musicList, R.layout.list_item_music);
        musiclistAdapter.setItemSelectListenter(new ItemSelectListenter() {
            @Override
            public void onChangeSelect(int postion, boolean activated, int selectVideoCount, Music music) {
                musicList.remove(postion);
                music.setAcctivated(activated);
                music.setAcctivatedCount(selectVideoCount);
                musicList.add(postion,music);
                musiclistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChangeSelect(boolean selectAll) {
                imgDelete.setActivated(selectAll);
            }
        });
        recyclerView.setAdapter(musiclistAdapter);
    }

    public void deleteSelectFiles() {
        musiclistAdapter.deleteSelectFiles();
    }


    public static class MusiclistAdapter extends CommonRecyclerAdapter<Music> {
        private ItemSelectListenter itemSelectListenter;
        private int selectVideoCount = 0;
        private MediaPlayer mediaPlayer;

        public void setItemSelectListenter(ItemSelectListenter itemSelectListenter) {
            this.itemSelectListenter = itemSelectListenter;
            selectVideoCount = 0;
        }

        public MusiclistAdapter(MediaPlayer mediaPlayer, Context context, List<Music> data, int layoutId) {
            super(context, data, layoutId);
            this.mediaPlayer = mediaPlayer;
        }

        public void deleteSelectFiles() {
            for (int i = 0; i < mData.size(); i++) {
                Music music = mData.get(i);
                if (music.isAcctivated()) {
                    boolean delete = FileUtils.delete(new File(music.getPath().trim()));
                    if (delete) {
                        selectVideoCount--;
                        if (selectVideoCount < 0) selectVideoCount = 0;
                        mData.remove(music);
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
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return sdf.format(milliseconds);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void convert(ViewHolder holder, Music item, int position) {
            try {
                String path = item.getPath();
                TextView videoSize = holder.itemView.findViewById(R.id.music_size);
                String size = Formatter.formatFileSize(mContext, item.getSize());
                String time = formatTime(item.getDuration());
                videoSize.setText(size + "   " + time);
                TextView videoName = holder.itemView.findViewById(R.id.music_name);
                videoName.setText(item.getName());

                FrameLayout play = holder.itemView.findViewById(R.id.play_fram);
                LinearLayout videoContent = holder.itemView.findViewById(R.id.music_content);
                View.OnClickListener onClickListener = v -> {
                    //开始播放
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    } else {
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(path);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                        } catch (IOException e) {
                            if (IConstant.IS_DEBUG) e.printStackTrace();
                        }
                    }
                };
                videoContent.setOnClickListener(onClickListener);
                play.setOnClickListener(onClickListener);


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
                play.setOnClickListener(v -> {
                    boolean activated1 = !item.isAcctivated();
                    if (itemSelectListenter != null) {
                        itemSelectListenter.onChangeSelect(position, activated1, selectVideoCount, item);
                    }
                });
                videoSelect.setOnClickListener(onClickListener);
            } catch (Exception e) {
                Log.e("=====", "===eee:::" + e.toString());
            }
        }
    }

    public interface ItemSelectListenter {
        void onChangeSelect(int postion, boolean activated, int selectVideoCount, Music video);

        void onChangeSelect(boolean selectAll);
    }
}