package com.apps.darkone.redpitayascope.communication.commSAP;

import org.json.JSONObject;

/**
 * Created by DarkOne on 07.10.15.
 */
public interface IOnParamListener {

    public void newParamsAvailable(String appName, JSONObject newParams);

    public void newSignalDataAvailable(String appName, JSONObject newParams);

}
