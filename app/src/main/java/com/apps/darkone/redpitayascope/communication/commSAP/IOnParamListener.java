package com.apps.darkone.redpitayascope.communication.commSAP;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by DarkOne on 07.10.15.
 */
public interface IOnParamListener {

    public void newParamsAvailable(String appName, JSONObject newParams);
}
