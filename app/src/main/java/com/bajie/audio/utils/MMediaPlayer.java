package com.bajie.audio.utils;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Surface;

import com.bajie.audio.entity.VideoInfo;

import java.io.IOException;

/**
 * bajie on 2020/6/19 14:21
 */
public class MMediaPlayer {
    private MediaPlayer mMediaPlayer;
    private Surface surface;
    private VideoInfo videoInfo;

    public void prepare() throws IOException {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this.onCompletionListener);
        mMediaPlayer.setOnErrorListener(this.onErrorListener);
        mMediaPlayer.setOnPreparedListener(this.onPreparedListener);
        mMediaPlayer.setDataSource(videoInfo.path);
        mMediaPlayer.prepare();
    }

    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void release() {
        mMediaPlayer.release();
    }

    // 设置视频播放源
    public void setDataSource(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        videoInfo = new VideoInfo();
        videoInfo.path = path;
        videoInfo.rotation = Integer.parseInt(rotation);
        videoInfo.width = Integer.parseInt(width);
        videoInfo.height = Integer.parseInt(height);
        videoInfo.duration = Integer.parseInt(duration);
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

        }
    };


}
