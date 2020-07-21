package com.bajie.audio.utils.renderer;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.bajie.audio.entity.VideoInfo;
import com.bajie.audio.utils.opengl.filter.RotationOESFilter;
import com.bajie.audio.utils.opengl.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * bajie on 2020/6/19 17:38
 */
public class AddWatermarkRenderer implements GLSurfaceView.Renderer {


    /**用于显示的变换矩阵*/
    private float[] SM = new float[16];

    private SurfaceTexture mSurfaceTexture;
    public Surface surface;
    int mTextureID = -1;
//    private DirectDrawer mDirectDrawer;

    // 用于预览视频的filter
    private RotationOESFilter mOESFilter;

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
        // 这里会对顶点和纹理的buffer进行初始化
        mOESFilter = new RotationOESFilter(res);
    }

    public void onVidePrepare(VideoInfo videoInfo) {
        if(mOESFilter != null) {
            mOESFilter.setRotation(videoInfo.rotation);
            if(videoInfo.rotation == 0 || videoInfo.rotation == 180) {
                MatrixUtils.getShowMatrix(SM,videoInfo.width,videoInfo.height,viewWidth,viewHeight);
            } else {
                MatrixUtils.getShowMatrix(SM,videoInfo.height,videoInfo.width,viewWidth,viewHeight);
            }
            mOESFilter.setMatrix(SM);
        }
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
//        mDirectDrawer = new DirectDrawer(mTextureID);
        surface = new Surface(mSurfaceTexture);
        // 创建GLProgram,获取OpenGL的各种句柄
        mOESFilter.create();
        mOESFilter.setTextureId(mTextureID);
    }

    @Override
    // 渲染窗口大小发生改变或者屏幕方向发生改变时的回调
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        viewHeight = height;
        viewWidth = width;
//        // 删除缓冲区buffer
//        GLES20.glDeleteFramebuffers(1, fFrame, 0);
//        // 删除纹理
//        GLES20.glDeleteTextures(1, fTexture, 0);
//        /** 创建一个帧缓冲区对象 */
//        GLES20.glGenFramebuffers(1, fFrame, 0);
//        /** 创建纹理 */
//        GLES20.glGenTextures(1, fTexture, 0);
//        /**将已经激活了的纹理绑定到一个目标  */
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
//        /**根据指定的参数 生产一个2D的纹理 调用该函数前  必须调用glBindTexture以指定要操作的纹理*/
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

    }

    @Override
    // 执行渲染工作
    public void onDrawFrame(GL10 gl) {
        /** 更新Surface中的纹理 */
        mSurfaceTexture.updateTexImage();
        /**绘制显示的filter*/
        GLES20.glViewport(0,0,viewWidth,viewHeight);
        mOESFilter.draw();

        // 获取新数据
//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        mSurfaceTexture.updateTexImage();
//        float[] mtx = new float[16];
//        mSurfaceTexture.getTransformMatrix(mtx);
//        mDirectDrawer.draw(mtx);
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

        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
}
