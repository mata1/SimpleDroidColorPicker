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

/**
 * Color picker abstraction class
 */
public abstract class ColorPicker extends View {
    protected OnColorPickedListener mOnColorPickedListener;
    protected OnColorChangedListener mOnColorChangedListener;

    protected Paint mColorPaint;
    protected Paint mHandlePaint, mHandleStrokePaint;
    private int mHandleStrokeColor;

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
        mHandlePaint.setColor(Color.WHITE);

        mHandleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandleStrokePaint.setStyle(Paint.Style.STROKE);
        mHandleStrokePaint.setColor(mHandleStrokeColor);
        mHandleStrokePaint.setStrokeWidth(4);
    }
    protected void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker);

        try {
            mHandleStrokeColor = a.getColor(R.styleable.ColorPicker_handleStrokeColor, Color.WHITE);
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
