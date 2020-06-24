package com.bajie.audio.view.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

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
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        addWatermarkRenderer = new AddWatermarkRenderer();

        mMediaPlayer = new MMediaPlayer();
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void setVideoPath(String path) {
        mMediaPlayer.setDataSource(path);
    }

    public void onDestroy() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

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
}
