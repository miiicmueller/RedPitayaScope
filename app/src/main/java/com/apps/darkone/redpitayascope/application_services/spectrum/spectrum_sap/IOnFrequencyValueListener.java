package com.apps.darkone.redpitayascope.application_services.spectrum.spectrum_sap;

/**
 * Created by DarkOne on 20.10.15.
 */
public interface IOnFrequencyValueListener<T> {

    public void onNewValue(T newValue);
}
