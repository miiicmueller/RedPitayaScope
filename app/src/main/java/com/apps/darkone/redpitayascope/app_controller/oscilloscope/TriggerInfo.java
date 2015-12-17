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
    private boolean mSelected;

    public TriggerInfo(double triggerLevel, TriggerEdge triggerEdge, ChannelEnum triggerChannel, boolean selected) {
        this.mTriggerLevel = triggerLevel;
        this.mTriggerEdge = triggerEdge;
        this.mTriggerChannel = triggerChannel;
        this.mSelected = selected;
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

    public boolean isSelected() {
        return this.mSelected;
    }

}
