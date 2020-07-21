package com.bajie.audio.utils.opengl.filter;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;


import com.bajie.audio.utils.opengl.MatrixUtils;
import com.bajie.audio.utils.opengl.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Description:
 */
public abstract class AFilter {

    private static final String TAG="Filter";


    public static boolean DEBUG=true;
    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    protected Resources mRes;


    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    /**
     * 索引坐标Buffer
     */

    protected int mFlag=0;

    private float[] matrix= Arrays.copyOf(OM,16);

    private int textureType=0;      //默认使用Texture2D0
    private int textureId=0;
    //顶点坐标
    private float pos[] = {
        -1.0f,  1.0f,
        -1.0f, -1.0f,
        1.0f, 1.0f,
        1.0f,  -1.0f,
    };

    //纹理坐标-相机预览时对片元着色器使用的是纹理texture而不是颜色color
    private float[] coord={
        0.0f, 0.0f,
        0.0f,  1.0f,
        1.0f,  0.0f,
        1.0f, 1.0f,
    };

    private SparseArray<boolean[]> mBools;
    private SparseArray<int[]> mInts;
    private SparseArray<float[]> mFloats;


    public AFilter(Resources mRes){
        this.mRes=mRes;
        initBuffer();
    }

    public final void create(){
        onCreate();
    }

    public final void setSize(int width,int height){
        onSizeChanged(width,height);
    }

    public void draw(){
//        Log.e("videoo", "---卡主了？ 16  "+getClass());
        onClear();
//        Log.e("videoo", "---卡主了？ 17");
        onUseProgram();
//        Log.e("videoo", "---卡主了？ 18");
        onSetExpandData();
//        Log.e("videoo", "---卡主了？ 19");
        onBindTexture();
//        Log.e("videoo", "---卡主了？ 20");
        onDraw();
//        Log.e("videoo", "---卡主了？ 21");
    }

    public final void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    public float[] getMatrix(){
        return matrix;
    }

    public final void setTextureType(int type){
        this.textureType=type;
    }

    public final int getTextureType(){
        return textureType;
    }

    public final int getTextureId(){
        return textureId;
    }

    public final void setTextureId(int textureId){
        this.textureId=textureId;
    }

    public void setFlag(int flag){
        this.mFlag=flag;
    }

    public int getFlag(){
        return mFlag;
    }

    public void setFloat(int type,float ... params){
        if(mFloats==null){
            mFloats=new SparseArray<>();
        }
        mFloats.put(type,params);
    }
    public void setInt(int type,int ... params){
        if(mInts==null){
            mInts=new SparseArray<>();
        }
        mInts.put(type,params);
    }
    public void setBool(int type,boolean ... params){
        if(mBools==null){
            mBools=new SparseArray<>();
        }
        mBools.put(type,params);
    }

    public boolean getBool(int type,int index) {
        if (mBools == null) return false;
        boolean[] b = mBools.get(type);
        return !(b == null || b.length <= index) && b[index];
    }

    public int getInt(int type,int index){
        if (mInts == null) return 0;
        int[] b = mInts.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public float getFloat(int type,int index){
        if (mFloats == null) return 0;
        float[] b = mFloats.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public int getOutputTexture(){
        return -1;
    }

    /**
     * 实现此方法，完成程序的创建，可直接调用createProgram来实现
     */
    protected abstract void onCreate();
    protected abstract void onSizeChanged(int width,int height);

    protected final void createProgram(String vertex, String fragment){
        mProgram= uCreateGlProgram(vertex,fragment);
        /**
         * 获取shader代码中的变量索引，用于在后面的绘制代码中进行赋值
         * 变量索引在glsl程式生命周期内都是固定的，只需要获取一次
         */
        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord= GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture= GLES20.glGetUniformLocation(mProgram,"vTexture");
    }

    protected final void createProgramByAssetsFile(String vertex, String fragment){
        createProgram(OpenGlUtils.uRes(vertex),OpenGlUtils.uRes(fragment));
    }

    /**
     * Buffer初始化
     */
    protected void initBuffer(){

        // 装载顶点坐标
        ByteBuffer a= ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer=a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);

        // 装载纹理坐标
        ByteBuffer b= ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer=b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    protected void onUseProgram(){
        GLES20.glUseProgram(mProgram);
    }

    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    protected void onDraw(){
        // 啓用vertex
        GLES20.glEnableVertexAttribArray(mHPosition);
        // 綁定vertex坐標值，调用glVertexAttribPointer告诉opengl，它可以再缓冲区mVerBuffer中获取vPositioni的数据
        // 使用一次glEnableVertexAttribArray方法就要使用一次glVertexAttribPointer方法
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        // 通过GLE20.glDrawArrays或者GLE20.glDrawElements开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        // 使用了glEnableVertexAttribArray方法就必须使用glDisableVertexAttribArray方法
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * 清除画布
     */
    protected void onClear(){

//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        GLES20.glClearColor(0.1450f, 0.1490f, 0.1686f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData(){
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture,textureType);
    }

    public static void glError(int code, Object index){
        if(DEBUG&&code!=0){
            Log.e(TAG,"glError:"+code+"---"+index);
        }
    }

    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource){
        // 加载，编译顶点着色器
        int vertex=uLoadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        // 加载，编译片元着色器
        int fragment=uLoadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        // 创建一个Program对象
        int program= GLES20.glCreateProgram();
        if(program!=0){
            // 将一个已经编译好的shader挂载到program中
            GLES20.glAttachShader(program,vertex);  // 顶点着色器
            GLES20.glAttachShader(program,fragment);    // 片元着色器
            // 对制定的program对象执行链接操作，program在链接成功后才可以执行
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            // 判断链接是否成功
            if(linkStatus[0]!= GLES20.GL_TRUE){
                glError(1,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    /**加载shader*/
    public static int uLoadShader(int shaderType, String source){
        /**
         * 创建一个着色器对象
         * shaderType:制定要创建的着色器的类型，只能是GL_VERTEX_SHADER或GL_FRAGMENT_SHADER
         */
        int shader= GLES20.glCreateShader(shaderType);
        if(0!=shader){
            /**
             * 替换着色器对象中的源代码
             * source内容为源代码字符串，assets/shader/base_vertex.sh
             */
            GLES20.glShaderSource(shader,source);
            /**
             * 编译存储在shader中的源代码字符串
             */
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            // 这句是在验证shader源码编译是否成功
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                glError(1,"Could not compile shader:"+shaderType);
                glError(1,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }


}
