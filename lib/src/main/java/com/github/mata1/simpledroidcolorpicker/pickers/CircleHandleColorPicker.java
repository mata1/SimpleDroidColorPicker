package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Color picker with circular handle
 */
public abstract class CircleHandleColorPicker extends ColorPicker {

    protected float mHandleX, mHandleY; // handle center

    protected static final int HANDLE_RADIUS = 30;

    public CircleHandleColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleHandleColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected abstract void moveHandleTo();

    protected abstract void animateHandleTo(float x, float y);
}
