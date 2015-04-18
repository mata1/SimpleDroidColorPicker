package com.github.mata1.simpledroidcolorpicker.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.pickers.CircleColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.RingColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.linear.HSVLinearColorPicker;

/**
 * Created by matej on 17/04/15.
 */
public class ColorPickerDialog extends AlertDialog implements OnColorChangedListener, OnColorPickedListener {

    public enum PickerType {
        RING, CIRCLE, HSV
    }

    private OnColorPickedListener mListener;

    private int mColor = Color.RED;

    private View mColorPicker = null;

    public ColorPickerDialog(Context context, PickerType pickerType) {
        super(context);

        init(context, pickerType);
    }

    public ColorPickerDialog(Context context, PickerType pickerType, int color) {
        this(context, pickerType);
        mColor = color;
    }

    private void init(Context context, PickerType pickerType) {
        // create color picker based on type
        switch (pickerType) {
            case RING:
                mColorPicker = new RingColorPicker(context, null);
                ((RingColorPicker)mColorPicker).setOnColorPickedListener(this);
                break;
            case CIRCLE:
                mColorPicker = new CircleColorPicker(context, null);
                ((CircleColorPicker)mColorPicker).setOnColorChangedListener(this);
                break;
            case HSV:
                mColorPicker = new HSVLinearColorPicker(context, null); // TODO FIX
                ((HSVLinearColorPicker)mColorPicker).setOnColorChangedListener(this);
                break;
        }
        int pad = context.getResources().getDimensionPixelSize(R.dimen.default_padding);
        mColorPicker.setPadding(pad, pad, pad, pad);
        setView(mColorPicker);

        // set buttons if not ring
        if (pickerType != PickerType.RING)
            setButton(BUTTON_POSITIVE, "Select", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null)
                        mListener.colorPicked(mColor);
                }
            });
        //setButton(BUTTON_NEGATIVE, "Dismiss", (OnClickListener)null);
    }

    public void setOnColorPickedListener(OnColorPickedListener listener) {
        mListener = listener;
    }

    @Override
    public void colorChanged(int color) {
        mColor = color;
    }

    @Override
    public void colorPicked(int color) {
        if (mListener != null)
            mListener.colorPicked(color);
        dismiss();
    }
}
