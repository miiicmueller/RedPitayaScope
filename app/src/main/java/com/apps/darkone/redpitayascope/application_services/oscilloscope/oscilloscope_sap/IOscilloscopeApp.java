package com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap;

/**
 * Created by DarkOne on 22.10.15.
 */
public interface IOscilloscopeApp {

    /**
     * Add a callback method when new value from channels arrives
     * @param onChannelsValueListener
     */
    public void addAppValuesListener(IOnChannelsValueListener onChannelsValueListener);

    /**
     * Remove a callback method when new value from channels arrives
     * @param onChannelsValueListener
     */
    public void removeAppValuesListener(IOnChannelsValueListener onChannelsValueListener);


    /**
     * Get the channel 1 mean value
     * @return Channel 1 mean value
     */
    public Double getChannel1MeanValue();

    /**
     * Get the channel 2 mean value
     * @return Channel 2 mean value
     */
    public Double getChannel2MeanValue();


    /**
     * FIXME ... only for debugging
     * @param xmin
     * @param xmax
     */
    public void setTimeLimits(Double xmin, Double xmax);


    // TODO @Matthieu. Construct the Interface

}
