package com.bajie.audio.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
    private Bitmap mDeleteBitmap;   // 删除图标
    private float mHalfDeleteImgW, mHalfDeleteImgH; // 删除图标的一半宽度和高度
    private Matrix mMatrix;
    private RectF mBitmapContentRect;   // 图片位置信息
    // 存储原始的图片位置信息，因为mapRect的计算是累加的，把累加的值赋值给mBitmapContentRect获取当前的正确位置信息
    private RectF mOriginalBitmapContentRect;
    private float mLastPointX, mLastPointY; // 保存bitmap上次移动前的位置

    private Paint mBorderPaint;     // 绘制边框的Paint

//    private boolean isDrawBorder = false;   // 是否绘制Border
//    private boolean isClickAtBitmap;    // 是否点击在图片上

    private int mAction = -1; // 用户操作动作
    private static final int ACTION_OUTSIDE = 0;    // 点中bitmap外围
    private static final int ACTION_INSIDE = 1;       // 点中bitmap
    private static final int ACTION_DELETE = 2;     // 点击删除

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

        mDeleteBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.btn_delete);
        mHalfDeleteImgW = mDeleteBitmap.getWidth() / 2;
        mHalfDeleteImgH = mDeleteBitmap.getHeight() / 2;

    }

    public void setBitmap(Bitmap bitmap) {
        if(bitmap != null) {
            this.mBitmap = bitmap;
            float bitmapW = this.mBitmap.getWidth();
            float bitmapH = this.mBitmap.getHeight();
            mOriginalBitmapContentRect = new RectF(0, 0, bitmapW, bitmapH);
            mBitmapContentRect = new RectF();
            mMatrix = new Matrix();
            mMatrix.postTranslate(mHalfDeleteImgW, mHalfDeleteImgH);
        } else {
            this.mBitmap = null;
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

        // 如果点击范围在bitmap之内的话，绘制border
        if(mAction != ACTION_OUTSIDE) {
            canvas.drawLine(mBitmapContentRect.left, mBitmapContentRect.top, mBitmapContentRect.right, mBitmapContentRect.top, mBorderPaint);   // 上
            canvas.drawLine(mBitmapContentRect.right, mBitmapContentRect.top, mBitmapContentRect.right, mBitmapContentRect.bottom, mBorderPaint);   // 右
            canvas.drawLine(mBitmapContentRect.right, mBitmapContentRect.bottom, mBitmapContentRect.left, mBitmapContentRect.bottom, mBorderPaint);   // 下
            canvas.drawLine(mBitmapContentRect.left, mBitmapContentRect.bottom, mBitmapContentRect.left, mBitmapContentRect.top, mBorderPaint);   // 左
            canvas.drawBitmap(mDeleteBitmap, mBitmapContentRect.left - mHalfDeleteImgW, mBitmapContentRect.top - mHalfDeleteImgH, null);
        }
        canvas.drawBitmap(mBitmap, mMatrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                // 是否点中删除按钮
                if(isClickAtDelete(x, y)) {
                    mAction = ACTION_DELETE;
                } else {
                    // 判断用户是否点中bitmap
                    boolean isClickAtBitmap = mBitmapContentRect.contains(x, y);
                    if(isClickAtBitmap) {
                        mAction = ACTION_INSIDE;
                        mLastPointX = x;
                        mLastPointY = y;
                    } else {
                        // 如果点中bitmap外面，则把border线去掉
                        mAction = ACTION_OUTSIDE;
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果点中bitmap，让bitmap跟随手指 移动
                if(mAction == ACTION_INSIDE) {
                    // 计算偏移量
                    float offsetX = x - mLastPointX;
                    float offsetY = y - mLastPointY;
                    mMatrix.postTranslate(offsetX, offsetY);    // 更新坐标
                    postInvalidate();               // 更新页面
                    mLastPointX = x;
                    mLastPointY = y;
                }
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
        RectF deleteRectF = new RectF(mBitmapContentRect.left - mHalfDeleteImgW,
                mBitmapContentRect.top - mHalfDeleteImgH,
                mBitmapContentRect.left + mHalfDeleteImgW, mBitmapContentRect.top + mHalfDeleteImgH );
        System.out.println("deleaction deleteRectF =" + deleteRectF.toString());
        result = deleteRectF.contains(x, y);
        return  result;
    }

    public interface IOnStickerListener {
        public void onTouchDown(StickerView stickerView);
        public void onTouchDelete(StickerView stickerView);
    }
}
