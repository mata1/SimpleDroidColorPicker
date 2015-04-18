package com.github.mata1.simpledroidcolorpicker.pickers.linear;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;

/**
 * Value Linear Color Picker View
 */
public class ValueLinearColorPicker extends LinearColorPicker {

    public ValueLinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValueLinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected Shader createGradient() {
        return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                ColorUtils.getColorFromHSV(mHue, mSat, 0), ColorUtils.getColorFromHSV(mHue, mSat, 1),
                LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected Shader createFakeGradient() {
        return new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(),
                Color.BLACK, Color.RED, LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected void setColorFromFraction(float fraction) {
        mVal = fraction;
    }

    @Override
    protected float getFractionFromColor() {
        return mVal;
    }
}
