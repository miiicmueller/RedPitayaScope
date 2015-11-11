package com.apps.darkone.redpitayascope.communication.commSAP;

import java.util.List;
import java.util.Map;

/**
 * Created by DarkOne on 07.10.15.
 */
public interface IOnDataListener {

    public void newDataAvailable(String appName,  List<Map<Number, Number>> newData);
}
