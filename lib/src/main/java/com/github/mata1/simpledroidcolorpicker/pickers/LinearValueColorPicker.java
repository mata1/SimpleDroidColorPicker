package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Created by matej on 12/04/15.
 */
public class LinearValueColorPicker extends LinearColorPicker {

    private float[] mHSV;

    public LinearValueColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mColorPaint.setShader(new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP));
        mHandleRect.offsetTo(mRect.right - mHandleRect.width()/2, mHandleRect.top);
        mHandlePaint.setColor(Color.WHITE);
    }

    @Override
    protected void moveHandleTo(float x) {
        //super.moveHandleTo(x);
        x = Utils.clamp(x, mRect.left, mRect.right);
        mHandleRect.offsetTo(x - mHandleRect.width()/2, mHandleRect.top);

        // repaint
        float fraction = (x - mRect.left) / mRect.width();
        mHSV[2] = fraction; // value
        int color = ColorUtils.getColorFromHSV(mHSV[0], mHSV[1], mHSV[2]);
        mHandlePaint.setColor(color);
        invalidate();

        // fire event
        if (mOnColorChangedListener != null)
            mOnColorChangedListener.colorChanged(color);
    }

    @Override
    public void setColor(int color) {
        //super.setColor(color);

        float[] HSV = new float[3];
        Color.colorToHSV(color, HSV);
        mHSV = HSV;
        mHSV[2] = 1; // ignore color value
        Shader sh = new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                        Color.BLACK, Color.HSVToColor(mHSV), Shader.TileMode.CLAMP);

        mColorPaint.setShader(sh);
        mHandlePaint.setColor(color);
        invalidate();
    }
}
