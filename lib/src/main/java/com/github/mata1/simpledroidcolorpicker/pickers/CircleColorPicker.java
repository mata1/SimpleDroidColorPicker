package com.github.mata1.simpledroidcolorpicker.pickers;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener;
import com.github.mata1.simpledroidcolorpicker.pickers.linear.LinearColorPicker;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Circular Color Picker View
 */
public class CircleColorPicker extends ColorPicker {

    private Paint mSaturationPaint, mValuePaint;

    private float mHandleX, mHandleY; // handle center
    private float mRadius; // circle radius

    private LinearColorPicker mValLCP;

    public CircleColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mColorPaint.setShader(new SweepGradient(0, 0, COLORS, null));
        mSaturationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setAlpha((int)((1 - mVal) * 255));

        if (isInEditMode())
            mHandlePaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mRadius = Math.min(mHalfWidth, mHalfHeight) - getMaxPadding() - mHandleSize/2 - mHandleStrokePaint.getStrokeWidth()/2;

        // position circle based on HSV values
        mHandleX = (float)Math.cos(Math.toRadians(mHue)) * mSat * mRadius;
        mHandleY = (float)Math.sin(Math.toRadians(mHue)) * mSat * mRadius;

        // set paint radial shader
        RadialGradient radialGradient = new RadialGradient(0, 0, mRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        mSaturationPaint.setShader(radialGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mHalfWidth, mHalfHeight);

        canvas.drawCircle(0, 0, mRadius, mColorPaint);
        canvas.drawCircle(0, 0, mRadius, mSaturationPaint);
        canvas.drawCircle(0, 0, mRadius + 1, mValuePaint);

        canvas.drawCircle(mHandleX, mHandleY, mHandleSize/2, mHandlePaint);
        canvas.drawCircle(mHandleX, mHandleY, mHandleSize/2, mHandleStrokePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {
        // set origin to center
        x -= mHalfWidth;
        y -= mHalfHeight;

        float centerDist = Utils.getDistance(x, y, 0, 0);

        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                mDragging = Utils.getDistance(x, y, mHandleX, mHandleY) < mTouchSize/2;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDragging) {
                    // clamp to circle edge
                    double angle = Utils.getAngle(0, 0, x, y);
                    x = (float)Math.cos(angle) * Math.min(centerDist, mRadius);
                    y = (float)Math.sin(angle) * Math.min(centerDist, mRadius);
                    moveHandleTo(x, y);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mDragging)
                    mDragging = false;
                else if (centerDist < mRadius) // animate move if inside bounds
                    animateHandleTo(x, y);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(min, min);
    }

    /**
     * Set handle color based on current position
     */
    @Override
    protected void moveHandleTo(float x, float y) {
        mHandleX = x;
        mHandleY = y;
        mHue = Utils.getAngleDeg(0, 0, x, y);
        mSat = Utils.getDistance(0, 0, x, y) / mRadius;
        int color = ColorUtils.getColorFromHSV(mHue, mSat, mVal);

        // repaint
        mHandlePaint.setColor(color);
        invalidate();

        // fire event
        if (mOnColorChangedListener != null)
            mOnColorChangedListener.colorChanged(color);

        // set value linear picker if attached
        if (mValLCP != null)
            mValLCP.updateHSV(mHue, mSat, mVal);
    }

    /**
     * Animate handle to new position
     * @param x new x
     * @param y new y
     */
    @Override
    protected void animateHandleTo(float x, float y) {
        PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("x", mHandleX, x);
        PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("y", mHandleY, y);

        ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(xHolder, yHolder);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator val) {
                moveHandleTo((float)val.getAnimatedValue("x"), (float)val.getAnimatedValue("y"));
            }
        });
        anim.start();
    }

    /*
    SETTERS/GETTERS
     */

    @Override
    public void setColor(int color) {
        float hue = ColorUtils.getHueFromColor(color);
        float sat = ColorUtils.getSaturationFromColor(color);
        float x = (float)Math.cos(Math.toRadians(hue)) * sat * mRadius;
        float y = (float)Math.sin(Math.toRadians(hue)) * sat * mRadius;
        animateHandleTo(x, y);
    }

    public void setValueLinearColorPicker(LinearColorPicker lcp) {
        mValLCP = lcp;
        if (mValLCP != null) {
            mValLCP.updateHSV(mHue, mSat, mVal);
            mValLCP.setOnColorChangedListener(new OnColorChangedListener() {
                @Override
                public void colorChanged(int color) {
                    mVal = ColorUtils.getValueFromColor(color);
                    mValuePaint.setAlpha((int) ((1 - mVal) * 255));
                    mHandlePaint.setColor(color);
                    invalidate();
                }
            });
        }

    }
}
