package com.github.mata1.simpledroidcolorpicker.utils;

import android.graphics.Color;

/**
 * Color utilities class. Helper methods for easier HSV <-> RGB conversion
 */
public class ColorUtils {

    /**
     * Get color at specific hue angle
     * @param angle angle in degrees
     * @return color at specific hue angle
     */
    public static int getColorFromHue(float angle) {
        return getColorFromHSV(angle, 1, 1);
    }

    /**
     * Get color at specific hue angle, saturation and value
     * @param angle angle in degrees
     * @param saturation color saturation
     * @param value color value
     * @return color at specific hue angle, saturation and value
     */
    public static int getColorFromHSV(float angle, float saturation, float value) {
        angle = Utils.normalizeAngle(angle);
        return Color.HSVToColor(new float[]{angle, saturation, value});
    }

    /**
     * Get color from float between 0 and 1
     * @param fraction float between 0 and 1
     * @return color from fraction
     */
    public static int getColorFromFraction(float fraction) {
        return Color.HSVToColor(new float[] { fraction*360, 1, 1 });
    }

    /**
     * Get fraction of hue from color
     * @param color color
     * @return hue/360, 0..1
     */
    public static float getFractionFromColor(int color) {
        return getHueFromColor(color)/360;
    }

    /**
     * Get hue from color
     * @param color color
     * @return color hue value, 0..360
     */
    public static float getHueFromColor(int color) {
        return getHSVFromColor(color)[0];
    }

    /**
     * Get saturation from color
     * @param color color
     * @return color saturation value, 0..1
     */
    public static float getSaturationFromColor(int color) {
        return getHSVFromColor(color)[1];
    }

    /**
     * Get value from color
     * @param color color
     * @return color value, 0..1
     */
    public static float getValueFromColor(int color) {
        return getHSVFromColor(color)[2];
    }

    /**
     * Get HSV array from color
     * @param color color
     * @return HSV array, length = 3
     */
    public static float[] getHSVFromColor(int color) {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv;
    }

    /**
     * Get hue ring color array
     * @param n number of colors in array
     * @return hue color array
     */
    public static int[] getHueRingColors(int n) {
        int[] c = new int[n];

        for (int i = 0; i < n; i++)
            c[i] = Color.HSVToColor(new float[]{(float)i / n * 360, 1, 1});

        return c;
    }

    public static int[] getHueRingColors(int n, float saturation, float value) {
        int[] c = new int[n];

        for (int i = 0; i < n; i++)
            c[i] = getColorFromHSV(i / (n - 1f) * 360f, saturation, value);

        return c;
    }
}
