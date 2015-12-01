package com.apps.darkone.redpitayascope.application_services.oscilloscope;

import com.androidplot.xy.XYSeries;

import java.util.List;

/**
 * Created by DarkOne on 02.11.15.
 */

public class OscilloscopeTimeValueSerie implements XYSeries {

    private StringBuffer mTitle;
    private int mSerieSize = 0;

    private Number[] xBuffer;
    private Number[] yBuffer;


    public OscilloscopeTimeValueSerie(String title) {
        this.mTitle = new StringBuffer(title);
        this.mSerieSize = 0;

        this.xBuffer = null;
        this.yBuffer = null;
    }
    
    public void updateFromXYSerie(Number[] xBuffer, Number[] yBuffer) {

        if (xBuffer.length != 0) {

            this.xBuffer = xBuffer;
            this.yBuffer = yBuffer;

            // Update the size
            mSerieSize = xBuffer.length;
        }
    }


    public void clear()
    {
        mSerieSize = 0;
    }


    @Override
    public int size() {
        return mSerieSize;
    }

    @Override
    public Number getX(int index) {

        if (index >= mSerieSize) {
            //throw new IllegalArgumentException();
            return 0.0;
        }
        return xBuffer[index];
    }

    @Override
    public Number getY(int index) {
        if (index >= mSerieSize) {
            //throw new IllegalArgumentException();
            return 0.0;
        }
        return yBuffer[index];
    }

    @Override
    public Number[] getBufferNumberX() {
        return xBuffer;
    }

    @Override
    public Number[] getBufferNumberY() {
        return yBuffer;
    }

    @Override
    public String getTitle() {
        return this.mTitle.toString();
    }
}
