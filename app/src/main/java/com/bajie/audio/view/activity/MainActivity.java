package com.bajie.audio.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bajie.audio.R;
import com.bajie.audio.adapter.MainPageAdapter;
import com.bajie.audio.base.BaseActivity;
import com.bajie.audio.entity.MainViewEntity;
import com.bajie.audio.entity.VideoInfo;
import com.bajie.audio.utils.FileUtils;
import com.bajie.audio.utils.GlideEngine;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    private static final int HANDLE_MSG_GET_VIDEO_INFO = 0;

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private MainPageAdapter adapter;
    private List<MainViewEntity> data = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == HANDLE_MSG_GET_VIDEO_INFO) {
                dismissLoadingDialog();
                AddWatermarkActivity.launch(MainActivity.this, (VideoInfo) msg.obj);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("bybajie MainActivity onCreate");
        Toast.makeText(this, "MainActivity onCreate", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择视频后，跳转到添加水印页面
                case PictureConfig.CHOOSE_REQUEST:
                    showLoadingDialog("加载视频...");
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    String path = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ? selectList.get(0).getAndroidQToPath() : selectList.get(0).getPath();
                    getVideoInfo(path);
                    break;
            }
        }
    }

    @Override
    protected void initView() {
        System.out.println("bybajie MainActivity initView");
        Toast.makeText(this, "initView", Toast.LENGTH_SHORT);
        data.add(new MainViewEntity(MainViewEntity.ITEM_FIRST));
        data.add(new MainViewEntity(MainViewEntity.ITEM_FIRST));
        data.add(new MainViewEntity(MainViewEntity.ITEM_FIRST));

        List<MainViewEntity.MainViewItemEntity> mainViewItemEntities = new ArrayList<>();
        mainViewItemEntities.add(new MainViewEntity.MainViewItemEntity(R.mipmap.icon_add_watermark, "添加水印", "文字 | 图片", MainViewEntity.ACTIOIN_ADD_WATERMARK));
        mainViewItemEntities.add(new MainViewEntity.MainViewItemEntity(R.mipmap.icon_add_watermark, "添加水印", "文字 | 图片", MainViewEntity.ACTIOIN_ADD_WATERMARK));
        data.add(new MainViewEntity(MainViewEntity.ITEM_SECOND, mainViewItemEntities));

        adapter = new MainPageAdapter(this, data);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

        adapter.setActionClickListener(new MainPageAdapter.ActionClickListener() {
            @Override
            public void onActionclick(int action) {
                switch (action) {
                    case MainViewEntity.ACTIOIN_ADD_WATERMARK:
                        // 点击添加水印
                        addWatermarkAction();
                        break;
                }
            }
        });
    }

    // 点击添加水印
    private void addWatermarkAction() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo()) // 媒体类型
                .loadImageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)    //单选
                .isPreviewVideo(false)  // 取消预览
                .isSingleDirectReturn(true) // 选中后直接返回
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }



    private void getVideoInfo(String filePath) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(filePath);
                String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int durationMs = Integer.parseInt(duration);
                int spacing = durationMs / 8;

                ArrayList<String> frames = new ArrayList<>();
                int j = 1;
                for (int i = 0; i < durationMs; i += spacing) {
                    Bitmap frameAtIndex = mediaMetadataRetriever.getFrameAtTime(i * 1000);
                    Bitmap frame = Bitmap.createScaledBitmap(frameAtIndex, frameAtIndex.getWidth() / 8, frameAtIndex.getHeight() / 8, false);
                    frameAtIndex.recycle();
                    try {
                        String imgPath = FileUtils.saveBitmapToDir(getApplication(), "frame" + j, frame);
                        j ++;
                        frames.add(imgPath);
                        frame.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                mediaMetadataRetriever.release();

                VideoInfo videoInfo = new VideoInfo();
                videoInfo.path = filePath;
                videoInfo.rotation = Integer.parseInt(rotation);
                videoInfo.width = Integer.parseInt(width);
                videoInfo.height = Integer.parseInt(height);
                videoInfo.duration = Integer.parseInt(duration);
                videoInfo.frames = frames;

                Message msg = new Message();
                msg.what = HANDLE_MSG_GET_VIDEO_INFO;
                msg.obj = videoInfo;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private String saveBitmapToDir(String fileName,Bitmap bitmap) throws IOException {
        String filePath = getExternalFilesDir(null).getAbsolutePath() + "/" + fileName + ".png";
        File file = new File(filePath);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
        return filePath;

    }

}
