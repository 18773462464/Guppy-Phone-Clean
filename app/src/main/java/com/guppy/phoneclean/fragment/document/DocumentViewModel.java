package com.guppy.phoneclean.fragment.document;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CommonRecyclerAdapter;
import com.guppy.phoneclean.adapter.ViewHolder;
import com.guppy.phoneclean.bean.FileBean;
import com.guppy.phoneclean.model.BaseViewModel;
import com.guppy.phoneclean.utils.FileManager;
import com.guppy.phoneclean.utils.OpenFileUtil;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DocumentViewModel extends BaseViewModel {
    private DocumentlistAdapter documentlistAdapter;
    private List<FileBean> fileList;


    public void setDataAdapter(Context context, RecyclerView recyclerView, boolean allSelect, ImageView imgDelete) {
        fileList = FileManager.getInstance(context).getFilesByType(com.guppy.phoneclean.utils.FileUtils.TYPE_DOC, allSelect);
        documentlistAdapter = new DocumentlistAdapter(context, fileList, R.layout.list_item_document);
        documentlistAdapter.setItemSelectListenter(new ItemSelectListenter() {
            @Override
            public void onChangeSelect(int postion, boolean activated, int selectVideoCount, FileBean video) {
                fileList.remove(postion);
                video.setAcctivated(activated);
                video.setAcctivatedCount(selectVideoCount);
                fileList.add(postion,video);
                documentlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChangeSelect(boolean selectAll) {
                imgDelete.setActivated(selectAll);
            }
        });
        recyclerView.setAdapter(documentlistAdapter);
    }

    public void deleteSelectFiles() {
        documentlistAdapter.deleteSelectFiles();
    }


    public static class DocumentlistAdapter extends CommonRecyclerAdapter<FileBean> {
        private ItemSelectListenter itemSelectListenter;
        private int selectVideoCount = 0;

        public void setItemSelectListenter(ItemSelectListenter itemSelectListenter) {
            this.itemSelectListenter = itemSelectListenter;
            selectVideoCount = 0;
        }

        public DocumentlistAdapter(Context context, List<FileBean> data, int layoutId) {
            super(context, data, layoutId);
        }

        public void deleteSelectFiles() {
            for (int i = 0; i < mData.size(); i++) {
                FileBean fileBean = mData.get(i);
                if (fileBean.isAcctivated()) {
                    boolean delete = FileUtils.delete(new File(fileBean.getPath().trim()));
                    if (delete) {
                        selectVideoCount--;
                        if (selectVideoCount < 0) selectVideoCount = 0;
                        mData.remove(fileBean);
                    }
                }
            }
            if (itemSelectListenter != null) {
                itemSelectListenter.onChangeSelect(mData.size() != 0 && selectVideoCount >= mData.size());
            }
            notifyDataSetChanged();
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void convert(ViewHolder holder, FileBean item, int position) {
            try {
                String path = item.getPath();

                TextView videoSize = holder.itemView.findViewById(R.id.document_size);
                String autoFileOrFilesSize = com.guppy.phoneclean.utils.FileUtils.getAutoFileOrFilesSize(path);
                videoSize.setText(autoFileOrFilesSize);
                TextView videoName = holder.itemView.findViewById(R.id.document_name);
                videoName.setText(new File(path).getName());

                ImageView play = holder.itemView.findViewById(R.id.play_document);
                play.setOnClickListener(v -> {
                    //开始播放
                    Intent intent = OpenFileUtil.openFile(path);
                    mContext.startActivity(intent);
                });

                LinearLayout videoContent = holder.itemView.findViewById(R.id.document_content);
                ImageView videoSelect = holder.itemView.findViewById(R.id.document_select);
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
        void onChangeSelect(int postion, boolean activated, int selectVideoCount, FileBean video);

        void onChangeSelect(boolean selectAll);
    }
}