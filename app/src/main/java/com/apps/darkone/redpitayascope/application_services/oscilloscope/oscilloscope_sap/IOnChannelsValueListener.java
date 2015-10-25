package com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap;

/**
 * Created by DarkOne on 20.10.15.
 */
public interface IOnChannelsValueListener<T> {

    public void onNewValue(T newValue);
}
