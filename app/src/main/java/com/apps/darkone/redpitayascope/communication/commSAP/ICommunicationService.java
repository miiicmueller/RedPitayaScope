package com.apps.darkone.redpitayascope.communication.commSAP;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by DarkOne on 06.10.15.
 */
public interface ICommunicationService {


    /**
     * Starts the communcation service with the given IPv4 address.
     * @param ipAddress : IPv4 address of the redpitaya board.
     */
    public void startService(String ipAddress, Context context);

    /**
     * Stop the service, if running.
     */
    public void stopService();

    /**
     * Check if the service is actually running.
     * @return TRUE if the service run. FALSE otherwise.
     */
    public boolean isServiceRunning();


    /**
     * Check if the service is used by an application.
     * @return TRUE if the service is used. FALSE otherwise.
     */
    public boolean isServiceUsed();


    /**
     * Should start a new application on the redpitaya. Ex "scope+gen".
     * @param appName : application name to be runned on the redpitaya board.
     */
    public void startApp(String appName);


    /**
     * Should stop the app who is actually running. Used this function before
     * starting a new application
     */
    public void stopActualRunningApp();


    /**
     * Gets the actual service app name
     * @return App name
     */
    public String getActualRunningAppName();


    /**
     * Add a listener to be called when some data arrives
     * @param dataListener
     */
    public void addOnDataListener(IOnDataListener dataListener);


    /**
     * Removes the data listener
     * @param dataListener
     */
    public void removeOnDataListener(IOnDataListener dataListener);


    /**
     * Add a listener to be called when params arrives
     * @param paramListener
     */
    public void addOnParamListener(IOnParamListener paramListener);

    /**
     * remove the params listener
     * @param paramListener
     */
    public void removeOnParamListener(IOnParamListener paramListener);


    /**
     * Execute a POST request on the red pitaya board
     * @param appName : Application name
     * @param params : parameters to post
     */
    public boolean asyncNewParamsPost(String appName, JSONObject params);

    /**
     * Add a listener to be called when some event occur with the communication
     * @param connectionListener
     */
    public void addOnConnectionListener(IOnConnectionListener connectionListener);

    /**
     * Remove the connection listener
     * @param connectionListener
     */
    public void removeOnConnectionListener(IOnConnectionListener connectionListener);


    /**
     * Retreive the connection status with the redpitaya board
     * @return
     */
    public Integer getConnectionStatus();



}
