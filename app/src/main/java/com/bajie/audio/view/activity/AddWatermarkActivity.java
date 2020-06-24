package com.bajie.audio.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bajie.audio.R;
import com.bajie.audio.base.BaseActivity;
import com.bajie.audio.view.widget.ActivityHeader;
import com.bajie.audio.view.widget.VideoPreviewView;

import butterknife.BindView;

/**
 * bajie on 2020/6/19 12:21
 */
public class AddWatermarkActivity extends BaseActivity {

    public static final String INTENT_EXTRA_PATH = "path";

    @BindView(R.id.video_preview)
    VideoPreviewView mVideoPreview;
    @BindView(R.id.title_bar)
    ActivityHeader mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_watermark);
    }

    @Override
    protected void initView() {
        initTitleBar();
        getIntentInfo();


    }

    private void initTitleBar() {
        mTitleBar.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWatermarkActivity.this.finish();
            }
        });
    }

    private void getIntentInfo() {
        // 获取视频路径
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra(INTENT_EXTRA_PATH);
        mVideoPreview.setVideoPath(videoPath);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mVideoPreview.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPreview.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoPreview != null) {
            mVideoPreview.onDestroy();
        }
    }
}
