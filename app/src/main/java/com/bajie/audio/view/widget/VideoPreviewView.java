package com.bajie.audio.view.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

import com.bajie.audio.R;
import com.bajie.audio.entity.VideoInfo;
import com.bajie.audio.utils.renderer.AddWatermarkRenderer;
import com.bajie.audio.utils.MMediaPlayer;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * bajie on 2020/6/19 12:18
 */
public class VideoPreviewView extends GLSurfaceView {
    private MMediaPlayer mMediaPlayer;
    private AddWatermarkRenderer addWatermarkRenderer;

    private MMediaPlayer.IMediaCallback iMediaCallback;

    public void setiMediaCallback(MMediaPlayer.IMediaCallback iMediaCallback) {
        this.iMediaCallback = iMediaCallback;
    }

    public VideoPreviewView(Context context) {
        super(context);
    }

    public VideoPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //GLContext设置OpenGLES2.0
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        /*渲染方式，RENDERMODE_WHEN_DIRTY表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。RENDERMODE_CONTINUOUSLY表示持续渲染*/
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        addWatermarkRenderer = new AddWatermarkRenderer(getResources());

        mMediaPlayer = new MMediaPlayer();
        mMediaPlayer.setiMediaCallback(mediaCallback);
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }
    public void setVideoPath(VideoInfo videoPath) {
        mMediaPlayer.setDataSource(videoPath);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void seekTo(int msec, int seekClosest) {
        mMediaPlayer.seekTo(msec);
    }

    public void onDestroy() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

    private MMediaPlayer.IMediaCallback mediaCallback = new MMediaPlayer.IMediaCallback() {

        @Override
        public void onVideoPrepare(VideoInfo videoInfo) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    addWatermarkRenderer.onVidePrepare(videoInfo);
                    if(iMediaCallback != null) iMediaCallback.onVideoPrepare(videoInfo);
                }
            });
//
        }

        @Override
        public void onVideoStart(int duration) {
            if(iMediaCallback != null) iMediaCallback.onVideoStart(duration);
        }

        @Override
        public void onVideoCompletion() {
            if(iMediaCallback != null) iMediaCallback.onVideoCompletion();
        }
    };

    private Renderer renderer = new Renderer() {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            addWatermarkRenderer.setSurfaceTextureListener(new AddWatermarkRenderer.OnSurfaceTextureListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    requestRender();
                }
            });
            addWatermarkRenderer.onSurfaceCreated(gl, config);
            Surface surface = addWatermarkRenderer.surface;
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 设置MediaPlayer的显示表面，将MediaPlayer和OpenGL联系起来
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.start();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            addWatermarkRenderer.onSurfaceChanged(gl, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            addWatermarkRenderer.onDrawFrame(gl);
        }
    };

    public Surface getSurface() {
        return addWatermarkRenderer.surface;
    }
}
