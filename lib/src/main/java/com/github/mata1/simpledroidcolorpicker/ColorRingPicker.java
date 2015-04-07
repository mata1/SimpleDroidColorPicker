package com.github.mata1.simpledroidcolorpicker;

import android.animation.ValueAnimator;
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
import android.view.MotionEvent;
import android.view.View;

import com.github.mata1.simpledroidcolorpicker.util.Utils;

/**
 * RGB Color Ring View. Starting with red color at 0 degrees.
 */
public class ColorRingPicker extends View {

    private OnColorPickedListener mListener;

    private Paint mRingPaint, mStrokePaint, mInnerPaint, mInnerStrokePaint, mHandlePaint;
    private RectF mHandleRect;

    private int mThickness;

    private float mAngle; // current selection angle

    public ColorRingPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPicker);

        try {
            mThickness = a.getDimensionPixelSize(R.styleable.ColorPicker_thickness, 50);
        } finally {
            a.recycle();
        }

        mRingPaint = new Paint();
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mThickness);

        mStrokePaint = new Paint(mRingPaint);
        mStrokePaint.setColor(Color.WHITE);
        mStrokePaint.setStrokeWidth(mThickness+10);

        mInnerPaint = new Paint(mRingPaint);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setColor(Color.RED);

        mInnerStrokePaint = new Paint(mRingPaint);
        mInnerStrokePaint.setStrokeWidth(10);
        mInnerStrokePaint.setStyle(Paint.Style.STROKE);
        mInnerStrokePaint.setColor(Color.WHITE);

        mHandlePaint = new Paint(mInnerStrokePaint);
        mHandlePaint.setStrokeWidth(10/2f);

        mHandleRect = new RectF();
    }

    public ColorRingPicker(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Shader gradientShader = new SweepGradient(w/2f, h/2f, getAllColors(), getAllPositions());
        mRingPaint.setShader(gradientShader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        int spec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY);
        setMeasuredDimension(spec, spec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float GAP = 50; // TODO change to global, add setter
        float center = Math.min(getWidth(), getHeight())/2f;
        float rOuter = center - mStrokePaint.getStrokeWidth()/2 - getPaddingTop();
        float rInner = rOuter - mStrokePaint.getStrokeWidth()/2 - GAP;
        float w = getWidth()/2f, h = getHeight()/2f;

        mHandleRect.set(
                getPaddingLeft(), // left
                h - 20, // top
                getPaddingLeft() + mStrokePaint.getStrokeWidth(), // right
                h + 20 // bottom
        );

        // outer ring
        canvas.drawCircle(w, h, rOuter, mStrokePaint);
        canvas.drawCircle(w, h, rOuter, mRingPaint);

        // inner circle
        mInnerPaint.setColor(Utils.getColorFromAngle(mAngle));
        canvas.drawCircle(w, h, rInner, mInnerStrokePaint);
        canvas.drawCircle(w, h, rInner, mInnerPaint);

        // rotate handle
        canvas.save();
        canvas.rotate(mAngle, w, h);
        canvas.drawRoundRect(mHandleRect, 5, 5, mHandlePaint);
        canvas.restore();

        Log.i("ColorRing", "drawing");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int[] location = new int[2];
        getLocationOnScreen(location);

        float x = event.getRawX() - location[0];
        float y = event.getRawY() - location[1];

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // TODO check if touching ring
                mAngle = (float)getAngleDeg(x, y);
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                // TODO check if touching center, fire event
                if (mListener != null)
                    mListener.colorPicked(Utils.getColorFromAngle(mAngle));

                // TODO check if touching ring
                float newAngle = (float)getAngleDeg(x, y);
                float diff = mAngle - newAngle;

                // correct angles
                if (diff < -180) diff += 360;
                else if (diff > 180) diff -= 360;

                if (mAngle > 360) mAngle -= 360;
                else if (mAngle < 0) mAngle += 360;

                // start animating
                ValueAnimator anim = ValueAnimator.ofFloat(mAngle, mAngle - diff);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mAngle = (float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
                anim.start();
                break;
            case MotionEvent.ACTION_DOWN:
                // TODO check if touching ring
                break;
        }

        return true;
    }

    private double getAngle(float x, float y) {
        return Math.atan2(getWidth()/2f - x, getHeight()/2f - y) * -1 + Math.PI/2;
    }

    private double getAngleDeg(float x, float y) {
        return Math.toDegrees(getAngle(x, y));
    }

    public void setThickness(int thickness) {
        mThickness = thickness;
        mRingPaint.setStrokeWidth(mThickness);
        mStrokePaint.setStrokeWidth(mThickness + 10);
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

    public interface OnColorPickedListener{
        public void colorPicked(int color);
    }

    public void setColorPickedListener(OnColorPickedListener eventListener) {
        mListener = eventListener;
    }
}
