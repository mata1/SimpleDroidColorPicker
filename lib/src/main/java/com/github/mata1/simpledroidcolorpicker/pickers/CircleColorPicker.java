package com.github.mata1.simpledroidcolorpicker.pickers;

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

import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Circular Color Picker View
 */
public class CircleColorPicker extends ColorPicker {

    private Paint mCircleColorPaint, mCircleAlphaPaint;
    private Paint mHandlePaint, mHandleStrokePaint;

    private float mRadius;

    private float mHandleX, mHandleY, mHandleRadius;

    private boolean mDragging;

    public CircleColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        mCircleColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleAlphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandleStrokePaint.setColor(Color.WHITE);
        mHandleStrokePaint.setStyle(Paint.Style.STROKE);
        mHandleStrokePaint.setStrokeWidth(5);

        mHandleRadius = 20;
    }

    @Override
    protected void initAttributes(AttributeSet attrs) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        float halfHeight = h/2f, halfWidth = w/2f;
        float minCenter = Math.min(halfHeight, halfWidth);
        mRadius = minCenter - getMaxPadding() - mHandleRadius - mHandleStrokePaint.getStrokeWidth()/2;

        // init with center
        if (mHandleX == 0 && mHandleY == 0) {
            mHandleX = halfWidth;
            mHandleY = halfHeight;
            setColor();
        }


        SweepGradient sweepGradient = new SweepGradient(0, 0, Utils.getHueRingColors(36), null);
        mCircleColorPaint.setShader(sweepGradient);
        RadialGradient radialGradient = new RadialGradient(0, 0, mRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        mCircleAlphaPaint.setShader(radialGradient);

        // XXX NOT WORKING WITH HW ACCELERATION
        //ComposeShader shader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        canvas.drawCircle(0, 0, mRadius, mCircleColorPaint);
        canvas.drawCircle(0, 0, mRadius, mCircleAlphaPaint);
        canvas.restore();

        canvas.drawCircle(mHandleX, mHandleY, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mHandleX, mHandleY, mHandleRadius, mHandleStrokePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {

        float halfWidth = getWidth()/2f, halfHeight = getHeight()/2f;
        float centerDist = Utils.getDistance(x, y, halfWidth, halfHeight);
        boolean isInsideBounds = centerDist < mRadius;

        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                if (Utils.getDistance(x, y, mHandleX, mHandleY) < mHandleRadius * 1.5) {
                    mDragging = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDragging) {
                    // clamp to circle edge
                    double angle = Utils.getAngle(halfWidth, halfHeight, x, y);
                    double cos = Math.cos(angle) * Math.min(centerDist, mRadius);
                    double sin = Math.sin(angle) * Math.min(centerDist, mRadius);
                    mHandleX = (float)-cos + halfWidth;
                    mHandleY = (float)-sin + halfHeight;
                    setColor();
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    mDragging = false;
                } else if (isInsideBounds) {
                    // animate move to x, y
                    /*final double curA = Utils.getAngle(getWidth()/2f, getHeight()/2f, mHandleX, mHandleY);
                    double newA = Utils.getAngle(getWidth()/2f, getHeight()/2f, x, y);
                    mHandleDist = Utils.getDistance(mHandleX, mHandleY, getWidth()/2f, getHeight()/2f);

                    ValueAnimator animAngle = ValueAnimator.ofFloat((float)curA, (float)newA);
                    animAngle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float angle = (float) animation.getAnimatedValue();
                            //float dist = Utils.getDistance(getWidth()/2f, getHeight()/2f, mHandleX, mHandleY);
                            double cos = Math.cos(angle) * mHandleDist;
                            double sin = Math.sin(angle) * mHandleDist;
                            mHandleX = (float)-cos + getWidth()/2f;
                            mHandleY = (float)-sin + getHeight()/2f;
                            invalidate();
                        }
                    });
                    animAngle.start();

                    ValueAnimator animDist = ValueAnimator.ofFloat(mHandleDist, centerDist);
                    animDist.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mHandleDist = (float) animation.getAnimatedValue();
                        }
                    });
                    animDist.start();*/

                    ValueAnimator animX, animY;
                    // animate X coordinate
                    animX = ValueAnimator.ofFloat(mHandleX, x);
                    animX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mHandleX = (float) animation.getAnimatedValue();
                            //invalidate();
                        }
                    });
                    animX.start();

                    // animate Y coordinate
                    animY = ValueAnimator.ofFloat(mHandleY, y);
                    animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mHandleY = (float) animation.getAnimatedValue();
                            setColor();
                            invalidate();
                        }
                    });
                    animY.start();
                }
                break;
        }
    }

    private void setColor() {
        float halfWidth = getWidth()/2f, halfHeight = getHeight()/2f;

        float hue = Utils.getAngleDeg(halfWidth, halfHeight, mHandleX, mHandleY);
        float sat = Utils.getDistance(halfWidth, halfHeight, mHandleX, mHandleY) / mRadius;

        mHandlePaint.setColor(Utils.getColorFromAngle(hue, sat, 1));
    }

    public int getColor() {
        return mHandlePaint.getColor();
    }
 }
