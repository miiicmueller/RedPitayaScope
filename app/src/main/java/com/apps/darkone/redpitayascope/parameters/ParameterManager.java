package com.apps.darkone.redpitayascope.parameters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

/**
 * Created by DarkOne on 09.10.15.
 */
public class ParameterManager {

    // Tools
    private Map<String, List<Parameter<Double>>> mAppsParameters;
    private Semaphore semaphore;

    private static final String LOG_TAG = "ParameterManager";

    public ParameterManager() {
        Log.d(LOG_TAG, "ParameterManager creation....");

        if (null == this.mAppsParameters) {
            this.mAppsParameters = new TreeMap<>();
        }

        this.semaphore = new Semaphore(1);
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


    public void addParameter(String appName, String paramName, Double paramValue, boolean isUpdatedByBoard) {
        if (this.isParamsAlreadyPresent(appName)) {
            this.mAppsParameters.get(appName).add(new Parameter(paramName, paramValue, isUpdatedByBoard));
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


    public JSONObject getJsonObject(List<Parameter<Double>> listToConvert) throws JSONException {

        JSONObject paramJsonTmp = new JSONObject();
        JSONObject jsonDatasetSection = new JSONObject();
        JSONObject jsonParamsSection = new JSONObject();


        for (Parameter<Double> parameter : listToConvert) {
            paramJsonTmp.put(parameter.getParamName(), (double) parameter.getParamValue());
        }

        // Encapsulate in the API JSON format
        jsonParamsSection = jsonParamsSection.put("params", paramJsonTmp);

        jsonDatasetSection = jsonDatasetSection.put("datasets", jsonParamsSection);


        return jsonDatasetSection;
    }


    public boolean updateParamsFromJson(String appName, JSONObject newParams) {

        if (this.isParamsAlreadyPresent(appName)) {

            try {
                this.semaphore.acquire();
                // Itération sur la liste
                for (Parameter<Double> parameter : this.mAppsParameters.get(appName)) {

                    try {
                        parameter.setParamValue(newParams.getDouble(parameter.getParamName()));
                        Log.d(LOG_TAG, "Parameter " + parameter.getParamName() + " updated!");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON parse error. The parameter " + parameter.getParamName() + " may not exsit...");
                    }


                }
                this.semaphore.release();
                return true;

            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Semaphore take error : " + e);
            }
        }
        return false;
    }


    public void updateChannelsStat(String appName, JSONObject newParams) {
        for (Parameter<Double> parameter : this.mAppsParameters.get(appName)) {

            try {
                if (parameter.isData()) {
                    parameter.setParamValue(newParams.getDouble(parameter.getParamName()));
                    Log.d(LOG_TAG, "Data " + parameter.getParamName() + " updated!");
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON parse error. The parameter " + parameter.getParamName() + " may not exsit...");
            }


        }
    }


    public boolean isParamsChanges(String appName, JSONObject newParams) {

        boolean compareResult = false;


        if (this.isParamsAlreadyPresent(appName)) {

            try {
                List<Parameter<Double>> listToCompare = createListFromJSON(newParams);


                compareResult = listToCompare.equals(this.mAppsParameters.get(appName));

                return compareResult;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "List creation error ! " + e.toString());
            }
        }
        return compareResult;
    }


    public JSONObject changeParamInJSON(String paramName, Double newParamValue, JSONObject jsonParams) throws JSONException {

        List<Parameter<Double>> paramList = createListFromJSON(jsonParams.getJSONObject("datasets").getJSONObject("params"));

        for (Parameter parameter : paramList) {
            if (parameter.getParamName().equals(paramName)) {
                parameter.setParamValue(newParamValue);
            }
        }

        return getJsonObject(paramList);
    }


    public void changeParamForAll(String appName, String paramName, Double newParamValue) {

        if (this.isParamsAlreadyPresent(appName)) {

            try {
                this.semaphore.acquire();
                // Itération sur la liste
                for (Parameter<Double> parameter : this.mAppsParameters.get(appName)) {

                    if (parameter.getParamName().equals(paramName)) {
                        parameter.setParamValue(newParamValue);
                    }

                }
                this.semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }


    /**
     * Create a list from a JSON object
     *
     * @param newParams
     * @return
     */
    private List<Parameter<Double>> createListFromJSON(JSONObject newParams) throws JSONException {
        List<Parameter<Double>> listTemp = new ArrayList<>();

        Iterator<String> iter = newParams.keys();

        while (iter.hasNext()) {
            String name = iter.next();
            listTemp.add(new Parameter<Double>(name, newParams.getDouble(name), false));
        }

        return listTemp;

    }


}
