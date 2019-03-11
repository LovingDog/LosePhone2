package com.example.wanghanp.alarm;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;


public class LockView extends View {

    private Paint mPaint;
    private WeakReference<Context> mContext;
    private int screenWidth;
    private int screenHeight;
    private int radial = 100;
    private Paint mTextPaint;
    private Paint mRectPaint;

    public LockView(Context context) {
        super(context, null);
        this.mContext = new WeakReference<>(context);
        init();
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = new WeakReference<>(context);
        init();
    }

    private void init() {
        if (mContext.get() == null) {
            return;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));


        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(Color.WHITE);
        mRectPaint.setStyle(Paint.Style.FILL);
//        mRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
//        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mTextPaint.setColor(Color.parseColor("#10ffffff"));
        mTextPaint.setStyle(Paint.Style.FILL);

        WindowManager wm = (WindowManager) mContext.get().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        float length = (rectF.right-rectF.left) / 2;
        canvas.drawCircle(length, length, length, mTextPaint);
        canvas.drawArc(rectF.left+length *2/3+5,rectF.top+length/2,rectF.right-length *2/3-5,rectF.bottom-length+5,170.0f,200,false,mPaint);
        float x =10;
        float y = 10;
        canvas.drawRoundRect(length/3+10,rectF.bottom-length-length/4+5,length*2 - length/3-10,length*2 - length/2 ,x,y,mRectPaint);
    }
}