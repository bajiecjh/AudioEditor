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

    private boolean isDrawBorder = false;   // 是否绘制Border
    private boolean isClickAtBitmap;    // 是否点击在图片上

    private int action = -1; // 用户操作动作
    private static final int ACTION_OUTSIDE = 0;    // 点中bitmap外围
    private static final int ACTION_MOVE = 1;       // 点中bitmap

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
        mHalfDeleteImgH = mDeleteBitmap.getWidth() / 2;
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
            mMatrix.postTranslate(0, 0);
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // mMatrix变换之后，通过mapRect方法调整mBitmapContentRect的值
        // postTranslate的值是累加的，对mOriginalBitmapContentRect计算之后赋值给mBitmapContentRect
        mMatrix.mapRect(mBitmapContentRect, mOriginalBitmapContentRect);

        // 绘制border
        if(isDrawBorder) {
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
                System.out.println("bybajie onTouchEvent");
                // 判断用户是否点中bitmap
                isClickAtBitmap = mBitmapContentRect.contains(x, y);
                if(isClickAtBitmap) {
                    mLastPointX = x;
                    mLastPointY = y;
                    isDrawBorder = true;
                } else {
                    // 如果点中bitmap外面，则把border线去掉
                    isDrawBorder = false;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果点中bitmap，让bitmap跟随手指 移动
                if(isClickAtBitmap) {

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
                isClickAtBitmap = false;
                break;
        }

        return true;
    }

    private boolean isClickAtDelete(float x, float y) {
        boolean result = false;
        RectF deleteRectF = new RectF(mBitmapContentRect.left - mHalfDeleteImgW,
                mBitmapContentRect.top - mHalfDeleteImgH,
                mBitmapContentRect.left + mHalfDeleteImgW, mBitmapContentRect.top + mHalfDeleteImgH );
        result = deleteRectF.contains(x, y);
        return  result;
    }

    public interface IOnStickerListener {
        public void onTouchDown(StickerView stickerView);
    }
}
