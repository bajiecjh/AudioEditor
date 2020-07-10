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

    private IMediaCallback iMediaCallback;

    public void setiMediaCallback(IMediaCallback iMediaCallback) {
        this.iMediaCallback = iMediaCallback;
    }

    public void prepare() throws IOException {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this.onCompletionListener);
        mMediaPlayer.setDataSource(videoInfo.path);
        mMediaPlayer.prepare();
        if(iMediaCallback != null) iMediaCallback.onVideoPrepare(videoInfo);
    }

    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    public void start() {
        mMediaPlayer.start();
        if(iMediaCallback != null) iMediaCallback.onVideoStart(mMediaPlayer.getCurrentPosition());
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }
    public void pause() {
        mMediaPlayer.pause();
    }
    // 毫秒
    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
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
    public void setDataSource(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(iMediaCallback != null) iMediaCallback.onVideoCompletion();
        }
    };


    public interface IMediaCallback {
        void onVideoPrepare(VideoInfo videoInfo);
        void onVideoStart(int duration);
        void onVideoCompletion();
    }


}
