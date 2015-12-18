package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

/**
 * Created by DarkOne on 10.12.15.
 */
public class TriggerInfo {

    private double mTriggerLevel;
    private TriggerEdge mTriggerEdge;
    private ChannelEnum mTriggerChannel;
    private boolean mSelected;

    public TriggerInfo(double triggerLevel, TriggerEdge triggerEdge, ChannelEnum triggerChannel, boolean selected) {
        this.mTriggerLevel = triggerLevel;
        this.mTriggerEdge = triggerEdge;
        this.mTriggerChannel = triggerChannel;
        this.mSelected = selected;
    }

    public TriggerInfo() {
        this.mTriggerLevel = 0;
        this.mTriggerEdge = TriggerEdge.RISING;
        this.mTriggerChannel = ChannelEnum.CHANNEL1;
        this.mSelected = true;
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

    public TriggerInfo setmTriggerLevel(double mTriggerLevel) {
        this.mTriggerLevel = mTriggerLevel;
        return this;
    }

    public TriggerInfo setmTriggerEdge(TriggerEdge mTriggerEdge) {
        this.mTriggerEdge = mTriggerEdge;
        return this;
    }

    public TriggerInfo setmTriggerChannel(ChannelEnum mTriggerChannel) {
        this.mTriggerChannel = mTriggerChannel;
        return this;
    }

    public TriggerInfo setmSelected(boolean mSelected) {
        this.mSelected = mSelected;
        return this;
    }

    public double getmTriggerLevel() {
        return mTriggerLevel;
    }

    public TriggerEdge getmTriggerEdge() {
        return mTriggerEdge;
    }

    public ChannelEnum getmTriggerChannel() {
        return mTriggerChannel;
    }

    public boolean ismSelected() {
        return mSelected;
    }
}
