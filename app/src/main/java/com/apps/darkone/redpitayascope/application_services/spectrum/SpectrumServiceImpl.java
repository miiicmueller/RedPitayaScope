package com.apps.darkone.redpitayascope.application_services.spectrum;

import android.util.Log;

import com.apps.darkone.redpitayascope.app_service_sap.ServiceStatus;
import com.apps.darkone.redpitayascope.application_services.AppServiceBase;
import com.apps.darkone.redpitayascope.application_services.spectrum.spectrum_sap.IOnFrequencyValueListener;
import com.apps.darkone.redpitayascope.application_services.spectrum.spectrum_sap.ISpectrumApp;
import com.apps.darkone.redpitayascope.communication.CommunicationServiceFactory;
import com.apps.darkone.redpitayascope.communication.commSAP.ICommunicationService;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnDataListener;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnParamListener;
import com.apps.darkone.redpitayascope.parameters.ParameterManager;
import com.apps.darkone.redpitayascope.parameters.ParameterManagerFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by DarkOne on 19.10.15.
 */
public class SpectrumServiceImpl extends AppServiceBase implements  IOnDataListener, ISpectrumApp {


    private static final String XMIN = "xmin";
    private static final String XMAX = "xmax";



    private static final String APP_SERVICE_NAME = "spectrum";
    private ICommunicationService mCommunicationService;
    private ParameterManager mParameterManager;
    private ServiceStatus serviceStatus;
    private List<IOnFrequencyValueListener> mOnChannelsValueListenersList;

    public SpectrumServiceImpl() {

        // Construct the base class with the application name
        super(APP_SERVICE_NAME);

        //Create the list
        mOnChannelsValueListenersList = new ArrayList<IOnFrequencyValueListener>();

        // Create the parameters
        mParameterManager = ParameterManagerFactory.getParameterManagerInstance();


        if (!mParameterManager.isParamsAlreadyPresent(APP_SERVICE_NAME)) {
            mParameterManager.addNewAppsParams(APP_SERVICE_NAME);
        }
        mParameterManager.addParameter(APP_SERVICE_NAME, XMAX, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, XMIN, 0.0, false);


        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Add the useful listeners
        mCommunicationService.addOnDataListener(this);
        mCommunicationService.addOnParamListener(this);
    }


    @Override
    public void addAppValuesListener(IOnFrequencyValueListener onChannelsValueListener) {
        mOnChannelsValueListenersList.add(onChannelsValueListener);
    }

    @Override
    public void removeAppValuesListener(IOnFrequencyValueListener onChannelsValueListener) {
        mOnChannelsValueListenersList.remove(onChannelsValueListener);
    }

    @Override
    public void newDataAvailable(String appName, List<List<Map<Double, Double>>> newData) {
        // TODO Transform the data to the used one (XYSeries)
        if (isAppConcerned(appName)) {
            Log.d(APP_SERVICE_NAME, "New data available : " + newData.toString());

            for(IOnFrequencyValueListener listener : mOnChannelsValueListenersList)
            {
                listener.onNewValue(null);
            }
        }
    }
}
