package com.github.mata1.simpledroidcolorpicker.pickers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Bubble color picker
 */
public class BubbleColorPicker extends ColorPicker {

    private float mRadius, mBubbleRadius;

    private Set<Circle> cs;

    public BubbleColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        cs = new HashSet<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mBubbleRadius = 40;
        mRadius = mHalfWidth - getMaxPadding() - mBubbleRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mHalfWidth, mHalfHeight);

        final float goldenRatio = 0.6180339887499895f;
        final int minR = 10;
        final int maxR = 50;

        Random r = new Random();

        cs.clear();

        long start = System.currentTimeMillis();

        float angle = r.nextFloat();
        for (int i = 0; i < 2000; i++) {
            angle += goldenRatio;
            angle %= 1;
            float angleRad = (float)Math.toRadians(angle * 360);//r.nextFloat() * (float)Math.PI * 2;
            Circle ci = new Circle();
            ci.x = (float)Math.cos(angleRad) * mRadius * r.nextFloat();
            ci.y = (float)Math.sin(angleRad) * mRadius * r.nextFloat();
            ci.r = Utils.getDistance(0,0,ci.x, ci.y)/mRadius * (maxR - minR) + minR;

            // VERSION 1
            for (Circle c : cs) {
                while (Utils.getDistance(c.x, c.y, ci.x, ci.y) - c.r - ci.r < 0) {
                    ci.r -= 1;
                    if (ci.r < minR)
                        break;
                }
            }
            if (ci.r >= minR)
                cs.add(ci);

            // VERSION 2
            /*ci.r = 0;
            float minDist = Float.MAX_VALUE;
            for (Circle c : cs) {
                float dist = Utils.getDistance(c.x, c.y, ci.x, ci.y) - c.r;
                //Log.d("dist", dist+"");
                if (dist < minDist) {
                    minDist = dist;
                }
            }
            if (minDist > minR) {
                ci.r += Math.min(minDist, maxR);
                cs.add(ci);
            }*/
        }

        Log.d("time ms", System.currentTimeMillis() - start + "");

        Log.d("circle set size", cs.size()+"");
        for (Circle c : cs) {
            float hue = Utils.getAngleDeg(0, 0, c.x, c.y);
            float dist = Utils.getDistance(0, 0, c.x, c.y);
            mColorPaint.setColor(ColorUtils.getColorFromHSV(hue, dist / mRadius, 1));
            canvas.drawCircle(c.x, c.y, c.r, mColorPaint);
        }

        // VERSION 3
        /*int n = 10;
        //float part = 360f / n;
        //mBubbleRadius = (float)(2 * Math.PI * mRadius) / n / 2;
        //mBubbleRadius = (float)(2 * Math.PI * (mRadius - mBubbleRadius)) / n / 2;
        double part = 360f / ((2 * Math.PI * mRadius) / (mBubbleRadius * 2));
        n = (int)(360f / (float)part);

        for (int i = 0; i < 6; i+=2) {
            for (float hue = 0; hue < 360; hue += part) {
                Log.d("hue", hue + "");
                mColorPaint.setColor(ColorUtils.getColorFromHue(hue));
                canvas.drawCircle(mRadius - i * mBubbleRadius, 0, mBubbleRadius, mColorPaint);
                canvas.rotate((float) part);
            }
            part += part/2;
        }*/


        /*for (int c : ColorUtils.getHueRingColors(n)) {
            float hue = ColorUtils.getHueFromColor(c);
            Log.d("hue", hue + "");
            float x = (float)Math.cos(Math.toRadians(hue)) * mRadius;
            float y = (float)Math.sin(Math.toRadians(hue)) * mRadius;

            mColorPaint.setColor(c);
            canvas.drawCircle(x, y, mBubbleRadius, mColorPaint);
        }*/
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {

    }

    @Override
    protected void moveHandleTo(float x, float y) {

    }

    @Override
    protected void animateHandleTo(float x, float y) {

    }

    @Override
    public void setColor(int color) {

    }

    private class Circle implements Comparable<Circle> {
        public float r = 50, x, y;

        @Override
        public int compareTo(Circle another) {
            return Float.compare(this.r, another.r);
        }

        public int getColor(float maxR) {
            //float sat = r
            return Color.RED;
        }

        @Override
        public String toString() {
            return String.format("x:%.2f y:%.2f r:%.2f", x, y, r);
        }
    }
}
