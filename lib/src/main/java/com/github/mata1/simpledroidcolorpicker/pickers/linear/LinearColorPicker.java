package com.github.mata1.simpledroidcolorpicker.pickers.linear;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mata1.simpledroidcolorpicker.pickers.ColorPicker;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Linear Color Picker View
 */
public abstract class LinearColorPicker extends ColorPicker {

    protected RectF mRect;
    protected RectF mHandleRect;

    private static final int RECT_EDGE_RADIUS = 10;
    private static final int HANDLE_PADDING = 10;
    private static final int HANDLE_EDGE_RADIUS = 5;

    public LinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRect = new RectF();
        mHandleRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mRect.set(
                getPaddingLeft() + mHandleSize/2, // left
                getPaddingTop() + HANDLE_PADDING, // top
                w - getPaddingRight() - mHandleSize/2, // right
                h - getPaddingBottom() - HANDLE_PADDING // bottom
        );

        float s = mHandleStrokePaint.getStrokeWidth()/2;
        mHandleRect.set(
                getPaddingLeft() + s, // left
                getPaddingTop() + s, // top
                getPaddingLeft() + mHandleSize - s, // right
                h - getPaddingBottom() - s // bottom
        );

        // set handle to correct position
        mHandleRect.offsetTo(getNewX() - mHandleRect.width()/2, mHandleRect.top);

        mColorPaint.setShader(createGradient());

        // if in edit mode, create different shader
        if (isInEditMode()) {
            mColorPaint.setShader(createFakeGradient());
            mHandlePaint.setColor(Color.RED);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw gradient
        canvas.drawRoundRect(mRect, RECT_EDGE_RADIUS, RECT_EDGE_RADIUS, mColorPaint);

        // draw handle
        canvas.drawRoundRect(mHandleRect, HANDLE_EDGE_RADIUS, HANDLE_EDGE_RADIUS, mHandlePaint);
        canvas.drawRoundRect(mHandleRect, HANDLE_EDGE_RADIUS, HANDLE_EDGE_RADIUS, mHandleStrokePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                // if over handle, grab handle
                mDragging = mHandleRect.contains(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                // check if handle grabbed and inside bounds
                if (mDragging) {
                    attemptClaimDrag();
                    moveHandleTo(x, 0);
                }
                break;

            case MotionEvent.ACTION_UP:
                // release handle or animate handle
                if (mDragging)
                    mDragging = false;
                else if (mRect.contains(x, y))
                    animateHandleTo(x, 0);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getPaddingLeft() + getPaddingRight() + (int)mHandleSize * 4 + HANDLE_PADDING * 2;
        int desiredHeight = getPaddingTop() + getPaddingBottom() + (int)mHandleSize + HANDLE_PADDING * 2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void moveHandleTo(float x, float y) {
        // move
        x = Utils.clamp(x, mRect.left, mRect.right);
        mHandleRect.offsetTo(x - mHandleRect.width()/2, mHandleRect.top);

        // repaint
        setNewX(x);
        int color = ColorUtils.getColorFromHSV(mHue, mSat, mVal);
        mHandlePaint.setColor(color);
        invalidate();

        // fire event
        if (mOnColorChangedListener != null)
            mOnColorChangedListener.colorChanged(color);
    }

    @Override
    protected void animateHandleTo(float x, float y) {
        ValueAnimator anim = ValueAnimator.ofFloat(mHandleRect.centerX(), x);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveHandleTo((float) animation.getAnimatedValue(), 0);
            }
        });
        anim.start();
    }

    protected abstract void setColorFromFraction(float fraction);
    protected abstract float getFractionFromColor();

    private float getNewX() {
        return getFractionFromColor() * mRect.width() + mRect.left;
    }

    private void setNewX(float x) {
        float fraction = (x - mRect.left) / mRect.width();
        fraction = Math.max(fraction, 0.01f); // prevent zero value
        setColorFromFraction(fraction);
    }

    protected abstract Shader createGradient();
    protected abstract Shader createFakeGradient(); // for edit mode

    /*
    SETTERS/GETTERS
     */

    @Override
    public void setColor(int color) {
        updateHSV(ColorUtils.getHueFromColor(color),
                ColorUtils.getSaturationFromColor(color),
                ColorUtils.getValueFromColor(color));
        animateHandleTo(getNewX(), 0);
    }

    public void updateHSV(float hue, float sat, float val) {
        mHue = hue;
        mSat = sat;
        mVal = val;

        mColorPaint.setShader(createGradient());
        mHandlePaint.setColor(ColorUtils.getColorFromHSV(hue, sat, val));
        invalidate();
    }

}
