package com.apps.darkone.redpitayascope.application_services.spectrum.spectrum_sap;

/**
 * Created by DarkOne on 22.10.15.
 */
public interface ISpectrumApp {

    /**
     * Add a callback method when new value from channels arrives
     * @param onChannelsValueListener
     */
    public void addAppValuesListener(IOnFrequencyValueListener onChannelsValueListener);

    /**
     * Remove a callback method when new value from channels arrives
     * @param onChannelsValueListener
     */
    public void removeAppValuesListener(IOnFrequencyValueListener onChannelsValueListener);

    // TODO @Matthieu. Construct the Interface

}
