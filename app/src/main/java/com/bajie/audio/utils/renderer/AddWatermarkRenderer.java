package com.bajie.audio.utils.renderer;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.bajie.audio.utils.DirectDrawer;
import com.bajie.audio.utils.filter.AFilter;
import com.bajie.audio.utils.filter.NoFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * bajie on 2020/6/19 17:38
 */
public class AddWatermarkRenderer implements GLSurfaceView.Renderer {

    private SurfaceTexture mSurfaceTexture;
    public Surface surface;
    int mTextureID = -1;
    private DirectDrawer mDirectDrawer;

    // 显示视频的滤镜
    private AFilter mShowFilter;

    private int viewWidth;
    private int viewHeight;

    // 创建离屏的buffer
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    private OnSurfaceTextureListener mSurfaceTextureListener;

    public void setSurfaceTextureListener(OnSurfaceTextureListener surfaceTextureListener) {
        mSurfaceTextureListener = surfaceTextureListener;
    }

    public AddWatermarkRenderer(Resources res) {
        mShowFilter = new NoFilter(res);
    }

    @Override
    // Surface被创建后需要做的处理
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureID = createTextureID();
        // 根据纹理ID生成SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                // 监听是否有新的一帧数据到来
                if(mSurfaceTextureListener != null) {
                    mSurfaceTextureListener.onFrameAvailable(surfaceTexture);
                }
            }
        });
        mDirectDrawer = new DirectDrawer(mTextureID);
        surface = new Surface(mSurfaceTexture);
        mShowFilter.create();
    }

    @Override
    // 渲染窗口大小发生改变或者屏幕方向发生改变时的回调
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, height, width);

    }

    @Override
    // 执行渲染工作
    public void onDrawFrame(GL10 gl) {
        // 获取新数据
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);
    }

    public interface OnSurfaceTextureListener {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    public int createTextureID()
    {
        int[] texture = new int[1];

        /**
         * 产生n个纹理ID存储在textures数组中，生成的textture此时是没有维度的，当他们第一次绑定纹理目标时才被制定维度
         * 参数：n 制定要生成的纹理ID的数量
         *      textures 制定存储生成的纹理ID的数组
         */
        GLES20.glGenTextures(1, texture, 0);
        /**
         * 将已经激活了的纹理绑定到一个目标，当一个纹理ID绑定到目标时，这个目标之前的绑定关系就会自动解除
         * target:指定之前激活了的纹理要绑定到的一个目标
         * texture:指定纹理ID
         */
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);

        /**
         * 设置纹理参数
         * GL_TEXTURE_MIN_FILTER 只要纹理贴图比要贴的区域大，就会使用这个纹理缩小功能
         * GL_TEXTURE_MAG_FILTER 当纹理化的像素映射到小于或等于一个纹理元素的区域时，使用纹理放大功能
         */
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

//        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
}
