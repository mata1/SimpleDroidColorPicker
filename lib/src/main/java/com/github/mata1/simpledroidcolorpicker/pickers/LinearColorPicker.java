package com.github.mata1.simpledroidcolorpicker.pickers;

import android.animation.ValueAnimator;
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

    private boolean mDragging;

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
        mHandlePaint.setColor(Utils.getColorFromFraction(0));
        mHandleStrokePaint = new Paint(mStrokePaint);


        mRect = new RectF();
        mHandleRect = new RectF();
    }

    @Override
    protected void initAttributes(AttributeSet attrs) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
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
                // if over handle, grab handle
                mDragging = isTouchingHandle(x);
                break;

            case MotionEvent.ACTION_MOVE:
                // check if handle grabbed
                if (mDragging) {
                    if (!isInsideBounds(x))
                        break;
                    mHandleRect.offsetTo(x - mHandleRect.width() / 2, mHandleRect.top);

                    float fraction = (mHandleRect.centerX() - mRect.left) / mRect.width();
                    mHandlePaint.setColor(Utils.getColorFromFraction(fraction));
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                // release handle or move handle
                if (mDragging) {
                    mDragging = false;
                } else if (isInsideBounds(x)) {
                    // start animating
                    ValueAnimator anim = ValueAnimator.ofFloat(mHandleRect.centerX(), x);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float newX = (float) animation.getAnimatedValue();
                            mHandleRect.offsetTo(newX - mHandleRect.width()/2, mHandleRect.top);

                            float fraction = (mHandleRect.centerX() - mRect.left) / mRect.width();
                            mHandlePaint.setColor(Utils.getColorFromFraction(fraction));
                            invalidate();
                        }
                    });
                    anim.start();
                }
                break;
        }
        return true;
    }

    private boolean isTouchingHandle(float x) {
        return x > mHandleRect.left && x < mHandleRect.right;
    }

    private boolean isInsideBounds(float x) {
        return x > mRect.left && x < mRect.right;
    }
}
