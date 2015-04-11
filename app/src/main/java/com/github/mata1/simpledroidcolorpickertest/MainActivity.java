package com.github.mata1.simpledroidcolorpickertest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.pickers.CircleColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.LinearColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.RingColorPicker;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;


public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{

    private SeekBar sbRing, sbGap, sbStroke;
    private RingColorPicker rcp;
    private CircleColorPicker ccp;
    private LinearColorPicker lcp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcp = (RingColorPicker)findViewById(R.id.rcp);
        rcp.setOnColorPickedListener(new OnColorPickedListener() {
            @Override
            public void colorPicked(int color) {
                Toast.makeText(getApplicationContext(), "Color selected: " + color, Toast.LENGTH_SHORT).show();
            }
        });
        ccp = (CircleColorPicker)findViewById(R.id.ccp);
        lcp = (LinearColorPicker)findViewById(R.id.lcp);

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
            rcp.setRingWidth(progress);
        } else if (seekBar.equals(sbGap)) {
            rcp.setGapWidth(progress);
        } else if (seekBar.equals(sbStroke)) {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void randomColor(View v) {
        Random r = new Random();
        int color = ColorUtils.getColorFromHSV(r.nextInt(360), r.nextFloat(), 1);
        rcp.setColor(color);
        ccp.setColor(color);
        lcp.setColor(color);
    }

    public void save(View v) {
        rcp.setDrawingCacheEnabled(true);
        Bitmap b = rcp.getDrawingCache();
        String sd = Environment.getExternalStorageDirectory() + File.separator + "RingColorPicker.png";
        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(sd));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
