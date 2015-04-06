package com.github.mata1.simpledroidcolorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * RGB Color Ring View. Starting with red color at 0 degrees.
 */
public class ColorRing extends View {

    private Paint mRingPaint;

    private RectF mRingRect;

    private int mThickness;

    public ColorRing(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ColorPicker,
                0, 0);

        try {
            mThickness = a.getDimensionPixelSize(R.styleable.ColorPicker_thickness, 50);
        } finally {
            a.recycle();
        }

        mRingPaint = new Paint();
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mThickness);

        mRingRect = new RectF();
    }

    public ColorRing(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Shader gradientShader = new SweepGradient(w/2f, h/2f, getAllColors(), getAllPositions());
        mRingPaint.setShader(gradientShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int s = mThickness/2;
        mRingRect.set(s + getPaddingLeft(),
                s + getPaddingTop(),
                getWidth() - s - getPaddingRight(),
                getHeight() - s - getPaddingBottom());

        canvas.drawOval(mRingRect, mRingPaint);

        Log.i("ColorRing", "drawing");
    }

    public void setThickness(int thickness) {
        mThickness = thickness;
        mRingPaint.setStrokeWidth(mThickness);
        invalidate();
    }

    public int getThickness() {
        return mThickness;
    }

    private float[] getAllPositions() {
        float[] p = new float[360];

        for (int i = 0; i < p.length; i++) {
            p[i] = (float)(i+1)/p.length;
        }

        return p;
    }

    private int[] getAllColors() {
        int[] c = new int[360];

        for (int i = 0; i < 360; i++)
            c[i] = Color.HSVToColor(new float[]{i, 1, 1});

        return c;
    }
}
