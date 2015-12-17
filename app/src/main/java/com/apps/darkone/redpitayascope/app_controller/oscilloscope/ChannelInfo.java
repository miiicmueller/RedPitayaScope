package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

/**
 * Created by DarkOne on 09.12.15.
 */
public class ChannelInfo {


    private double mOffset;
    private double mMeanFreq;
    private double mAmplitude;
    private double mVoltagePerDiv;


    public ChannelInfo(double offset, double meanFreq, double amplitude, double voltagePerDiv) {
        this.mAmplitude = amplitude;
        this.mMeanFreq = meanFreq;
        this.mOffset = offset;
        this.mVoltagePerDiv = voltagePerDiv;
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

    public double getVoltagePerDiv() {
        return mVoltagePerDiv;
    }

}
