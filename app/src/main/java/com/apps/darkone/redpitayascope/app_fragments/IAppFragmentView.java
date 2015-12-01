package com.apps.darkone.redpitayascope.app_fragments;

import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

import java.util.List;
import java.util.Vector;

/**
 * Created by DarkOne on 25.11.15.
 */
public interface IAppFragmentView {


    /**
     * Update the plot values.
     * @param newValuesArray : [Channel N][X values][Y values]
     */
    public void updateGraphValues(Number[][][] newValuesArray);


    /**
     * Update the time domain range of the plot
     * @param tMin
     * @param tMax
     */
    public void updateTimeRange(double tMin, double tMax);


    /**
     * Update the trigger value
     * @param triggerValue
     */
    public void updateTriggerValue(float triggerValue);


    /**
     * Update the trigger edges
     * @param trigEdge
     */
    public void updateTriggerMode(TriggerEdge trigEdge);


    /**
     * Update the selected channels
     * @param selectedChannel
     */
    public void updateSelectedChannel(ChannelEnum selectedChannel);


    /**
     * Update the enabled channels
     * @param selectedChannel
     */
    public void updateEnabledChannels(Vector<ChannelEnum> selectedChannel);


    /**
     * Update the oscilloscope mode
     * @param mode
     */
    public void updateOscMode(OscilloscopeMode mode);


    /**
     * Update the channels offset
     * @param channel
     * @param offset
     */
    public void updateChannelsOffset(ChannelEnum channel, double offset);


    /**
     * Update the time units
     * @param timeUnits
     */
    public void updateOscilloscopeTimeUnits(TimeUnits timeUnits);


}

