package com.github.mata1.simpledroidcolorpickertest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mata1.simpledroidcolorpicker.ColorRingPicker;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;


public class MainActivity extends Activity {

    SeekBar sbRing, sbGap;
    ColorRingPicker cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cr = (ColorRingPicker)findViewById(R.id.cr);
        cr.setOnColorPickedListener(new OnColorPickedListener() {
            @Override
            public void colorPicked(int color) {
                Toast.makeText(getApplicationContext(), "Color selected: " + color, Toast.LENGTH_SHORT).show();
            }
        });

        sbRing = (SeekBar)findViewById(R.id.sb_ring);
        sbRing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cr.setRingWidth(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbGap = (SeekBar)findViewById(R.id.sb_gap);
        sbGap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cr.setGapWidth(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
