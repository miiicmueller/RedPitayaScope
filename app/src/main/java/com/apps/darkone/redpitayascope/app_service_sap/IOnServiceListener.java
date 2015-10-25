package com.apps.darkone.redpitayascope.app_service_sap;

/**
 * Created by DarkOne on 23.10.15.
 */
public interface IOnServiceListener {

    /**
     * When service is up. (App is started and we are receiving datas)
     * @param appName
     */
    public void onServiceUp(String appName);

    /**
     * When service is stopper. (App is stopped and unloaded)
     * @param appName
     */
    public void onServiceStopped(String appName);

    /**
     * If the service is in error
     * @param appName
     */
    public void onServiceError(String appName);

    /**
     * If the service is loading the application...
     * @param appName
     */
    public void onServiceLoading(String appName);


}
