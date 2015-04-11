package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Color picker with rectangle handle
 */
public abstract class RectHandleColorPicker extends ColorPicker {

    protected RectF mHandleRect;

    protected static final int HANDLE_TOUCH_LIMIT = 15;
    protected static final int HANDLE_WIDTH = 40;
    protected static final int HANDLE_PADDING = 10;
    protected static final int HANDLE_EDGE_RADIUS = 5;

    public RectHandleColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectHandleColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mHandleRect = new RectF();
    }

    protected abstract void moveHandleTo(float x);

    protected abstract void animateHandleTo(float x);
}
