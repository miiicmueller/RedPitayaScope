package com.apps.darkone.redpitayascope.app_fragments;

import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

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
    public void updateTimeRange(int tMin, int tMax);


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
    public void updateSelectedChannel(ChannelEnum[] selectedChannel);


    /**
     * Update the oscilloscope mode
     * @param mode
     */
    public void updateOscMode(OscilloscopeMode mode);

}

