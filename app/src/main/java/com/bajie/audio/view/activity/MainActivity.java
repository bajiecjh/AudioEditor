package com.bajie.audio.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bajie.audio.R;
import com.bajie.audio.adapter.MainPageAdapter;
import com.bajie.audio.base.BaseActivity;
import com.bajie.audio.entity.MainViewEntity;
import com.bajie.audio.utils.GlideEngine;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private MainPageAdapter adapter;
    private List<MainViewEntity> data = new ArrayList<>();

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
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择视频后，跳转到添加水印页面
                case PictureConfig.CHOOSE_REQUEST :
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    Intent intent = new Intent(MainActivity.this, AddWatermarkActivity.class);
                    intent.putExtra(AddWatermarkActivity.INTENT_EXTRA_PATH, selectList.get(0).getPath());
                    startActivity(intent);
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
                    case MainViewEntity.ACTIOIN_ADD_WATERMARK :
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

}
