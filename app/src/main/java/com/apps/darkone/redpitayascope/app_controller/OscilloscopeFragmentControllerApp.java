package com.apps.darkone.redpitayascope.app_controller;

import android.content.Context;

import com.apps.darkone.redpitayascope.app_fragments.IAppFragmentView;
import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.application_services.AppServiceFactory;
import com.apps.darkone.redpitayascope.application_services.AppServiceManager;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOnChannelsValueListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;

/**
 * Created by DarkOne on 25.11.15.
 */
public class OscilloscopeFragmentControllerApp implements ITouchAppViewController, IOnChannelsValueListener {


    private IAppFragmentView mAppFragmentView;
    private IOscilloscopeApp mOscilloscopeApp;
    private AppServiceManager mAppServiceManager;
    private Context mContext;


    public OscilloscopeFragmentControllerApp(IAppFragmentView appFragmentView, Context context) {

        this.mAppFragmentView = appFragmentView;
        this.mContext = context;
        this.mAppServiceManager = AppServiceFactory.getAppServiceManager(this.mContext);

        // Our application is the oscilloscope one
        this.mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();
    }


    @Override
    public void startController() {

        // Launch the app on the redpitaya board
        mAppServiceManager.setAppServiceFocus((IAppService) this.mOscilloscopeApp);

        // Add the new value listener
        mOscilloscopeApp.addAppValuesListener(this);
    }

    @Override
    public void stopController() {
        // remove the listener
        mOscilloscopeApp.removeAppValuesListener(this);
    }


    @Override
    public void onScaleFactorX(float scaleFactorValue) {

    }

    @Override
    public void onScaleFactorY(float scaleFactorValue) {

    }

    @Override
    public void onDoubleTapGesture() {

    }

    @Override
    public void onNewValues(Number[][][] newValuesArray) {
        this.mAppFragmentView.updateGraphValues(newValuesArray);
    }
}
