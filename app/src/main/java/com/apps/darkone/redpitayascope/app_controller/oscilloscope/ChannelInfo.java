package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

/**
 * Created by DarkOne on 09.12.15.
 */
public class ChannelInfo {


    private double mOffset;
    private double mViewOffset;
    private double mMeanFreq;
    private double mAmplitude;
    private double mVoltagePerDiv;


    public ChannelInfo(double viewOffset, double offset, double meanFreq, double amplitude, double voltagePerDiv) {
        this.mAmplitude = amplitude;
        this.mMeanFreq = meanFreq;
        this.mViewOffset = viewOffset;
        this.mOffset = offset;
        this.mVoltagePerDiv = voltagePerDiv;
    }

    public ChannelInfo() {
        this.mAmplitude = 0;
        this.mMeanFreq = 0;
        this.mOffset = 0;
        this.mVoltagePerDiv = 0;
        this.mViewOffset = 0;
    }


    public double getOffset() {
        return mOffset;
    }

    public double getViewOffset() {
        return mViewOffset;
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
