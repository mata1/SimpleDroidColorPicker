package com.github.mata1.simpledroidcolorpicker.pickers.linear;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.github.mata1.simpledroidcolorpicker.pickers.ColorPicker;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;

/**
 * Hue Linear Color Picker View
 */
public class HueLinearColorPicker extends LinearColorPicker {

    public HueLinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HueLinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected Shader createGradient() {
        return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                ColorUtils.getHueRingColors(7, mSat, mVal), null, LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected Shader createFakeGradient() {
        return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                ColorPicker.COLORS, null, LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected void setColorFromFraction(float fraction) {
        mHue = fraction * 360;
    }

    @Override
    protected float getFractionFromColor() {
        return mHue / 360;
    }
}
