package com.example.wanghanp.alarm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

public class RadiumTextView extends TextView {

    private int mBorderWidth = 2;
    private int mBorderWidthColor = Color.parseColor("#CCCCCC");
    private int mCornersize = 20;
    private Paint mCornerPaint;
    private boolean mHasBackGround;

    @Override
    public boolean removeCallbacks(Runnable action) {
        return super.removeCallbacks(action);
    }

    public RadiumTextView(Context context) {
        this(context, null);
    }

    public void setRadiuView(boolean hasBackGround, int color) {
        this.mHasBackGround = hasBackGround;
        this.mBorderWidthColor = color;
        this.mCornersize = 25;
        mCornerPaint.setColor(mBorderWidthColor);
        if (mHasBackGround) {
            mCornerPaint.setStyle(Paint.Style.FILL);
        }
        invalidate();
    }

    public RadiumTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadiumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mBorderWidth = 2;
        mCornersize = 5;
        mCornerPaint = new Paint();
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setDither(true);
        if (mHasBackGround) {
            mCornerPaint.setStyle(Paint.Style.FILL);
        } else {
            mCornerPaint.setStrokeWidth(mBorderWidth);
            mCornerPaint.setStyle(Paint.Style.STROKE);
        }
        mCornerPaint.setColor(mBorderWidthColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        RectF rectF = new RectF(mBorderWidth / 2, mBorderWidth / 2, getMeasuredWidth() - mBorderWidth, getMeasuredHeight() - mBorderWidth);
        canvas.drawRoundRect(rectF, mCornersize, mCornersize, mCornerPaint);
        super.onDraw(canvas);
    }


    public void setCornerSize(int size) {
        mCornersize = size;
//        invalidate();
    }


    public RadiumTextView setfilColor(int color) {

        this.mBorderWidthColor = color;
        mCornerPaint.setColor(mBorderWidthColor);
        return this;
//        invalidate();
    }
}