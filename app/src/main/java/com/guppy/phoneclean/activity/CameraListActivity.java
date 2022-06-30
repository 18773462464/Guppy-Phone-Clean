package com.guppy.phoneclean.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.guppy.phoneclean.R;
import com.guppy.phoneclean.adapter.CommonRecyclerAdapter;
import com.guppy.phoneclean.adapter.ViewHolder;
import com.guppy.phoneclean.bean.CameraBean;
import com.guppy.phoneclean.bean.ImgFolderBean;
import com.guppy.phoneclean.dialog.TipDialog;
import com.guppy.phoneclean.utils.FileManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CameraListActivity extends BaseActivity {

    private static final String TAG = "CameraListActivity";
    private RecyclerView recyclerView;
    private ImageView imgAllcheck;
    private TextView toolbarTitle,textShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        initView();
        initData();
    }

    private void initView() {
        imgAllcheck = findViewById(R.id.img_allcheck);
        recyclerView = findViewById(R.id.camera_list);
        toolbarTitle = findViewById(R.id.toolbar_title);
        textShow = findViewById(R.id.text_show);
    }

    private void initData(){
        loadBannerAd();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        List<ImgFolderBean> imageFolders = FileManager.getInstance(this).getImageFolders();
        int postion_file = extras.getInt("POSTION_FILE", 0);
        ImgFolderBean imgFolderBean = imageFolders.get(postion_file);
        String name = imgFolderBean.getName();
        toolbarTitle.setText(name);
        textShow.setText(name);

        List<CameraBean> filesAllName = getFilesAllName(imgFolderBean.getDir());
        CameralistAdapter cameralistAdapter = new CameralistAdapter(this, filesAllName, R.layout.list_item_camera);
        cameralistAdapter.setItemSelectListenter(new ItemSelectListenter() {
            @Override
            public void onChangeSelect(int postion, boolean activated, int selectVideoCount, CameraBean music) {
                filesAllName.remove(postion);
                music.setAcctivated(activated);
                music.setAcctivatedCount(selectVideoCount);
                filesAllName.add(music);
                cameralistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChangeSelect(boolean selectAll) {
                imgAllcheck.setActivated(selectAll);
            }
        });
        recyclerView.setAdapter(cameralistAdapter);

        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TipDialog(CameraListActivity.this,cameralistAdapter).show();
                //cameralistAdapter.deleteSelectFiles();
            }
        });
        findViewById(R.id.toolbar_left_img).setOnClickListener(v -> {
            finish();
        });

    }

    public static class CameralistAdapter extends CommonRecyclerAdapter<CameraBean> {
        private int selectVideoCount = 0;
        private ItemSelectListenter itemSelectListenter;

        public void setItemSelectListenter(ItemSelectListenter itemSelectListenter) {
            this.itemSelectListenter = itemSelectListenter;
        }

        public CameralistAdapter(Context context, List<CameraBean> data, int layoutId) {
            super(context, data, layoutId);
            selectVideoCount = 0;
        }

        public void deleteSelectFiles() {
            for (int i = 0; i < mData.size(); i++) {
                CameraBean music = mData.get(i);
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


        @SuppressLint("SetTextI18n")
        @Override
        public void convert(ViewHolder holder, CameraBean item, int position) {
            try {
                ImageView firstImage = holder.itemView.findViewById(R.id.camera_view);
                Glide.with(mContext).load(item.getPath()).into(firstImage);
                ImageView selectPoint = holder.itemView.findViewById(R.id.select_point);
                boolean acctivated = item.isAcctivated();
                if (acctivated) {
                    selectVideoCount++;
                } else {
                    selectVideoCount--;
                    if (selectVideoCount < 0) selectVideoCount = 0;
                }
                selectPoint.setActivated(acctivated);
                if (itemSelectListenter != null) {
                    itemSelectListenter.onChangeSelect(mData.size() != 0 && selectVideoCount >= mData.size());
                }
                FrameLayout cameraFrame = holder.itemView.findViewById(R.id.camera_frame);
                cameraFrame.setOnClickListener(v -> {
                    boolean activated1 = !item.isAcctivated();
                    if (itemSelectListenter != null) {
                        itemSelectListenter.onChangeSelect(position, activated1, selectVideoCount, item);
                    }
                });
            } catch (Exception e) {
                System.out.println(TAG+"===eee:::" + e.toString() );
            }
        }
    }

    public interface ItemSelectListenter {
        void onChangeSelect(int postion, boolean activated, int selectVideoCount, CameraBean video);

        void onChangeSelect(boolean selectAll);
    }

    public static List<CameraBean> getFilesAllName(String path) {
        //传入指定文件夹的路径
        File file = new File(path);
        File[] files = file.listFiles();
        List<CameraBean> imagePaths = new LinkedList<>();
        for (int i = 0; i < files.length; i++) {
            if (checkIsImageFile(files[i].getPath())) {
                CameraBean cameraBean = new CameraBean(files[i].getPath(), false);
                imagePaths.add(cameraBean);
            }
        }
        return imagePaths;
    }

    /**
     * 判断是否是照片
     */
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (fileEnd.equals("jpg") || fileEnd.equals("png") || fileEnd.equals("gif")
                || fileEnd.equals("jpeg") || fileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }
}