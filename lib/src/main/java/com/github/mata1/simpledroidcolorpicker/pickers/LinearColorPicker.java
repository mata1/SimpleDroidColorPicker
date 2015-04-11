package com.github.mata1.simpledroidcolorpicker.pickers;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

/**
 * Linear Color Picker View
 */
public class LinearColorPicker extends RectHandleColorPicker {

    protected RectF mRect;

    private static final int RECT_EDGE_RADIUS = 10;

    public LinearColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mHandlePaint.setColor(COLORS[0]);

        mRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        mRect.set(
                getPaddingLeft() + HANDLE_WIDTH/2, // left
                getPaddingTop() + HANDLE_PADDING, // top
                w - getPaddingRight() - HANDLE_WIDTH/2, // right
                h - getPaddingBottom() - HANDLE_PADDING // bottom
        );

        float s = mHandleStrokePaint.getStrokeWidth()/2;
        mHandleRect.set(
                getPaddingLeft() + s, // left
                getPaddingTop() + s, // top
                getPaddingLeft() + HANDLE_WIDTH - s, // right
                h - getPaddingBottom() - s // bottom
        );

        Shader sweep = new LinearGradient(mRect.left, mRect.centerY(), mRect.right, mRect.centerY(), COLORS, null, LinearGradient.TileMode.CLAMP);
        mColorPaint.setShader(sweep);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw gradient
        canvas.drawRoundRect(mRect, RECT_EDGE_RADIUS, RECT_EDGE_RADIUS, mColorPaint);

        // draw handle
        canvas.drawRoundRect(mHandleRect, HANDLE_EDGE_RADIUS, HANDLE_EDGE_RADIUS, mHandlePaint);
        canvas.drawRoundRect(mHandleRect, HANDLE_EDGE_RADIUS, HANDLE_EDGE_RADIUS, mHandleStrokePaint);
    }

    @Override
    protected void handleTouch(int motionAction, float x, float y) {
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                // if over handle, grab handle
                mDragging = mHandleRect.contains(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                // check if handle grabbed and inside bounds
                if (mDragging)
                    moveHandleTo(x);
                break;

            case MotionEvent.ACTION_UP:
                // release handle or animate handle
                if (mDragging)
                    mDragging = false;
                else if (mRect.contains(x, y))
                    animateHandleTo(x);
                break;
        }
    }

    /**
     * Moves handle to new position and sets color.
     * X is clamped so handle does not leave bounds.
     * @param x new handle position
     */
    @Override
    protected void moveHandleTo(float x) {
        // move
        x = Utils.clamp(x, mRect.left, mRect.right);
        mHandleRect.offsetTo(x - mHandleRect.width()/2, mHandleRect.top);

        // repaint
        float fraction = (x - mRect.left) / mRect.width();
        int color = ColorUtils.getColorFromFraction(fraction);
        mHandlePaint.setColor(color);
        invalidate();

        // fire event
        if (mOnColorChangedListener != null)
            mOnColorChangedListener.colorChanged(color);
    }

    /**
     * Animate handle to new position
     * @param x new handle position
     */
    @Override
    protected void animateHandleTo(float x) {
        ValueAnimator anim = ValueAnimator.ofFloat(mHandleRect.centerX(), x);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveHandleTo((float) animation.getAnimatedValue());
            }
        });
        anim.start();
    }

    /*
    SETTERS/GETTERS
     */

    @Override
    public void setColor(int color) {
        float fraction = ColorUtils.getFractionFromColor(color);
        float newX = fraction * mRect.width() + mRect.left;
        animateHandleTo(newX);
    }
}
