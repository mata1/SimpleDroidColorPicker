package com.github.mata1.simpledroidcolorpicker.pickers.linear;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;

/**
 * Saturation Linear Color Picker View
 */
public class SaturationLinearColorPicker extends LinearColorPicker {

    public SaturationLinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SaturationLinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected Shader createGradient() {
        return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                ColorUtils.getColorFromHSV(mHue, 0, mVal), ColorUtils.getColorFromHSV(mHue, 1, mVal),
                LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected Shader createFakeGradient() {
        return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                Color.WHITE, Color.RED, LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected void setColorFromFraction(float fraction) {
        mSat = fraction;
    }

    @Override
    protected float getFractionFromColor() {
        return mSat;
    }
}
