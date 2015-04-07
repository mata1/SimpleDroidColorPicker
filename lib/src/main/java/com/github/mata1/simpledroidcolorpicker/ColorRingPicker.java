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
import android.view.MotionEvent;
import android.view.View;

import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Color Ring Picker View
 */
public class ColorRingPicker extends View {

    private OnColorPickedListener mListener;

    private Paint mOuterPaint, mOuterStrokePaint;
    private Paint mInnerPaint, mInnerStrokePaint;
    private Paint mHandlePaint;

    private RectF mHandleRect;

    private int mRingWidth, mStrokeWidth, mGapWidth; // view attributes
    private float mInnerRadius, mOuterRadius, mHalfWidth, mHalfHeight; // view measurements

    private float mAngle; // current selection angle
    private boolean mDragging; // whether handle is being dragged

    private static final int HANDLE_TOUCH_LIMIT = 15;
    private static final int HANDLE_WIDTH = 40;

    public ColorRingPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        init();
    }

    public ColorRingPicker(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    /**
     * Initialize member objects
     */
    private void init() {
        // init paints
        mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setColor(Color.WHITE);
        mOuterPaint.setStrokeWidth(mRingWidth);
        mOuterStrokePaint = new Paint(mOuterPaint);
        mOuterStrokePaint.setStrokeWidth(mRingWidth + mStrokeWidth * 2);

        mInnerPaint = new Paint(mOuterPaint);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setColor(Color.RED);
        mInnerStrokePaint = new Paint(mOuterPaint);
        mInnerStrokePaint.setStrokeWidth(mStrokeWidth * 2);

        mHandlePaint = new Paint(mOuterPaint);
        mHandlePaint.setStrokeWidth(mStrokeWidth);

        // init rectangle
        mHandleRect = new RectF();
    }

    /**
     * Initialize XML attributes
     * @param attrs xml attribute set
     */
    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker);

        try {
            mRingWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_ringWidth, 50);
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_strokeWidth, 5);
            mGapWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_gapWidth, 50);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHalfWidth = w / 2f;
        mHalfHeight = h / 2f;
        mOuterRadius = Math.min(mHalfWidth, mHalfHeight) - mOuterStrokePaint.getStrokeWidth()/2 - getMaxPadding();
        mInnerRadius = mOuterRadius - mOuterStrokePaint.getStrokeWidth()/2 - mGapWidth;

        mHandleRect.set(
                getPaddingLeft(), // left
                mHalfHeight - HANDLE_WIDTH/2, // top
                getPaddingLeft() + mOuterStrokePaint.getStrokeWidth(), // right
                mHalfHeight + HANDLE_WIDTH/2 // bottom
        );

        // create color ring shader
        Shader gradientShader = new SweepGradient(mHalfWidth, mHalfHeight, Utils.getHueRingColors(36), null);
        mOuterPaint.setShader(gradientShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // outer ring
        canvas.drawCircle(mHalfWidth, mHalfHeight, mOuterRadius, mOuterStrokePaint);
        canvas.drawCircle(mHalfWidth, mHalfHeight, mOuterRadius, mOuterPaint);

        // inner circle
        mInnerPaint.setColor(Utils.getColorFromAngle(mAngle));
        canvas.drawCircle(mHalfWidth, mHalfHeight, mInnerRadius, mInnerStrokePaint);
        canvas.drawCircle(mHalfWidth, mHalfHeight, mInnerRadius, mInnerPaint);

        // rotate handle
        canvas.save();
        canvas.rotate(mAngle, mHalfWidth, mHalfHeight);
        canvas.drawRoundRect(mHandleRect, 5, 5, mHandlePaint);
        canvas.restore();
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
                if (mDragging) {
                    mAngle = Utils.getAngleDeg(mHalfWidth, mHalfHeight, x, y);
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                // release handle
                mDragging = false;

                // TODO check if touching center
                if (mListener != null)
                    mListener.colorPicked(Utils.getColorFromAngle(mAngle));

                // TODO check if touching ring
                float newAngle = Utils.getAngleDeg(mHalfWidth, mHalfHeight, x, y);
                float diff = mAngle - newAngle;

                // correct angles
                if (diff < -180) diff += 360;
                else if (diff > 180) diff -= 360;

                // start animating
                ValueAnimator anim = ValueAnimator.ofFloat(mAngle, mAngle - diff);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mAngle = (float)animation.getAnimatedValue();
                        invalidate();
                    }
                });
                anim.start();
                break;

            case MotionEvent.ACTION_DOWN:
                // TODO check if touching ring
                float absDiff = Math.abs(Utils.getAngleDeg(mHalfWidth, mHalfHeight, x, y) - mAngle);
                if (absDiff < HANDLE_TOUCH_LIMIT)
                    mDragging = true;
                break;
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        int spec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY);
        setMeasuredDimension(spec, spec);
    }

    /**
     * Set outer color ring width
     * @param ringWidth outer ring width in pixels
     */
    public void setRingWidth(int ringWidth) {
        mRingWidth = ringWidth;
        mOuterPaint.setStrokeWidth(mRingWidth);
        mOuterStrokePaint.setStrokeWidth(mRingWidth + mStrokeWidth * 2);

        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }

    /**
     * Set gap width between outer ring and inner circle
     * @param gapWidth gap width in pixels
     */
    public void setGapWidth(int gapWidth) {
        mGapWidth = gapWidth;

        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }

    /**
     * Get view maximum padding
     * @return maximum padding
     */
    private int getMaxPadding() {
        return Math.max(Math.max(getPaddingLeft(), getPaddingRight()), Math.max(getPaddingTop(), getPaddingBottom()));
    }

    /**
     * Set listener for color picked event
     * @see com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener
     * @param eventListener OnColorPickedListener event listener
     */
    public void setOnColorPickedListener(OnColorPickedListener eventListener) {
        mListener = eventListener;
    }
}
