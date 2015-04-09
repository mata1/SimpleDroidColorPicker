package com.github.mata1.simpledroidcolorpicker.pickers;

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

    private Paint mCircleColorPaint, mCircleAlphaPaint, mHandlePaint;

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
        mHandlePaint.setColor(Color.WHITE);
        mHandlePaint.setStyle(Paint.Style.STROKE);
        mHandlePaint.setStrokeWidth(5);

        mHandleRadius = 20;
    }

    @Override
    protected void initAttributes(AttributeSet attrs) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        float halfHeight = h/2f, halfWidth = w/2f;
        float minCenter = Math.min(halfHeight, halfWidth);
        mRadius = minCenter - getMaxPadding();


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

        //canvas.translate(-getWidth()/2f, -getHeight()/2f);
        canvas.drawCircle(mHandleX, mHandleY, mHandleRadius, mHandlePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {

        boolean isInsideBounds = Utils.getDistance(x, y, getWidth()/2f, getHeight()/2f) < mRadius;
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                if (Utils.getDistance(x, y, mHandleX, mHandleY) < mHandleRadius * 1.5) {
                    mDragging = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mDragging) {
                    mHandleX = x;
                    mHandleY = y;
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    mDragging = false;
                } else if (isInsideBounds) {
                    // animate move to x, y
                    mHandleX = x;
                    mHandleY = y;
                    invalidate();
                }
                break;
        }
    }
}
