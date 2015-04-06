package com.github.mata1.simpledroidcolorpicker;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by matej on 22/01/15.
 */
public class ColorPicker extends RelativeLayout implements View.OnTouchListener {

    private OnColorPickedListener mListener;

    private ImageView mHandleView, mCenterView, mBackStrokeView;
    private ColorRing mColorRing;

    private int mColor, mCenterSize;


    public ColorPicker(Context context) {
        this(context, null);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.color_picker, null);

        mHandleView = (ImageView)v.findViewById(R.id.handle);
        mCenterView = (ImageView)v.findViewById(R.id.center);
        mColorRing = (ColorRing)v.findViewById(R.id.ring);
        mBackStrokeView = (ImageView)v.findViewById(R.id.backStroke);

        mHandleView.setOnTouchListener(this);
        mCenterView.setOnTouchListener(this);
        mColorRing.setOnTouchListener(this);

        addView(v);

        setColor(0);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterSize = Math.min(w, h)/2;
        resize();

        Log.d("size changed", w + " " + h);
    }

    private void resize() {
        LayoutParams lp;

        // set handle size
        lp = (LayoutParams) mHandleView.getLayoutParams();
        lp.width = mColorRing.getThickness() + mColorRing.getPaddingLeft()*2;
        mHandleView.setLayoutParams(lp);

        // set handle pivot
        mHandleView.setPivotY(mHandleView.getMeasuredHeight()/2);
        mHandleView.setPivotX(mCenterSize - getPaddingLeft());

        ShapeDrawable sd = new ShapeDrawable(new RectShape());
        //sd.getPaint().setStyle(Paint.Style.FILL);
        sd.getPaint().setStrokeWidth(mColorRing.getThickness() * 2);
        sd.getPaint().setColor(Color.BLACK);
        mBackStrokeView.setImageDrawable(sd);

        // set center size
        lp = (LayoutParams)mCenterView.getLayoutParams();
        lp.width = lp.height = mCenterSize - (mColorRing.getThickness() + mColorRing.getPaddingLeft()*1);
        mCenterView.setLayoutParams(lp);

        Log.d("resize", ".");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        int spec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY);
        super.onMeasure(spec, spec);

        Log.d("on measure min", min + "");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int[] location = new int[2];
        getLocationOnScreen(location);

        float x = event.getRawX() - location[0];
        float y = event.getRawY() - location[1];

        if (v.equals(mHandleView) && event.getAction() == MotionEvent.ACTION_MOVE) {

            mHandleView.setRotation((float) getAngleDeg(x, y));
            setColor(mHandleView.getRotation());

        } else if (v.equals(mCenterView) && event.getAction() == MotionEvent.ACTION_UP) {
            //Toast.makeText(getContext(), "CLICK", Toast.LENGTH_SHORT).show();
            v.playSoundEffect(SoundEffectConstants.CLICK);
            if (mListener != null)
                mListener.colorPicked(mColor);
        } else if (v.equals(mColorRing) && event.getAction() == MotionEvent.ACTION_DOWN) {

            float curAngle = mHandleView.getRotation();
            float deg = (float)getAngleDeg(x, y);
            float diff = curAngle - deg;

            //Log.i("ColorPicker", "angle: " + deg + " - current: " + curAngle + " diff: " + diff);

            setColor(deg);

            if (diff < -180) diff += 360;
            else if (diff > 180) diff -= 360;

            if (curAngle > 360)
                mHandleView.setRotation(curAngle - 360);
            else if (curAngle < 0)
                mHandleView.setRotation(curAngle + 360);

            mHandleView.animate().rotationBy(-diff);

            ValueAnimator anim = ValueAnimator.ofFloat(mHandleView.getRotation(), mHandleView.getRotation() - diff);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setColor((float) animation.getAnimatedValue());
                }
            });
            anim.start();
        }

        return true;
    }

    private double getAngle(float x, float y) {
        return Math.atan2(mCenterSize - x, mCenterSize - y) * -1 + Math.PI/2;
    }

    private double getAngleDeg(float x, float y) {
        return Math.toDegrees(getAngle(x, y));
    }

    private void setColor(float angle) {
        angle -= 180;
        if (angle < 0) angle += 360;
        if (angle >= 360) angle -= 360;
        mColor = Color.HSVToColor(new float[] { angle, 1, 1 });

        GradientDrawable sd = (GradientDrawable) mCenterView.getDrawable();
        sd.setColor(mColor);
    }

    public int getColor() {
        return mColor;
    }

    public void setThickness(int thickness) {
        mColorRing.setThickness(thickness);
        resize();
    }


    public interface OnColorPickedListener{
        public void colorPicked(int color);
    }

    public void setColorPickedListener(OnColorPickedListener eventListener) {
        mListener = eventListener;
    }

}
