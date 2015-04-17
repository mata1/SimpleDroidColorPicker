package com.github.mata1.simpledroidcolorpickertest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.mata1.simpledroidcolorpicker.interfaces.OnColorPickedListener;
import com.github.mata1.simpledroidcolorpicker.pickers.CircleColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.linear.LinearColorPicker;
import com.github.mata1.simpledroidcolorpicker.pickers.RingColorPicker;
import com.github.mata1.simpledroidcolorpicker.utils.ColorUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private RingColorPicker rcp;
    private CircleColorPicker ccp;
    private LinearColorPicker lcp, lcp_sat, lcp_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SimpleDroidColorPicker");
        toolbar.setSubtitle("Example");
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        // linear color pickers
        lcp = (LinearColorPicker)findViewById(R.id.lcp_hue);
        lcp_sat = (LinearColorPicker)findViewById(R.id.lcp_sat);
        lcp_val = (LinearColorPicker)findViewById(R.id.lcp_val);

        // ring color picker
        rcp = (RingColorPicker)findViewById(R.id.rcp);
        rcp.setSaturationLinearColorPicker(lcp_sat);
        rcp.setValueLinearColorPicker(lcp_val);
        rcp.setOnColorPickedListener(new OnColorPickedListener() {
            @Override
            public void colorPicked(int color) {
                Toast.makeText(getApplicationContext(), "Color selected: " + color, Toast.LENGTH_SHORT).show();
                toolbar.setBackgroundColor(color);
            }
        });

        // circle color picker
        ccp = (CircleColorPicker)findViewById(R.id.ccp);
        //ccp.setValueLinearColorPicker(lcp_val);
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
