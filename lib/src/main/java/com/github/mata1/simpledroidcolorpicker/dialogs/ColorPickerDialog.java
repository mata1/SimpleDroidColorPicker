package com.github.mata1.simpledroidcolorpicker.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import com.github.mata1.simpledroidcolorpicker.R;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorChangedListener;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.pickers.CircleColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.ColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.RingColorPicker;

/**
 * Created by matej on 17/04/15.
 */
public class ColorPickerDialog extends AlertDialog {

    public enum PickerType {
        RING, CIRCLE, HSV
    }

    private OnColorPickedListener mListener;

    private int mColor = Color.RED;

    private ColorPicker mColorPicker = null;

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
                break;
            case CIRCLE:
                mColorPicker = new CircleColorPicker(context, null);
                break;
            case HSV:
                mColorPicker = null; // TODO FIX
                break;
        }
        int pad = context.getResources().getDimensionPixelSize(R.dimen.default_padding);
        mColorPicker.setPadding(pad, pad, pad, pad);
        setView(mColorPicker);

        // set listener
        mColorPicker.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                mColor = color;
            }
        });
        mColorPicker.setOnColorPickedListener(new OnColorPickedListener() {
            @Override
            public void colorPicked(int color) {
                if (mListener != null)
                    mListener.colorPicked(color);
                dismiss();
            }
        });

        // set buttons
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
}
