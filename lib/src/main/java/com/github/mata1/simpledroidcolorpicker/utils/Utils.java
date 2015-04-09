package com.github.mata1.simpledroidcolorpicker.utils;

import android.graphics.Color;

/**
 * Utilities class
 */
public class Utils {

    /**
     * Get color at specific hue angle
     * @param angle angle in degrees
     * @return color at specific hue angle
     */
    public static int getColorFromAngle(float angle) {
        return getColorFromAngle(angle, 1, 1);
    }

    /**
     * Get color at specific hue angle, saturation and value
     * @param angle angle in degrees
     * @param saturation color saturation
     * @param value color value
     * @return color at specific hue angle, saturation and value
     */
    public static int getColorFromAngle(float angle, float saturation, float value) {
        angle -= 180;
        if (angle < 0) angle += 360;
        if (angle >= 360) angle -= 360;
        return Color.HSVToColor(new float[] { angle, saturation, value });
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

    /**
     * Get angle between two points
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return angle in radians
     */
    public static double getAngle(float x1, float y1, float x2, float y2) {
        return Math.atan2(x1 - x2, y1 - y2) * -1 + Math.PI/2;
    }

    /**
     * Get angle between two points
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return angle in degrees
     */
    public static float getAngleDeg(float x1, float y1, float x2, float y2) {
        return (float)Math.toDegrees(getAngle(x1, y1, x2, y2));
    }

    /**
     * Get distance between two points
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return distance between two points
     */
    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float)Math.hypot(x1 - x2, y1 - y2);
    }

    /**
     * Clamp float value between min and max
     * @param val value to be clamped
     * @param min minimum value
     * @param max maximum value
     * @return clamped value
     */
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Clamp integer value between min and max
     * @param val value to be clamped
     * @param min minimum value
     * @param max maximum value
     * @return clamped value
     */
    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
}
