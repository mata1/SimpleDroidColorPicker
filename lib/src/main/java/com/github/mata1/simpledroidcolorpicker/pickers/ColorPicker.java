package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;

/**
 * Color picker abstraction class
 */
public abstract class ColorPicker extends View {
    protected OnColorPickedListener mOnColorPickedListener;
    protected OnColorChangedListener mOnColorChangedListener;

    protected Paint mHandlePaint, mHandleStrokePaint;

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
        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandlePaint.setColor(Color.WHITE);

        mHandleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandleStrokePaint.setStyle(Paint.Style.STROKE);
        mHandleStrokePaint.setColor(Color.WHITE);
        mHandleStrokePaint.setStrokeWidth(4);
    }
    protected abstract void initAttributes(AttributeSet attrs);

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
     * Get current picker color
     * @return current color
     */
    public int getColor() {
        return mHandlePaint.getColor();
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

    /**
     * Get view maximum padding
     * @return maximum padding
     */
    protected int getMaxPadding() {
        return Math.max(Math.max(getPaddingLeft(), getPaddingRight()), Math.max(getPaddingTop(), getPaddingBottom()));
    }
}
