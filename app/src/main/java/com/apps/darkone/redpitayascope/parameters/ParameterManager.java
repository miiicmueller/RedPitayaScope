package com.apps.darkone.redpitayascope.parameters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by DarkOne on 09.10.15.
 */
public class ParameterManager {

    // Tools
    private Map<String, List<Parameter<Double>>> mAppsParameters;


    private static final String LOG_TAG = "ParameterManager";

    public ParameterManager() {
        Log.d(LOG_TAG, "ParameterManager creation....");

        if (null == this.mAppsParameters) {
            this.mAppsParameters = new TreeMap<>();
        }
    }


    public boolean isParamsAlreadyPresent(String appName) {
        return this.mAppsParameters.containsKey(appName);
    }

    public void addNewAppsParams(String appName) {
        this.mAppsParameters.put(appName, new ArrayList<Parameter<Double>>());
    }

    public Parameter getSingleParameter(String appName, String paramName) {
        for (Parameter param : this.mAppsParameters.get(appName)) {
            if (param.getParamName().equals(paramName)) {
                return param;
            }
        }

        Log.d(LOG_TAG, "Parameter " + paramName + " not found !");
        return null;
    }

    public List<Parameter<Double>> getParamList(String appName) {
        return this.mAppsParameters.get(appName);
    }


    public void addParameter(String appName, String paramName, Double paramValue) {
        if (this.isParamsAlreadyPresent(appName)) {
            this.mAppsParameters.get(appName).add(new Parameter(paramName, paramValue));
        }
    }

    public boolean addParamList(String appName, List<Parameter<Double>> parameterList) {
        if (this.isParamsAlreadyPresent(appName)) {
            // remove the old list
            this.mAppsParameters.remove(appName);
            this.mAppsParameters.put(appName, parameterList);

            return true;
        }

        return false;
    }

    public void removeAllParams(String appName) {
        this.mAppsParameters.clear();
    }


    public JSONObject getJsonObject(String appName) throws JSONException {

        JSONObject paramJsonTmp = new JSONObject();
        JSONObject jsonDatasetSection = new JSONObject();
        JSONObject jsonParamsSection = new JSONObject();

        if (isParamsAlreadyPresent(appName)) {
            for (Parameter<Double> parameter : mAppsParameters.get(appName)) {

                paramJsonTmp.put(parameter.getParamName(), (double) parameter.getParamValue());

            }

            // Encapsulate in the API JSON format
            jsonParamsSection = jsonParamsSection.put("params", paramJsonTmp);

            jsonDatasetSection = jsonDatasetSection.put("datasets", jsonParamsSection);
        }

        return jsonDatasetSection;
    }


    public boolean updateParamsFromJson(String appName, JSONObject newParams) {

        if (this.isParamsAlreadyPresent(appName)) {

            // Itération sur la liste
            for (Parameter<Double> parameter : this.mAppsParameters.get(appName)) {

                try {
                    parameter.setParamValue(newParams.getDouble(parameter.getParamName()));
                    Log.d(LOG_TAG, "Parameter " + parameter.getParamName() + " updated!");
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSON parse error. The parameter " + parameter.getParamName() + " may not exsit...");
                }
            }
            return true;
        }
        return false;
    }


}
