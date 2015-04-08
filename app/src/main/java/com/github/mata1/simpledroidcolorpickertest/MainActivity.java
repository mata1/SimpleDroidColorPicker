package com.github.mata1.simpledroidcolorpickertest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mata1.simpledroidcolorpicker.pickers.RingColorPicker;
import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;


public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{

    SeekBar sbRing, sbGap, sbStroke;
    RingColorPicker cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cr = (RingColorPicker)findViewById(R.id.cr);
        cr.setOnColorPickedListener(new OnColorPickedListener() {
            @Override
            public void colorPicked(int color) {
                Toast.makeText(getApplicationContext(), "Color selected: " + color, Toast.LENGTH_SHORT).show();
            }
        });

        sbRing = (SeekBar)findViewById(R.id.sb_ring);
        sbRing.setOnSeekBarChangeListener(this);
        sbGap = (SeekBar)findViewById(R.id.sb_gap);
        sbGap.setOnSeekBarChangeListener(this);
        sbStroke = (SeekBar)findViewById(R.id.sb_stroke);
        sbStroke.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(sbRing)) {
            cr.setRingWidth(progress);
        } else if (seekBar.equals(sbGap)) {
            cr.setGapWidth(progress);
        } else if (seekBar.equals(sbStroke)) {
            cr.setStrokeWidth(progress);
            //cr.setStrokeColor(Utils.getColorFromFraction((new Random()).nextFloat()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void save(View v) {
        cr.setDrawingCacheEnabled(true);
        Bitmap b = cr.getDrawingCache();
        String sd = Environment.getExternalStorageDirectory() + File.separator + "RingColorPicker.png";
        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(sd));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
