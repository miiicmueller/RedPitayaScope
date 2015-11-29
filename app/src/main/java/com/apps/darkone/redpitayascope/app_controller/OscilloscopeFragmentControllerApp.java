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
    public void butOscModeOnDoubleTap() {

    }

    @Override
    public void butOscModeOnSingleTapConfirmed() {

    }

    @Override
    public void butOscModeOnLongPress() {

    }

    @Override
    public void butTrigSettingsOnDoubleTap() {

    }

    @Override
    public void butTrigSettingsOnSingleTapConfirmed() {

    }

    @Override
    public void butTrigSettingsOnLongPress() {

    }

    @Override
    public void butTimeSettingsOnDoubleTap() {

    }

    @Override
    public void butTimeSettingsOnSingleTapConfirmed() {

    }

    @Override
    public void butTimeSettingsOnLongPress() {

    }

    @Override
    public void butC1SettingsOnDoubleTap() {

    }

    @Override
    public void butC1SettingsOnSingleTapConfirmed() {

    }

    @Override
    public void butC1SettingsOnLongPress() {

    }

    @Override
    public void butC2SettingsOnDoubleTap() {

    }

    @Override
    public void butC2SettingsOnSingleTapConfirmed() {

    }

    @Override
    public void butC2SettingsOnLongPress() {

    }

    @Override
    public void mOscPlotOnDoubleTap() {

    }

    @Override
    public void mOscPlotOnSingleTapConfirmed() {

    }

    @Override
    public void mOscPlotOnLongPress() {

    }

    @Override
    public void mOscPlotOnScroll(float distanceX, float distanceY) {

    }

    @Override
    public void mOscPlotOnFling(float velocityX, float velocityY) {

    }

    @Override
    public void mOscPlotOnScaleBegin() {

    }

    @Override
    public void mOscPlotOnScaleX(float X) {

    }

    @Override
    public void mOscPlotOnScaleY(float Y) {

    }

    @Override
    public void mOscPlotOnScaleEnd() {

    }


    @Override
    public void onNewValues(Number[][][] newValuesArray) {
        this.mAppFragmentView.updateGraphValues(newValuesArray);
    }
}
