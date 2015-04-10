package com.github.mata1.simpledroidcolorpicker.pickers;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Circular Color Picker View
 */
public class CircleColorPicker extends ColorPicker {

    private Paint mAlphaPaint;

    private float mRadius;

    private float mHandleX, mHandleY; // handle center

    private static final int HANDLE_RADIUS = 30;

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
        mAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void initAttributes(AttributeSet attrs) { }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mRadius = Math.min(mHalfWidth, mHalfHeight) - getMaxPadding() - HANDLE_RADIUS - mHandleStrokePaint.getStrokeWidth()/2;

        // set paint radial shader
        RadialGradient radialGradient = new RadialGradient(0, 0, mRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        mAlphaPaint.setShader(radialGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mHalfWidth, mHalfHeight);

        canvas.drawCircle(0, 0, mRadius, mColorPaint);
        canvas.drawCircle(0, 0, mRadius, mAlphaPaint);

        canvas.drawCircle(mHandleX, mHandleY, HANDLE_RADIUS, mHandlePaint);
        canvas.drawCircle(mHandleX, mHandleY, HANDLE_RADIUS, mHandleStrokePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {
        // set origin to center
        x -= mHalfWidth;
        y -= mHalfHeight;

        float centerDist = Utils.getDistance(x, y, 0, 0);

        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                mDragging = Utils.getDistance(x, y, mHandleX, mHandleY) < HANDLE_RADIUS * 1.5;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDragging) {
                    // clamp to circle edge
                    double angle = Utils.getAngle(0, 0, x, y);
                    mHandleX = (float)Math.cos(angle) * Math.min(centerDist, mRadius);
                    mHandleY = (float)Math.sin(angle) * Math.min(centerDist, mRadius);
                    setHandleColor();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    mDragging = false;
                } else if (centerDist < mRadius) {
                    // animate move if inside bounds
                    PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("x", mHandleX, x);
                    PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("y", mHandleY, y);

                    ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(xHolder, yHolder);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator val) {
                            mHandleX = (float)val.getAnimatedValue("x");
                            mHandleY = (float)val.getAnimatedValue("y");
                            setHandleColor();
                        }
                    });
                    anim.start();
                }
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
    private void setHandleColor() {
        float hue = Utils.getAngleDeg(0, 0, mHandleX, mHandleY);
        float sat = Utils.getDistance(0, 0, mHandleX, mHandleY) / mRadius;

        mHandlePaint.setColor(Utils.getColorFromAngle(hue, sat, 1));
        invalidate();
    }
 }
