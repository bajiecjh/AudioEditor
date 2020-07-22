package com.bajie.audio.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.bajie.audio.R;

/**
 * bajie on 2020/7/15 11:03
 */
public class StickerView extends View {

    private boolean hasText;

    private Bitmap mBitmap;
    private Bitmap mDeleteBitmap, mSizeBitmap;   // 删除图标,旋转大小图标
    private float mHalfDeleteImgW, mHalfDeleteImgH; // 删除图标的一半宽度和高度
    private float mHalfSizeImgW, mHalfSizeImgH;     // 旋转改变大小图标的一半宽度和高度
    private Matrix mMatrix;
    private RectF mBitmapContentRect;   // 图片位置信息
    // 存储原始的图片位置信息，因为mapRect的计算是累加的，把累加的值赋值给mBitmapContentRect获取当前的正确位置信息
    private RectF mOriginalBitmapContentRect;
    // 存储原始图片四个点位置，主要用于绘制border定位
    private float[] mOriginalBitmapPoint, mBitmapPoint;
    private float mLastPointX, mLastPointY; // 保存bitmap上次移动前的位置
    private PointF midPoint = new PointF(); // 中心点位置

    private Paint mBorderPaint;     // 绘制边框的Paint

    private int mAction = -1; // 用户操作动作
    private static final int ACTION_OUTSIDE = 0;    // 点中bitmap外围
    private static final int ACTION_INSIDE = 1;       // 点中bitmap
    private static final int ACTION_DELETE = 2;     // 点击删除
    private static final int ACTION_SIZE = 2;     // 点击旋转

    private Context mContext;

    private IOnStickerListener onStickerListener;

    public StickerView(Context context, boolean hasText) {
       this(context, null, hasText);
    }

    public StickerView(Context context, @Nullable AttributeSet attrs, boolean hasText) {
        this(context, attrs, 0, hasText);
    }

    public StickerView(Context context, @Nullable AttributeSet attrs, int defStyle, boolean hasText) {
        super(context, attrs, defStyle);
        mContext = context;
        init(hasText);
    }

    public void setOnStickerListener(IOnStickerListener onStickerListener) {
        this.onStickerListener = onStickerListener;
    }

    private void init(boolean hasText) {
        this.hasText = hasText;
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);  // 抗锯齿
        mBorderPaint.setFilterBitmap(true);   // 再动画进行中会过滤掉队bitmap的优化操作，加快显示
        mBorderPaint.setStyle(Paint.Style.STROKE);    // 描边
        mBorderPaint.setStrokeWidth(4.0f);    // 描边宽度
        mBorderPaint.setColor(Color.WHITE);

        // 删除图标
        mDeleteBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.btn_delete);
        mHalfDeleteImgW = mDeleteBitmap.getWidth() / 2;
        mHalfDeleteImgH = mDeleteBitmap.getHeight() / 2;

        // 旋转和改变大小的图标
        mSizeBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.btn_scale);
        mHalfSizeImgW = mSizeBitmap.getWidth() / 2;
        mHalfSizeImgH = mSizeBitmap.getHeight() / 2;

    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if(bitmap != null) {
            float bitmapW = this.mBitmap.getWidth();
            float bitmapH = this.mBitmap.getHeight();

            mOriginalBitmapContentRect = new RectF(0, 0, bitmapW, bitmapH);
            mBitmapContentRect = new RectF();

            mOriginalBitmapPoint = new float[] {0, 0, bitmapW, 0, bitmapW, bitmapH, 0, bitmapH};
            mBitmapPoint = new float[8];
            mMatrix = new Matrix();
            mMatrix.postTranslate(mHalfDeleteImgW, mHalfDeleteImgH);
            float[] test = new float[9];
            mMatrix.getValues(test);
            System.out.println("matrixValue= " + test[0]);
            System.out.println("matrixValue= mMatrix" + mMatrix.toString());
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap == null) {
            return;
        }
        // mMatrix变换之后，通过mapRect方法调整mBitmapContentRect的值
        // postTranslate的值是累加的，对mOriginalBitmapContentRect计算之后赋值给mBitmapContentRect
        mMatrix.mapRect(mBitmapContentRect, mOriginalBitmapContentRect);
        mMatrix.mapPoints(mBitmapPoint, mOriginalBitmapPoint);

        // 绘制水印
        canvas.drawBitmap(mBitmap, mMatrix, null);
        // 如果点击范围在bitmap之内的话，绘制border
        if(mAction != ACTION_OUTSIDE) {
            canvas.drawLine(mBitmapPoint[0], mBitmapPoint[1], mBitmapPoint[2], mBitmapPoint[3], mBorderPaint);   // 上
            canvas.drawLine(mBitmapPoint[2], mBitmapPoint[3], mBitmapPoint[4], mBitmapPoint[5], mBorderPaint);   // 右
            canvas.drawLine(mBitmapPoint[4], mBitmapPoint[5], mBitmapPoint[6], mBitmapPoint[7], mBorderPaint);   // 下
            canvas.drawLine(mBitmapPoint[6], mBitmapPoint[7], mBitmapPoint[0], mBitmapPoint[1], mBorderPaint);   // 左
            canvas.drawBitmap(mDeleteBitmap, mBitmapPoint[0] - mHalfDeleteImgW, mBitmapPoint[1] - mHalfDeleteImgH, null);
            canvas.drawBitmap(mSizeBitmap, mBitmapPoint[4] - mHalfSizeImgW,  mBitmapPoint[5] - mHalfSizeImgH, null);
        }

        float[] test = new float[9];
        mMatrix.getValues(test);
        System.out.println("matrixValue=" + test[0]);
        System.out.println("matrixValue= mMatrix" + mMatrix.toString());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastPointX = x;
                mLastPointY = y;
                // 是否点中删除按钮
                if(isClickAtDelete(x, y)) {
                    mAction = ACTION_DELETE;
                } else if(isClickAtSize(x, y)) {    // 是否点中旋转大小按钮
                    mAction = ACTION_SIZE;
                    setMidPoint(x, y);      // 设置中心点位置

                } else if(mBitmapContentRect.contains(x, y)) {   // 判断用户是否点中bitmap
                    mAction = ACTION_INSIDE;
                } else {
                    // 如果点中bitmap外面，则把border线去掉
                    mAction = ACTION_OUTSIDE;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mAction == ACTION_SIZE) {
                    // 获取旋转角度
                    float lastDegree = getDegree(mLastPointX, mLastPointY);
                    float nowDegree = getDegree(x, y);
                    float rotation = nowDegree - lastDegree;
                    // 以中心位置旋转
                    mMatrix.postRotate(rotation, midPoint.x, midPoint.y);

                    // scale计算设置
                    float nowLength = getLength(mBitmapPoint[4], mBitmapPoint[5]);
                    float touchLength = getLength(x, y);
                    float scale = touchLength / nowLength;
                    mMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                } else if(mAction == ACTION_INSIDE) {  // 如果点中bitmap，让bitmap跟随手指 移动
                    // 计算偏移量
                    float offsetX = x - mLastPointX;
                    float offsetY = y - mLastPointY;
                    mMatrix.postTranslate(offsetX, offsetY);    // 更新坐标
                }
                postInvalidate();
                mLastPointX = x;
                mLastPointY = y;
                break;
            case MotionEvent.ACTION_UP:
                // 删除bitmap之前再判断一下最后落点是否在delete上
                if(mAction == ACTION_DELETE && isClickAtDelete(x, y)) {
                    setBitmap(null);
                    if(onStickerListener != null) {
                        onStickerListener.onTouchDelete(this);
                    }
                }
                mAction = -1;
                break;
        }

        return true;
    }

    private boolean isClickAtDelete(float x, float y) {
        boolean result = false;
        RectF deleteRectF = new RectF( mBitmapPoint[0] - mHalfDeleteImgW,
                mBitmapPoint[1] - mHalfDeleteImgH,
                mBitmapPoint[0] + mHalfDeleteImgW,
                mBitmapPoint[1] + mHalfDeleteImgH );
        result = deleteRectF.contains(x, y);
        return  result;
    }
    private boolean isClickAtSize(float x, float y) {
        boolean result = false;
        RectF sizeRectF = new RectF(mBitmapPoint[4] - mHalfSizeImgW,
                mBitmapPoint[5] - mHalfSizeImgH,
                mBitmapPoint[4] + mHalfSizeImgW,
                mBitmapPoint[5] + mHalfSizeImgH);
        result = sizeRectF.contains(x, y);
        return result;
    }

    // 计算bitmap左上角和传入点的中心位置
    private void setMidPoint(float x, float y) {
        float[] arrayOfFloat = new float[9];
        mMatrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + x;
        float f4 = f2 + y;
        midPoint.set(f3 / 2, f4 / 2);
    }

    // 计算传入点和minPoint之间的角度
    private float getDegree(float x, float y) {
        double deltaX = x - midPoint.x;
        double deltaY = y - midPoint.y;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    // 计算传入点和minPoint之间的距离
    private float getLength(float x, float y) {
       float length = 0;
       float ex = x - midPoint.x;
       float ey = y - midPoint.y;
       length = (float) Math.sqrt(ex * ex + ey * ey);
       return length;
    }
    public interface IOnStickerListener {
        public void onTouchDown(StickerView stickerView);
        public void onTouchDelete(StickerView stickerView);
    }
}
