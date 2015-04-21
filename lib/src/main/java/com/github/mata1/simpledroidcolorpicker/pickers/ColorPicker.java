package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Color picker abstraction class
 */
public abstract class ColorPicker extends View {
    protected OnColorPickedListener mOnColorPickedListener;
    protected OnColorChangedListener mOnColorChangedListener;

    protected Paint mColorPaint;
    protected Paint mHandlePaint, mHandleStrokePaint;
    private int mHandleStrokeColor;

    protected float mHandleSize, mTouchSize;

    protected float mHue, mSat, mVal; // HSV color values

    protected float mHalfWidth, mHalfHeight;

    protected boolean mDragging; // whether handle is being dragged

    protected static final int[] COLORS = new int[] { 0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000 };

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        init();
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    protected void init() {
        mColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandlePaint.setColor(ColorUtils.getColorFromHSV(mHue, mSat, mVal));

        mHandleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandleStrokePaint.setStyle(Paint.Style.STROKE);
        mHandleStrokePaint.setColor(mHandleStrokeColor);
        mHandleStrokePaint.setStrokeWidth(4);
    }

    protected void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker);

        try {
            mHandleStrokeColor = a.getColor(R.styleable.ColorPicker_handleStrokeColor, Color.WHITE);
            mHue = Utils.normalizeAngle(a.getFloat(R.styleable.ColorPicker_hue, 0));
            mSat = Utils.clamp(a.getFloat(R.styleable.ColorPicker_saturation, 1), 0, 1);
            mVal = Utils.clamp(a.getFloat(R.styleable.ColorPicker_value, 1), 0, 1);

            // TODO add to XML attributes
            mHandleSize = getResources().getDimensionPixelSize(R.dimen.default_handleSize);
            mTouchSize = getResources().getDimensionPixelSize(R.dimen.default_touchSize);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected abstract void onDraw(Canvas canvas);

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mHalfWidth = w / 2f;
        mHalfHeight = h / 2f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int[] location = new int[2];
        getLocationOnScreen(location);

        float x = event.getRawX() - location[0];
        float y = event.getRawY() - location[1];

        handleTouch(event.getAction(), x, y);
        return true;
    }

    protected abstract void handleTouch(int motionAction, float x, float y);

    protected abstract void moveHandleTo(float x, float y);
    protected abstract void animateHandleTo(float x, float y);

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    protected void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * Get view maximum padding
     * @return maximum padding
     */
    protected int getMaxPadding() {
        return Math.max(Math.max(getPaddingLeft(), getPaddingRight()), Math.max(getPaddingTop(), getPaddingBottom()));
    }

    /*
    SETTERS/GETTERS
     */

    /**
     * Set new picker color
     * @param color new picker color
     */
    public abstract void setColor(int color);

    /**
     * Get current picker color
     * @return current color
     */
    public int getColor() {
        return mHandlePaint.getColor();
    }

    /**
     * Set handle stroke color
     * @param color new handle stroke color
     */
    public void setHandleStrokeColor(int color) {
        mHandleStrokeColor = color;
        mHandleStrokePaint.setColor(mHandleStrokeColor);
        invalidate();
    }

    /**
     * Get handle stroke color
     * @return current handle stroke color
     */
    public int getHandleStrokeColor() {
        return mHandleStrokeColor;
    }

    /**
     * Set listener for color picked event
     * @see com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener
     * @param eventListener OnColorPickedListener event listener
     */
    public void setOnColorPickedListener(OnColorPickedListener eventListener) {
        mOnColorPickedListener = eventListener;
    }

    /**
     * Set listener for color changed event
     * @see com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener
     * @param eventListener OnColorChangedListener event listener
     */
    public void setOnColorChangedListener(OnColorChangedListener eventListener) {
        mOnColorChangedListener = eventListener;
    }


}
