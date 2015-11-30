package com.apps.darkone.redpitayascope.app_controller;

import android.content.Context;

import com.apps.darkone.redpitayascope.app_fragments.IAppFragmentView;
import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.application_services.AppServiceFactory;
import com.apps.darkone.redpitayascope.application_services.AppServiceManager;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOnChannelsValueListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;

import java.util.List;
import java.util.Vector;

/**
 * Created by DarkOne on 25.11.15.
 */
public class OscilloscopeFragmentControllerApp implements ITouchAppViewController, IOnChannelsValueListener {


    private IAppFragmentView mAppFragmentView;
    private IOscilloscopeApp mOscilloscopeApp;
    private AppServiceManager mAppServiceManager;
    private Context mContext;
    private OscilloscopeMode mMode;
    private Vector<ChannelEnum> mEnabledChannels;
    private ChannelEnum mSelectedChannel;


    public OscilloscopeFragmentControllerApp(IAppFragmentView appFragmentView, Context context) {

        this.mAppFragmentView = appFragmentView;
        this.mContext = context;
        this.mAppServiceManager = AppServiceFactory.getAppServiceManager(this.mContext);

        // Our application is the oscilloscope one
        this.mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();

        // MOde initialization
        this.mMode = OscilloscopeMode.AUTO;

        this.mSelectedChannel = ChannelEnum.CHANNEL1;
        this.mEnabledChannels = new Vector<ChannelEnum>();
        this.mEnabledChannels.add(ChannelEnum.CHANNEL1);


        // Update the view
//        this.mAppFragmentView.updateOscMode(this.mMode);
//        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
//        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);

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

        // Update the oscilloscope mode
        switch (this.mMode) {
            case SINGLE_SHOT:
                this.mMode = OscilloscopeMode.NORMAL;
                break;
            case NORMAL:
                this.mMode = OscilloscopeMode.AUTO;
                break;
            case AUTO:
                this.mMode = OscilloscopeMode.SINGLE_SHOT;
                break;
            default:
                break;
        }

        // Update the view
        this.mAppFragmentView.updateOscMode(this.mMode);

        //Change the model
        this.mOscilloscopeApp.setMode(this.mMode);
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

        // A double tap disable the channel if enabled
        if (this.mEnabledChannels.contains(ChannelEnum.CHANNEL1)) {

            this.mEnabledChannels.remove(ChannelEnum.CHANNEL1);

            //Check if the channel was selected
            if (this.mSelectedChannel == ChannelEnum.CHANNEL1) {
                // we have to switch either to channel 2 (if enabled), or none
                if (this.mEnabledChannels.size() != 0) {
                    this.mSelectedChannel = this.mEnabledChannels.get(0);
                } else {
                    this.mSelectedChannel = ChannelEnum.NONE;
                }
            }
        }

        //Update the view
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
    }

    @Override
    public void butC1SettingsOnSingleTapConfirmed() {
        // Single tap + not selected + not enabled mean : selected and enabled
        if ((this.mSelectedChannel != ChannelEnum.CHANNEL1) && !(this.mEnabledChannels.contains(ChannelEnum.CHANNEL1))) {
            this.mEnabledChannels.add(ChannelEnum.CHANNEL1);
            this.mSelectedChannel = ChannelEnum.CHANNEL1;
        }
        // If enable, the we only select it
        else if (this.mEnabledChannels.contains(ChannelEnum.CHANNEL1)) {
            this.mSelectedChannel = ChannelEnum.CHANNEL1;
        }

        //Update the view
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
    }

    @Override
    public void butC1SettingsOnLongPress() {

    }

    @Override
    public void butC2SettingsOnDoubleTap() {
// A double tap disable the channel if enabled
        if (this.mEnabledChannels.contains(ChannelEnum.CHANNEL2)) {

            this.mEnabledChannels.remove(ChannelEnum.CHANNEL2);

            //Check if the channel was selected
            if (this.mSelectedChannel == ChannelEnum.CHANNEL2) {
                // we have to switch either to channel 2 (if enabled), or none
                if (this.mEnabledChannels.size() != 0) {
                    this.mSelectedChannel = this.mEnabledChannels.get(0);
                } else {
                    this.mSelectedChannel = ChannelEnum.NONE;
                }
            }
        }

        //Update the view
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
    }

    @Override
    public void butC2SettingsOnSingleTapConfirmed() {
        // Single tap + not selected + not enabled mean : selected and enabled
        if ((this.mSelectedChannel != ChannelEnum.CHANNEL2) && !(this.mEnabledChannels.contains(ChannelEnum.CHANNEL2))) {
            this.mEnabledChannels.add(ChannelEnum.CHANNEL2);
            this.mSelectedChannel = ChannelEnum.CHANNEL2;
        }
        // If enable, the we only select it
        else if (this.mEnabledChannels.contains(ChannelEnum.CHANNEL2)) {
            this.mSelectedChannel = ChannelEnum.CHANNEL2;
        }

        //Update the view
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
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
