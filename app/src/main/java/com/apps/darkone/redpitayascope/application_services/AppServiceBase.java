package com.apps.darkone.redpitayascope.application_services;

import android.util.Log;

import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.app_service_sap.IOnServiceListener;
import com.apps.darkone.redpitayascope.app_service_sap.ServiceStatus;
import com.apps.darkone.redpitayascope.communication.CommunicationServiceFactory;
import com.apps.darkone.redpitayascope.communication.ConnectionEvent;
import com.apps.darkone.redpitayascope.communication.commSAP.ICommunicationService;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnConnectionListener;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnParamListener;
import com.apps.darkone.redpitayascope.parameters.ParameterManager;
import com.apps.darkone.redpitayascope.parameters.ParameterManagerFactory;

import org.json.JSONObject;

/**
 * Created by DarkOne on 22.10.15.
 */
public class AppServiceBase implements IAppService, IOnConnectionListener, IOnParamListener {

    private String mAppName;
    private ICommunicationService mCommunicationService;
    private ServiceStatus mServiceStatus;
    private IOnServiceListener mOnServiceListener;
    protected IOnAppParamsListener mOnAppParamsListener;
    protected ParameterManager mParameterManager;


    public AppServiceBase(String appName) {

        mAppName = appName;

        mOnServiceListener = null;

        Log.d(mAppName, "Creating new application service...");

        mServiceStatus = ServiceStatus.STOPPED;

        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        mCommunicationService.addOnConnectionListener(this);

        mParameterManager = ParameterManagerFactory.getParameterManagerInstance();
    }

    /**
     * Service app methods
     */

    @Override
    public void startAppService() {

        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Only start the app if the communication is running
        if (mCommunicationService.isServiceRunning()) {
            Log.d(mAppName, "Communication service is up");

            // The communication should not be used by another service
            if (!mCommunicationService.isServiceUsed()) {
                Log.d(mAppName, "Service is not used. OK!");

                //Change the app state
                mServiceStatus = ServiceStatus.LOADING;
                mCommunicationService.startApp(mAppName);

            } else {
                Log.d(mAppName, "Service used! Aborting !");
                mServiceStatus = ServiceStatus.ERROR;
            }
        } else {
            Log.d(mAppName, "Communication service is not up! Aborting !");
            mServiceStatus = ServiceStatus.ERROR;
        }
    }

    @Override
    public void stopAppService() {

        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Be careful. It will stop the current app, even if it's not this one.
        mCommunicationService.stopActualRunningApp();
    }

    @Override
    public void setOnServiceListener(IOnServiceListener mOnServiceListener) {
        this.mOnServiceListener = mOnServiceListener;
    }

    @Override
    public void removeOnServiceListener() {
        this.mOnServiceListener = null;
    }

    @Override
    public void setOnAppParamsListener(IOnAppParamsListener appParamsListener) {
        this.mOnAppParamsListener = appParamsListener;
    }

    @Override
    public ServiceStatus getAppServiceStatus() {
        return mServiceStatus;
    }


    @Override
    public String getAppServiceName() {
        return mAppName;
    }


    protected boolean isAppConcerned(String appNameToCheck) {
        return this.getAppServiceName().equals(appNameToCheck);
    }

    protected void postParamSafe(JSONObject requestParams) {
        // We can update the parameters
        mCommunicationService.asyncNewParamsPost(this.mAppName, requestParams);
    }

    @Override
    public void newParamsAvailable(String appName, JSONObject newParams) {
        // It's for us...
        if (isAppConcerned(appName)) {

            // Always upload the parameters
            mParameterManager.updateParamsFromJson(appName, newParams);

            // Tell listener that new params are available
            notifyParameterUpdated();
        }
    }

    @Override
    public void newSignalDataAvailable(String appName, JSONObject newParams) {
        // It's for us...
        if (isAppConcerned(appName)) {
            // Always upload the parameters
            mParameterManager.updateChannelsStat(appName, newParams);
        }
    }


    /**
     * Listener of the communication status
     *
     * @param appName         : App name concerned
     * @param connectionEvent : Event who occur
     */
    @Override
    public void onConnectionChanged(String appName, ConnectionEvent connectionEvent) {

        // It's for us...
        if (isAppConcerned(appName)) {
            Log.d(mAppName, "Connection state change : " + connectionEvent.getEventCode().toString());


            switch (connectionEvent.getEventCode()) {
                case STARTING_APP:
                    notifyServiceStatusChange(ServiceStatus.LOADING);
                    break;
                case APP_STARTED:
                    notifyServiceStatusChange(ServiceStatus.UP);
                    break;
                case APP_START_FAILED:
                case CONNECTION_ERROR:
                    notifyServiceStatusChange(ServiceStatus.ERROR);
                    break;
                case DISCONNECTED:
                    notifyServiceStatusChange(ServiceStatus.STOPPED);
                default:
                    break;
            }


        }

    }


    private void notifyParameterUpdated() {
        if (mOnAppParamsListener != null) {
            mOnAppParamsListener.onParametersUpdated();
        }
    }

    private void notifyServiceStatusChange(ServiceStatus status) {

        // Affect the status
        mServiceStatus = status;

        if (mOnServiceListener != null) {

            switch (mServiceStatus) {
                case LOADING:
                    mOnServiceListener.onServiceLoading(mAppName);
                    break;
                case UP:
                    mOnServiceListener.onServiceUp(mAppName);
                    break;
                case STOPPED:
                    mOnServiceListener.onServiceStopped(mAppName);
                    break;
                case ERROR:
                    mOnServiceListener.onServiceError(mAppName);
                    break;
                default:
                    break;
            }
        }
    }
}
