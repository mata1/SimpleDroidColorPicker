package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.Canvas;
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

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        init();
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    protected abstract void init();
    protected abstract void initAttributes(AttributeSet attrs);

    @Override
    protected abstract void onDraw(Canvas canvas);

    @Override
    protected abstract void onSizeChanged(int w, int h, int oldW, int oldH);

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
