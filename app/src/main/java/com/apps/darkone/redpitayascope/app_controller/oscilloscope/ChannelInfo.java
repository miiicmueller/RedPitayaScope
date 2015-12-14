package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

/**
 * Created by DarkOne on 09.12.15.
 */
public class ChannelInfo {


    private double mOffset;
    private double mMeanFreq;
    private double mAmplitude;

    public ChannelInfo(double offset, double meanFreq, double amplitude)
    {
        this.mAmplitude = amplitude;
        this.mMeanFreq = meanFreq;
        this.mOffset = offset;
    }


    public double getOffset() {
        return mOffset;
    }

    public double getMeanFreq() {
        return mMeanFreq;
    }

    public double getAmplitude() {
        return mAmplitude;
    }
}
