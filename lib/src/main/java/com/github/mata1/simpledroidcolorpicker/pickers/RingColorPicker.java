package com.github.mata1.simpledroidcolorpicker.pickers;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Color Ring Picker View
 */
public class RingColorPicker extends ColorPicker {

    private Paint mInnerPaint;

    private RectF mHandleRect;

    private int mRingWidth, mGapWidth; // view attributes
    private float mInnerRadius, mOuterRadius; // view measurements

    private float mAngle; // current selection angle

    // handle constants
    private static final int HANDLE_TOUCH_LIMIT = 15;
    private static final int HANDLE_WIDTH = 40;
    private static final int HANDLE_PADDING = 10;
    private static final int HANDLE_RADIUS = 5;

    public RingColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RingColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Initialize member objects
     */
    protected void init() {
        super.init();

        // init paints
        mColorPaint.setStyle(Paint.Style.STROKE);
        mColorPaint.setStrokeWidth(mRingWidth);
        mColorPaint.setShader(new SweepGradient(0, 0, COLORS, null));

        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setColor(Color.CYAN);

        mHandlePaint.setColor(Color.CYAN);

        // init rectangle
        mHandleRect = new RectF();
    }

    /**
     * Initialize XML attributes
     * @param attrs xml attribute set
     */
    protected void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker);

        try {
            mRingWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_ringWidth, HANDLE_WIDTH * 2);
            mGapWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_gapWidth, HANDLE_PADDING + HANDLE_WIDTH);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float minCenter = Math.min(mHalfWidth, mHalfHeight);
        int maxPadding = getMaxPadding();
        mOuterRadius = minCenter - mColorPaint.getStrokeWidth()/2 - maxPadding - HANDLE_PADDING;
        mInnerRadius = mOuterRadius - mColorPaint.getStrokeWidth()/2 - mGapWidth;

        mHandleRect.set(
                -minCenter + getPaddingLeft(), // left
                -HANDLE_WIDTH/2, // top
                -minCenter + getPaddingLeft() + mColorPaint.getStrokeWidth() + HANDLE_PADDING*2, // right
                HANDLE_WIDTH/2 // bottom
        );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mHalfWidth, mHalfHeight);
        // outer ring
        canvas.drawCircle(0, 0, mOuterRadius, mColorPaint);

        // inner circle
        canvas.drawCircle(0, 0, mInnerRadius, mInnerPaint);

        // rotate handle
        canvas.rotate(mAngle);
        canvas.drawRoundRect(mHandleRect, HANDLE_RADIUS, HANDLE_RADIUS, mHandlePaint);
        canvas.drawRoundRect(mHandleRect, HANDLE_RADIUS, HANDLE_RADIUS, mHandleStrokePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {
        float angle = Utils.getAngleDeg(mHalfWidth, mHalfHeight, x, y);
        float dist = Utils.getDistance(mHalfWidth, mHalfHeight, x, y);

        boolean isTouchingRing = dist > mInnerRadius + mGapWidth - HANDLE_PADDING
                && dist < mOuterRadius + mColorPaint.getStrokeWidth()/2 + HANDLE_PADDING;
        boolean isTouchingCenter = dist < mInnerRadius;
        boolean isOutsideCenter = dist > mInnerRadius;

        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                // check if touching handle
                float absDiff = Math.abs(angle - mAngle);
                mDragging = absDiff < HANDLE_TOUCH_LIMIT && isTouchingRing;
                break;

            case MotionEvent.ACTION_MOVE:
                // check if dragging AND touching ring
                if (mDragging && isOutsideCenter)
                    moveHandleTo(angle);
                break;

            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    // release handle if dragging
                    mDragging = false;
                } else if (mOnColorPickedListener != null && isTouchingCenter) {
                    // fire event if touching center
                    mOnColorPickedListener.colorPicked(Utils.getColorFromAngle(mAngle));
                    playSoundEffect(SoundEffectConstants.CLICK);
                } else if (isTouchingRing) {
                    // move handle if touching ring
                    float diff = mAngle - angle;

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
                            moveHandleTo((float) animation.getAnimatedValue());
                        }
                    });
                    anim.start();
                }
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(min, min);
    }

    private void moveHandleTo(float angle) {
        mAngle = angle;
        int color = Utils.getColorFromAngle(mAngle);
        mInnerPaint.setColor(color);
        mHandlePaint.setColor(color);
        invalidate();
    }

    /**
     * Set outer color ring width
     * @param ringWidth outer ring width in pixels
     */
    public void setRingWidth(int ringWidth) {
        mRingWidth = ringWidth;
        mColorPaint.setStrokeWidth(mRingWidth);

        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }

    /**
     * Set gap width between outer ring and inner circle. Values are clamped
     * @param gapWidth gap width in pixels
     */
    public void setGapWidth(int gapWidth) {
        mGapWidth = Math.max(gapWidth, HANDLE_PADDING * 2);//(int)Utils.clamp(gapWidth, HANDLE_PADDING*2, mHandleRect.width());

        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }
}
