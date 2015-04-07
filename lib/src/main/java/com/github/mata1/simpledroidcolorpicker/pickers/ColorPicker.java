package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;

/**
 * Color picker abstraction class
 */
public abstract class ColorPicker extends View {
    protected OnColorPickedListener mListener;

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
    public abstract boolean onTouchEvent(MotionEvent event);

    /**
     * Set listener for color picked event
     * @see com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener
     * @param eventListener OnColorPickedListener event listener
     */
    public void setOnColorPickedListener(OnColorPickedListener eventListener) {
        mListener = eventListener;
    }
}
