package com.github.mata1.simpledroidcolorpicker.utils;

/**
 * Utilities class
 */
public class Utils {

    /**
     * Get angle between two points
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return angle in radians
     */
    public static double getAngle(float x1, float y1, float x2, float y2) {
        return Math.atan2(x1 - x2, y1 - y2) * -1 - Math.PI/2;
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
     * Normalize angle in degrees to value between 0 and 360
     * @param deg angle in degrees
     * @return normalized angle in degrees
     */
    public static float normalizeAngle(float deg) {
        if (deg > 360) deg -= 360;
        else if (deg < 0) deg += 360;
        return deg;
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
}
