package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

/**
 * Created by DarkOne on 10.12.15.
 */
public class TriggerInfo {

    private final double mTriggerLevel;
    private final TriggerEdge mTriggerEdge;
    private final ChannelEnum mTriggerChannel;

    public TriggerInfo(double triggerLevel, TriggerEdge triggerEdge, ChannelEnum triggerChannel) {
        this.mTriggerLevel = triggerLevel;
        this.mTriggerEdge = triggerEdge;
        this.mTriggerChannel = triggerChannel;
    }

    public double getTriggerLevel() {
        return mTriggerLevel;
    }

    public TriggerEdge getTriggerEdge() {
        return mTriggerEdge;
    }

    public ChannelEnum getTriggerChannel() {
        return mTriggerChannel;
    }

}
