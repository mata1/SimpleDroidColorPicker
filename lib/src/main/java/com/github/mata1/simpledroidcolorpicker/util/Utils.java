package com.github.mata1.simpledroidcolorpicker.util;

import android.graphics.Color;

/**
 * Utilities class
 */
public class Utils {

    public static int getColorFromAngle(float angle) {
        angle -= 180;
        if (angle < 0) angle += 360;
        if (angle >= 360) angle -= 360;
        return Color.HSVToColor(new float[]{angle, 1, 1});
    }
}
