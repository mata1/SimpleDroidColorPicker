package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Linear Color Picker View
 */
public class LinearColorPicker extends ColorPicker {

    private Paint mColorPaint, mStrokePaint, mHandlePaint, mHandleStrokePaint;
    private RectF mRect, mHandleRect;

    private float mFraction;

    private static final int HANDLE_WIDTH = 40;
    
    public LinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        mColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(Color.WHITE);
        mStrokePaint.setStrokeWidth(5);

        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandlePaint.setColor(Utils.getColorFromFraction(mFraction));
        mHandleStrokePaint = new Paint(mStrokePaint);


        mRect = new RectF();
        mHandleRect = new RectF();
    }

    @Override
    protected void initAttributes(AttributeSet attrs) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        float hW = w/2f, hH = w/2f;
        float s = mStrokePaint.getStrokeWidth()/2;

        mRect.set(
                getPaddingLeft() + s + HANDLE_WIDTH/2,
                getPaddingTop() + s*2,
                w - getPaddingRight() - s - HANDLE_WIDTH/2,
                h - getPaddingBottom() - s*2
        );

        mHandleRect.set(
                getPaddingLeft() + s,
                getPaddingTop() + s,
                getPaddingLeft() + HANDLE_WIDTH - s,
                h - getPaddingBottom() - s
        );

        Shader sweep = new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(), Utils.getHueRingColors(50), null, LinearGradient.TileMode.CLAMP);
        mColorPaint.setShader(sweep);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw gradient
        canvas.drawRoundRect(mRect, 10, 10, mColorPaint);
        canvas.drawRoundRect(mRect, 10, 10, mStrokePaint);

        // draw handle
        canvas.drawRoundRect(mHandleRect, 5, 5, mHandlePaint);
        canvas.drawRoundRect(mHandleRect, 5, 5, mHandleStrokePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int[] location = new int[2];
        getLocationOnScreen(location);

        float x = event.getRawX() - location[0];
        float y = event.getRawY() - location[1];


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO check if over handle, grab handle
                break;

            case MotionEvent.ACTION_MOVE:
                // TODO check if handle grabbed
                if (!isInsideBounds(x))
                    break;
                mHandleRect.offsetTo(x - mHandleRect.width()/2, mHandleRect.top);

                mFraction = (mHandleRect.centerX() - mRect.left) / mRect.width();
                Log.d("fraction, centerx", mFraction+", " + (mHandleRect.centerX() - mRect.left));
                Log.d("mRect", mRect.toShortString()+ ", " + mRect.width() + ", "+mRect.right);
                mHandlePaint.setColor(Utils.getColorFromFraction(mFraction));
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // TODO release handle or move handle
                break;
        }
        return true;
    }

    private boolean isInsideBounds(float x) {
        return x > mRect.left && x < mRect.right;
    }
}
