package com.androidplot.xy;

import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.util.PixelUtils;

/**
 * Created by DarkOne on 09.11.15.
 */
public class TriggerLevel {

    private Paint triggerPaint;
    private double triggerLevel;


    public TriggerLevel()
    {
        this.triggerPaint = new Paint(Color.YELLOW);
        this.triggerLevel = 0.0;


        this.triggerPaint.setStrokeWidth(PixelUtils.dpToPix(2));
        this.triggerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.triggerPaint.setAntiAlias(true);

    }


    public double getTriggerLevel()
    {
        return triggerLevel;
    }

    public void setTriggerLevel(double level)
    {
        this.triggerLevel = level;
    }


    public Paint getTriggerPaint()
    {
        return this.triggerPaint;
    }

}
