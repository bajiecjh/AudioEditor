package com.bajie.audio.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bajie.audio.R;
import com.bajie.audio.base.BaseActivity;
import com.bajie.audio.entity.VideoInfo;
import com.bajie.audio.utils.DensityUtil;
import com.bajie.audio.utils.MMediaPlayer;
import com.bajie.audio.utils.TimeUtils;
import com.bajie.audio.view.widget.ActivityHeader;
import com.bajie.audio.view.widget.VideoPreviewView;

import butterknife.BindView;

import static android.media.MediaPlayer.SEEK_CLOSEST;

/**
 * bajie on 2020/6/19 12:21
 */
public class AddWatermarkActivity extends BaseActivity {

    public static final String INTENT_EXTRA_VIDEO_INFO = "videoInfo";
    private static final int HANDLE_MSG_REFRESH_VIDEO_TIME = 000;
    private static final int SCROLL_IMG_WIDTH_DP = 50;  // 滚动图片宽度
    private static final int SCROLL_IMG_MARGIN_DP = 2;  //  图片margin
    private static final int REFRESH_TIME_INTERVAL = 50;  //  图片margin

    @BindView(R.id.video_preview)
    VideoPreviewView mVideoPreview;
    @BindView(R.id.title_bar)
    ActivityHeader mTitleBar;
    @BindView(R.id.ll_frames)
    LinearLayout llFrames;
    @BindView(R.id.sv_frames)
    HorizontalScrollView svFrames;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_current_position)
    TextView tvCurrentPosition;
    @BindView(R.id.iv_pause)
    ImageView ivPause;
    @BindView(R.id.iv_play)
    ImageView ivPlay;


    private VideoInfo mVideoInfo;
    private int framesWidth;    // 滚动条图片总宽度,单位px
    private int halfScreenWidth;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_MSG_REFRESH_VIDEO_TIME:
                    updateCurrentPosition(mVideoPreview.getCurrentPosition());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_watermark);
    }

    public static void launch(Context context, VideoInfo videoInfo) {
        Intent intent = new Intent(context, AddWatermarkActivity.class);
        intent.putExtra(AddWatermarkActivity.INTENT_EXTRA_VIDEO_INFO, videoInfo);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        addVideoPlayerCallback();   // 设置播放回调
        initTitleBar();             // 初始化顶部bar
        getIntentInfo();            // 获取Intent传递过来的数据
        initFrames();               // 初始化底部图片滚动ui
        initDuration();             // 显示视频长度
        onClickPause();             // 点击暂停
        onClickPlay();              // 点击播放
        addScrollCallback();        // 滚动条滚动监听
    }

    private void addVideoPlayerCallback() {
        this.mVideoPreview.setiMediaCallback(new MMediaPlayer.IMediaCallback() {
            @Override
            public void onVideoPrepare(VideoInfo videoInfo) {}

            @Override
            public void onVideoStart(int duration) {
                System.out.println("bybajie:onVideoStart");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        while (mVideoPreview.isPlaying()) {
                            handler.sendEmptyMessage(HANDLE_MSG_REFRESH_VIDEO_TIME);

//                            System.out.println("bybajie:sendEmptyMessage");
                            try {
                                Thread.sleep(REFRESH_TIME_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }.start();
            }

            @Override
            public void onVideoCompletion() {   // 播放结束
                ivPause.setVisibility(View.INVISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                updateCurrentPosition(mVideoPreview.getDuration());
            }
        });
    }

    private void addScrollCallback() {
        svFrames.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {

                    // scrollview滚动的时候先暂停播放
                    pauseVideo();

                    int seekTo = (svFrames.getScrollX() * mVideoPreview.getDuration()) / framesWidth;
                    mVideoPreview.seekTo(seekTo, SEEK_CLOSEST);
                    System.out.println("bybajie: ACTION_MOVE seekTo=" + seekTo);
                }
                return false;
            }
        });
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
        mVideoInfo = (VideoInfo) intent.getSerializableExtra(INTENT_EXTRA_VIDEO_INFO);
        mVideoPreview.setVideoPath(mVideoInfo);
    }
    /** 初始化图片滚动条 **/
    private void initFrames() {
        // 为了滚动， 前面和后面各添加一张和背景同色的单色图片，宽度是屏幕宽度的一半
        halfScreenWidth = getScreenWidth() / 2;
        addBlackImgToScroll(halfScreenWidth);
        int frameSize = mVideoInfo.frames.size();
        for(int i = 0; i < frameSize; i ++) {
            Bitmap bitmap = BitmapFactory.decodeFile(mVideoInfo.frames.get(i));
            int width = DensityUtil.dip2px(this, SCROLL_IMG_WIDTH_DP);
            ImageView imageView = createImageView(width);
            imageView.setImageBitmap(bitmap);
            llFrames.addView(imageView);
        }
        addBlackImgToScroll(halfScreenWidth);
        framesWidth = DensityUtil.dip2px(this,frameSize * SCROLL_IMG_WIDTH_DP + SCROLL_IMG_MARGIN_DP * (frameSize-1));
    }
    /** 获取屏幕宽度 **/
    private int getScreenWidth() {
        int screenWidth;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        return screenWidth;
    }
    /** 添加一张屏幕一半宽度的黑色单色图片到滚动条中 **/
    private void addBlackImgToScroll(int halfScreenWidth) {
        ImageView imageView = createImageView(halfScreenWidth);
        imageView.setImageResource(R.mipmap.img_black);
        llFrames.addView(imageView);
    }
    /** 生成一张没有设置源的ImageView **/
    private ImageView createImageView(int width) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, DensityUtil.dip2px(this, SCROLL_IMG_WIDTH_DP));
        layoutParams.setMarginEnd(DensityUtil.dip2px(this, SCROLL_IMG_MARGIN_DP));
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    private void initDuration() {
        tvDuration.setText("总长 " + TimeUtils.formatTime(mVideoInfo.duration));
    }

    /** 设置当前进度 **/
    private void updateCurrentPosition(int millis) {
        tvCurrentPosition.setText(TimeUtils.formatTime(millis));
        int x = millis * framesWidth / mVideoPreview.getDuration();
        svFrames.scrollTo(x, 0);
    }


    /** 暂停播放 **/
    private void onClickPause() {
        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseVideo();
            }
        });
    }

    /** 暂停播放 **/
    private void pauseVideo() {
        if(mVideoPreview.isPlaying()) {
            mVideoPreview.pause();
            ivPause.setVisibility(View.INVISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
        }
    }
    private void onClickPlay() {
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoPreview.start();
                ivPause.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.INVISIBLE);
            }
        });
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
