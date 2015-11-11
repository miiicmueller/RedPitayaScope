package com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap;

import com.androidplot.xy.XYSeries;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.OscilloscopeTimeValueSerie;

import java.util.List;


/**
 * Created by DarkOne on 20.10.15.
 */
public interface IOnChannelsValueListener {

    /**
     * Called when new values for the both channels are available
     * @param newValuesArray
     */
    public void onNewValues(Number[][][] newValuesArray);

}
