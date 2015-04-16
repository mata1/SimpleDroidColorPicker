package com.github.mata1.simpledroidcolorpicker.pickers;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Linear Color Picker View
 */
public class LinearColorPicker extends RectHandleColorPicker {

    public enum PickerType { HUE, SATURATION, VALUE }

    private PickerType mPickerType;

    protected RectF mRect;

    private static final int RECT_EDGE_RADIUS = 10;

    public LinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initAttributes(AttributeSet attrs) {
        super.initAttributes(attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LinearColorPicker);

        try {
            int ordinal = a.getInt(R.styleable.LinearColorPicker_pickerType, PickerType.HUE.ordinal());
            if (ordinal >= 0 && ordinal < PickerType.values().length)
                mPickerType = PickerType.values()[ordinal];
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void init() {
        super.init();

        mRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mRect.set(
                getPaddingLeft() + HANDLE_WIDTH/2, // left
                getPaddingTop() + HANDLE_PADDING, // top
                w - getPaddingRight() - HANDLE_WIDTH/2, // right
                h - getPaddingBottom() - HANDLE_PADDING // bottom
        );

        float s = mHandleStrokePaint.getStrokeWidth()/2;
        mHandleRect.set(
                getPaddingLeft() + s, // left
                getPaddingTop() + s, // top
                getPaddingLeft() + HANDLE_WIDTH - s, // right
                h - getPaddingBottom() - s // bottom
        );

        // set handle to correct position
        mHandleRect.offsetTo(getNewX() - mHandleRect.width()/2, mHandleRect.top);

        mColorPaint.setShader(createGradient());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            switch (mPickerType) {
                case HUE:
                    mColorPaint.setShader(new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                            COLORS, null, LinearGradient.TileMode.CLAMP));
                    break;
                case SATURATION:
                    mColorPaint.setShader(new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                            Color.WHITE, Color.RED, LinearGradient.TileMode.CLAMP));
                    break;
                case VALUE:
                    mColorPaint.setShader(new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                            Color.BLACK, Color.RED, LinearGradient.TileMode.CLAMP));
                    break;
            }
            mHandlePaint.setColor(Color.RED);
        }

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
                if (mDragging)
                    moveHandleTo(x);
                break;

            case MotionEvent.ACTION_UP:
                // release handle or animate handle
                if (mDragging)
                    mDragging = false;
                else if (mRect.contains(x, y))
                    animateHandleTo(x);
                break;
        }
    }

    /**
     * Moves handle to new position and sets color.
     * X is clamped so handle does not leave bounds.
     * @param x new handle position
     */
    @Override
    protected void moveHandleTo(float x) {
        // move
        x = Utils.clamp(x, mRect.left, mRect.right);
        mHandleRect.offsetTo(x - mHandleRect.width()/2, mHandleRect.top);

        // repaint
        float fraction = (x - mRect.left) / mRect.width();
        fraction = Math.max(fraction, 0.01f); // prevent zero value
        switch (mPickerType) {
            case HUE:
                mHue = fraction * 360;
                break;
            case SATURATION:
                mSat = fraction;
                break;
            case VALUE:
                mVal = fraction;
                break;
        }
        int color = ColorUtils.getColorFromHSV(mHue, mSat, mVal);
        mHandlePaint.setColor(color);
        invalidate();

        // fire event
        if (mOnColorChangedListener != null)
            mOnColorChangedListener.colorChanged(color);
    }

    /**
     * Animate handle to new position
     * @param x new handle position
     */
    @Override
    protected void animateHandleTo(float x) {
        ValueAnimator anim = ValueAnimator.ofFloat(mHandleRect.centerX(), x);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveHandleTo((float) animation.getAnimatedValue());
            }
        });
        anim.start();
    }

    private float getNewX() {
        float fraction = 0;
        switch (mPickerType) {
            case HUE:
                fraction = mHue / 360;
                break;
            case SATURATION:
                fraction = mSat;
                break;
            case VALUE:
                fraction = mVal;
                break;
        }
        return fraction * mRect.width() + mRect.left;
    }

    private Shader createGradient() {
        switch (mPickerType) {
            case HUE:
                return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                        ColorUtils.getHueRingColors(7, mSat, mVal), null, LinearGradient.TileMode.CLAMP);
            case SATURATION:
                return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                        ColorUtils.getColorFromHSV(mHue, 0, mVal), ColorUtils.getColorFromHSV(mHue, 1, mVal), Shader.TileMode.CLAMP);
            case VALUE:
                return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                        Color.BLACK, ColorUtils.getColorFromHSV(mHue, mSat, 1), Shader.TileMode.CLAMP);
        }
        return null;
    }

    /*
    SETTERS/GETTERS
     */

    @Override
    public void setColor(int color) {
        float fraction = 0;
        switch (mPickerType) {
            case HUE:
                fraction = ColorUtils.getFractionFromColor(color);
                break;
            case SATURATION:
                fraction = ColorUtils.getSaturationFromColor(color);
                break;
            case VALUE:
                fraction = ColorUtils.getValueFromColor(color);
                break;
        }
        float newX = fraction * mRect.width() + mRect.left;
        animateHandleTo(newX);
    }

    public void setHSV(float hue, float sat, float val) {
        mHue = hue;
        mSat = sat;
        mVal = val;

        mColorPaint.setShader(createGradient());
        mHandlePaint.setColor(ColorUtils.getColorFromHSV(hue, sat, val));
        invalidate();
    }

}
