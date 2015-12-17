package com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap;

/**
 * Created by DarkOne on 22.10.15.
 */
public interface IOscilloscopeApp {

    /**
     * Add a callback method when new value from channels arrives
     *
     * @param onChannelsValueListener
     */
    public void addAppValuesListener(IOnChannelsValueListener onChannelsValueListener);

    /**
     * Remove a callback method when new value from channels arrives
     *
     * @param onChannelsValueListener
     */
    public void removeAppValuesListener(IOnChannelsValueListener onChannelsValueListener);




    /**
     * Get the channel information
     *
     * @param channel
     * @return
     */
    public double getChannelFreq(ChannelEnum channel);
    public double getChannelMeanValue(ChannelEnum channel);
    public double getChannelAmplitude(ChannelEnum channel);
    public double getChannelScale(ChannelEnum channel);


    /**
     * Set the time data range
     *
     * @param xmin
     * @param xmax
     */
    public void setTimeLimits(Double xmin, Double xmax);

    /**
     * Get the actual time limits
     * @return
     */
    public double[] getTimeLimits();

    /**
     * Get the actual time limits
     * @return
     */
    public TimeUnits getTimeUnits();

    /**
     * Set the redpitaya trigger level
     *
     * @param triggerLevel= Level in volts
     */
    public void setTriggerLevel(double triggerLevel);


    /**
     * Set the redpitaya trigger edge
     *
     * @param triggerEdge
     */
    public void setTriggerEdge(TriggerEdge triggerEdge);


    /**
     * Set the trigger channel
     *
     * @param channel 1 = Ch1, 2 = Ch2
     */
    public void setTriggerChannel(ChannelEnum channel);

    /**
     * Set the oscilloscope mode
     *
     * @param mode
     */
    public void setMode(OscilloscopeMode mode);


    /**
     * Set the time units for the red pitaya
     *
     * @param timeUnits
     */
    public void setTimeUnits(TimeUnits timeUnits);


    /**
     * Set if we use averaging
     *
     * @param avrgState
     */
    public void setAvergagingState(boolean avrgState);


    /**
     * Set the channel offset
     *
     * @param channel
     * @param offset
     */
    public void setChannelOffset(ChannelEnum channel, double offset);


    /**
     * Set the probe attenuation of the channel. 1X, 10X
     *
     * @param channel
     * @param attenuation
     */
    public void setChannelProbeAtt(ChannelEnum channel, ProbeAttenuation attenuation);


    /**
     * Set the channel gain. HV, LV
     *
     * @param channel
     * @param gain
     */
    public void setChannelGain(ChannelEnum channel, ChannelGain gain);


    /**
     * Set the division voltage. Ex.: 1V/div
     * @param channel
     * @param divisionVoltage
     */
    public void setChannelDivisionVoltageGain(ChannelEnum channel, double divisionVoltage);

}
