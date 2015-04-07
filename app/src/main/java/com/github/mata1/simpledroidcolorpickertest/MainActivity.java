package com.github.mata1.simpledroidcolorpickertest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.github.mata1.simpledroidcolorpicker.ColorRingPicker;


public class MainActivity extends Activity {

    SeekBar sb;
    ColorRingPicker cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cr = (ColorRingPicker)findViewById(R.id.cr);

        sb = (SeekBar)findViewById(R.id.sb);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cr.setThickness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
