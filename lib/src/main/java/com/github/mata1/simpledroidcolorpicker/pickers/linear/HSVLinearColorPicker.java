package com.github.mata1.simpledroidcolorpicker.pickers.linear;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;

/**
 * HSV Linear color picker consisting of three linear color pickers
 */
public class HSVLinearColorPicker extends LinearLayout {

    private OnColorChangedListener mOnColorChangedListener;

    private HueLinearColorPicker mHueLCP;
    private SaturationLinearColorPicker mSatLCP;
    private ValueLinearColorPicker mValLCP;

    private float mHue, mSat, mVal;

    public HSVLinearColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HSVLinearColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.hsv_color_picker, null);

        mHue = 0;
        mSat = 1;
        mVal = 1;

        mHueLCP = (HueLinearColorPicker)v.findViewById(R.id.hue);
        mSatLCP = (SaturationLinearColorPicker)v.findViewById(R.id.sat);
        mValLCP = (ValueLinearColorPicker)v.findViewById(R.id.val);

        mHueLCP.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                mHue = ColorUtils.getHueFromColor(color);
                mSatLCP.updateHSV(mHue, mSat, mVal);
                mValLCP.updateHSV(mHue, mSat, mVal);

                if (mOnColorChangedListener != null)
                    mOnColorChangedListener.colorChanged(ColorUtils.getColorFromHSV(mHue, mSat, mVal));
            }
        });

        mSatLCP.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                mSat = ColorUtils.getSaturationFromColor(color);
                mHueLCP.updateHSV(mHue, mSat, mVal);
                mValLCP.updateHSV(mHue, mSat, mVal);

                if (mOnColorChangedListener != null)
                    mOnColorChangedListener.colorChanged(ColorUtils.getColorFromHSV(mHue, mSat, mVal));
            }
        });

        mValLCP.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                mVal = ColorUtils.getValueFromColor(color);
                mHueLCP.updateHSV(mHue, mSat, mVal);
                mSatLCP.updateHSV(mHue, mSat, mVal);

                if (mOnColorChangedListener != null)
                    mOnColorChangedListener.colorChanged(ColorUtils.getColorFromHSV(mHue, mSat, mVal));
            }
        });

        addView(v);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mOnColorChangedListener = listener;
    }
}
