package com.apps.darkone.redpitayascope.app_fragments.oscilloscope;

import com.apps.darkone.redpitayascope.app_controller.oscilloscope.ChannelInfo;
import com.apps.darkone.redpitayascope.app_controller.oscilloscope.TriggerInfo;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

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
    public void updateTimeRange(double tMin, double tMax, TimeUnits timeUnits);


    /**
     * Update the trigger value
     * @param triggerInfo
     */
    public void updateTriggerValue(TriggerInfo triggerInfo);


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
     * Update the channel infos
     * @param channel : Channel to update
     * @param channelInfo : Channel information
     */
    public void updateChannelInfo(ChannelEnum channel, ChannelInfo channelInfo);


    /**
     * Update the trigger infos
     * @param triggerInfo : trigger information
     */
    public void updateTriggerInfo(TriggerInfo triggerInfo);

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


    /**
     * Show the custom menu of channel X
     */
    public void showChannelCustomMenu(ChannelEnum channelMenuToShow);

    /**
     * Show the custom menu of channel X
     */
    public void hideChannelCustomMenu(ChannelEnum channelMenuToShow);


    public void hideChannelCustomMenuLayout();

}

