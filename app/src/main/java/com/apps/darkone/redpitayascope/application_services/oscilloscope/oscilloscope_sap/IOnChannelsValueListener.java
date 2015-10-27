package com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap;

/**
 * Created by DarkOne on 20.10.15.
 */
public interface IOnChannelsValueListener<T> {

    /**
     * Called when new values for the both channels are available
     * @param newValue
     */
    public void onNewValues(T newValue);

}
