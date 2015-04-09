package com.github.mata1.simpledroidcolorpicker.pickers;

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
import android.view.SoundEffectConstants;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Color Ring Picker View
 */
public class RingColorPicker extends ColorPicker {

    private Paint mOuterPaint, mOuterStrokePaint;
    private Paint mInnerPaint, mInnerStrokePaint;
    private Paint mHandlePaint;

    private RectF mHandleRect;

    private int mRingWidth, mStrokeWidth, mGapWidth; // view attributes
    private float mInnerRadius, mOuterRadius; // view measurements

    private float mAngle; // current selection angle
    private boolean mDragging; // whether handle is being dragged

    // handle constants
    private static final int HANDLE_TOUCH_LIMIT = 15;
    private static final int HANDLE_WIDTH = 40;
    private static final int HANDLE_PADDING = 10;

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
        // init paints
        mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setColor(Color.WHITE);
        mOuterPaint.setStrokeWidth(mRingWidth);
        mOuterStrokePaint = new Paint(mOuterPaint);
        mOuterStrokePaint.setStrokeWidth(mRingWidth + mStrokeWidth * 2);

        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setStyle(Paint.Style.FILL);
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
    protected void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker);

        try {
            mRingWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_ringWidth, HANDLE_WIDTH * 2);
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_strokeWidth, 4);
            mGapWidth = a.getDimensionPixelSize(R.styleable.ColorPicker_gapWidth, HANDLE_PADDING + HANDLE_WIDTH);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        float minCenter = Math.min(w / 2f, h / 2f);
        mOuterRadius = minCenter - mOuterStrokePaint.getStrokeWidth()/2 - getMaxPadding() - HANDLE_PADDING;
        mInnerRadius = mOuterRadius - mOuterStrokePaint.getStrokeWidth()/2 - mGapWidth;

        mHandleRect.set(
                -minCenter + getPaddingLeft(), // left
                -HANDLE_WIDTH/2, // top
                -minCenter + getPaddingLeft() + mOuterStrokePaint.getStrokeWidth() + HANDLE_PADDING*2, // right
                HANDLE_WIDTH/2 // bottom
        );

        // create color ring shader
        Shader gradientShader = new SweepGradient(0, 0, Utils.getHueRingColors(36), null);
        mOuterPaint.setShader(gradientShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        // outer ring
        canvas.drawCircle(0, 0, mOuterRadius, mOuterStrokePaint);
        canvas.drawCircle(0, 0, mOuterRadius, mOuterPaint);

        // inner circle
        mInnerPaint.setColor(Utils.getColorFromAngle(mAngle));
        if (mStrokeWidth != 0)
            canvas.drawCircle(0, 0, mInnerRadius, mInnerStrokePaint);
        canvas.drawCircle(0, 0, mInnerRadius, mInnerPaint);

        // rotate handle
        canvas.rotate(mAngle);
        canvas.drawRoundRect(mHandleRect, 5, 5, mHandlePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {
        float halfHeight = getHeight()/2f, halfWidth = getWidth()/2f;
        float angle = Utils.getAngleDeg(halfWidth, halfHeight, x, y);
        float dist = Utils.getDistance(halfWidth, halfHeight, x, y);
        boolean isTouchingRing = dist > mInnerRadius + mGapWidth - HANDLE_PADDING
                && dist < mOuterRadius + mOuterStrokePaint.getStrokeWidth()/2 + HANDLE_PADDING;
        boolean isTouchingCenter = dist < mInnerRadius;

        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                // check if touching handle
                float absDiff = Math.abs(angle - mAngle);
                if (absDiff < HANDLE_TOUCH_LIMIT && isTouchingRing)
                    mDragging = true;
                break;

            case MotionEvent.ACTION_MOVE:
                // check if dragging AND touching ring
                if (mDragging && isTouchingRing) {
                    mAngle = angle;
                    invalidate();
                }
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
                            mAngle = (float) animation.getAnimatedValue();
                            invalidate();
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
     * Set gap width between outer ring and inner circle. Values are clamped
     * @param gapWidth gap width in pixels
     */
    public void setGapWidth(int gapWidth) {
        mGapWidth = Math.max(gapWidth, HANDLE_PADDING * 2);//(int)Utils.clamp(gapWidth, HANDLE_PADDING*2, mHandleRect.width());

        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = Utils.clamp(strokeWidth, 0, 10);
        mOuterStrokePaint.setStrokeWidth(mRingWidth + mStrokeWidth * 2);
        mInnerStrokePaint.setStrokeWidth(mStrokeWidth * 2);
        mHandlePaint.setStrokeWidth(Math.max(mStrokeWidth, 2)); // handle paint should not be less than 2

        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        mOuterStrokePaint.setColor(strokeColor);
        mInnerStrokePaint.setColor(strokeColor);
        mHandlePaint.setColor(strokeColor);
        invalidate();
    }


}
