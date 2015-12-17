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

    public ChannelInfo() {
        this.mAmplitude = 0;
        this.mMeanFreq = 0;
        this.mOffset = 0;
        this.mVoltagePerDiv = 0;
    }

    public ChannelInfo(final ChannelInfo channel) {
        this.mAmplitude = channel.getAmplitude();
        this.mMeanFreq = channel.getMeanFreq();
        this.mOffset = channel.getOffset();
        this.mVoltagePerDiv = channel.getVoltagePerDiv();
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


    public void setmOffset(double mOffset) {
        this.mOffset = mOffset;
    }

    public void setmMeanFreq(double mMeanFreq) {
        this.mMeanFreq = mMeanFreq;
    }

    public void setmAmplitude(double mAmplitude) {
        this.mAmplitude = mAmplitude;
    }

    public void setmVoltagePerDiv(double mVoltagePerDiv) {
        this.mVoltagePerDiv = mVoltagePerDiv;
    }
}
