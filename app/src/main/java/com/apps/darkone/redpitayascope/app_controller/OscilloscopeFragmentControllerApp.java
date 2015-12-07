package com.apps.darkone.redpitayascope.app_controller;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.apps.darkone.redpitayascope.R;
import com.apps.darkone.redpitayascope.app_fragments.IAppFragmentView;
import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.application_services.AppServiceBase;
import com.apps.darkone.redpitayascope.application_services.AppServiceFactory;
import com.apps.darkone.redpitayascope.application_services.AppServiceManager;
import com.apps.darkone.redpitayascope.application_services.IOnAppParamsListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelGain;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOnChannelsValueListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;

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
    private double[] mChannelsOffset;
    private double[] mGraphTimeValue;
    private double mTriggerDelay;
    private double mTriggerLevel;
    private TimeUnits mGraphTimeUnit;

    private static final int CHANNEL_COUNT = 2;
    private static final String OSC_VIEW_CONTROLLER_TAG = "OSC_VIEW_CTRL";


    public OscilloscopeFragmentControllerApp(IAppFragmentView appFragmentView, Context context) {

        this.mAppFragmentView = appFragmentView;
        this.mContext = context;
        this.mAppServiceManager = AppServiceFactory.getAppServiceManager(this.mContext);

        // Our application is the oscilloscope one
        this.mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();

        // MOde initialization
        this.mMode = OscilloscopeMode.AUTO;

        // Channels selection
        this.mSelectedChannel = ChannelEnum.CHANNEL1;
        this.mEnabledChannels = new Vector<ChannelEnum>();
        this.mEnabledChannels.add(ChannelEnum.CHANNEL1);


        // Channels offset
        this.mChannelsOffset = new double[CHANNEL_COUNT];
        this.mChannelsOffset[0] = 0.0;
        this.mChannelsOffset[1] = 0.0;

        //Time domain range
        this.mGraphTimeValue = new double[2];
        this.mGraphTimeValue[0] = 0.0;
        this.mGraphTimeValue[1] = 131.0;
        this.mGraphTimeUnit = TimeUnits.US;


        // Trigger configuration
        this.mTriggerDelay = 0.0;
        this.mTriggerLevel = 0.0;

        // Update the view
        this.mAppFragmentView.updateOscMode(this.mMode);
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);
        this.mAppFragmentView.updateOscilloscopeTimeUnits(this.mGraphTimeUnit);


        //Update the model
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
        this.mOscilloscopeApp.setTimeUnits(this.mGraphTimeUnit);

        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL1, ChannelGain.HV);
        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL2, ChannelGain.HV);


        ((AppServiceBase) this.mOscilloscopeApp).setOnAppParamsListener(new IOnAppParamsListener() {
            @Override
            public void onParametersUpdated() {
                double[] lim = mOscilloscopeApp.getTimeLimits();

                mGraphTimeValue[0] = lim[0];
                mGraphTimeValue[1] = lim[1];
                //mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);

            }
        });

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

        resetValues();
    }

    @Override
    public void mOscPlotOnSingleTapConfirmed() {

    }

    @Override
    public void mOscPlotOnLongPress() {

    }

    @Override
    public void mOscPlotOnScroll(float distanceX, float distanceY) {


        double scrollAngle;
        double acos = Math.abs(distanceY) / Math.sqrt(distanceX * distanceX + distanceY * distanceY);


        // Check tha acos value is never > 1.0
        acos = Math.min(acos, 1.0);
        scrollAngle = Math.acos(acos) * (180.0 / Math.PI);

        // Change the value due to the gest
        if (scrollAngle > 80.0 && scrollAngle < 110.0) {
            // We have an horizontal scroll

            double ratio = (Math.abs(this.mGraphTimeValue[0] - this.mGraphTimeValue[1]) / 100.0);

            // Change trigger delay
            this.mTriggerDelay += (distanceX * ratio / (110.0));
            this.mGraphTimeValue[0] += (distanceX * ratio / 110.0);
            this.mGraphTimeValue[1] += (distanceX * ratio / 110.0);
            //this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);


        } else if (scrollAngle < 20.0) {
            // We have an vertical scroll

            //Choose what channel to change offset. We use - because of inverted axis system
            switch (this.mSelectedChannel) {
                case CHANNEL1:
                    this.mChannelsOffset[0] += (distanceY / 110.0);
                    this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
                    this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);

                    break;
                case CHANNEL2:
                    this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
                    this.mChannelsOffset[1] += (distanceY / 110.0);
                    this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
                    break;
                default:
                    break;
            }

        }


        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot scroll X :" + distanceX + " Y :" + distanceY + " norm : " + acos + " angle : " + scrollAngle);
    }


    @Override
    public void mOscPlotOnFling(float velocityX, float velocityY) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot fling X : " + velocityX + " Y : " + velocityY);
    }

    @Override
    public void mOscPlotOnScaleBegin() {

    }

    @Override
    public void mOscPlotOnScaleX(float X) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot rescale X : " + X);

        // We are going to change the time range
        // The rescale will be done in the middle
        double rescalePoint = (this.mGraphTimeValue[0] + this.mGraphTimeValue[1]) / 2.0;

        // we calculate from the middle to the 2 limits delta
        double deltaPRescaleLeft = Math.abs(rescalePoint - this.mGraphTimeValue[0]);
        double deltaPRescaleRight = Math.abs(rescalePoint - this.mGraphTimeValue[1]);


        // Do the rescale
        deltaPRescaleLeft *= 1. / X;
        deltaPRescaleRight *= 1. / X;

        // Calculate the new limits
        this.mGraphTimeValue[0] = rescalePoint - deltaPRescaleLeft;
        this.mGraphTimeValue[1] = rescalePoint + deltaPRescaleRight;

        double ndivision = 9;
        double distance = getTimeRangeDelta() / ndivision;

        //Check the time units
//        switch(this.mGraphTimeUnit)
//        {
//            case S:
//                if(distance <= 0.1)
//                {
//                    this.mGraphTimeUnit = TimeUnits.MS;
//                    this.mGraphTimeValue[0] *= 1000.0;
//                    this.mGraphTimeValue[1] *= 1000.0;
//                }
//                break;
//            case MS:
//                if(distance >= 1000.0)
//                {
//                    this.mGraphTimeUnit = TimeUnits.S;
//                    this.mGraphTimeValue[0] /= 1000.0;
//                    this.mGraphTimeValue[1] /= 1000.0;
//                }
//                else if(distance <= 0.1)
//                {
//                    this.mGraphTimeUnit = TimeUnits.US;
//                    this.mGraphTimeValue[0] *= 1000.0;
//                    this.mGraphTimeValue[1] *= 1000.0;
//                }
//                break;
//            case US:
//                if(distance >= 1000.0)
//                {
//                    this.mGraphTimeUnit = TimeUnits.MS;
//                    this.mGraphTimeValue[0] /= 1000.0;
//                    this.mGraphTimeValue[1] /= 1000.0;
//                }
//                else if(distance <= 0.1)
//                {
//                    this.mGraphTimeUnit = TimeUnits.NS;
//                }
//                break;
//            case NS:
//                if(distance >= 1000.0)
//                {
//                    this.mGraphTimeUnit = TimeUnits.US;
//                }
//                break;
//        }

        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);
        this.mAppFragmentView.updateOscilloscopeTimeUnits(this.mGraphTimeUnit);
    }

    @Override
    public void mOscPlotOnScaleY(float Y) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot rescale Y : " + Y);
    }

    @Override
    public void mOscPlotOnScaleEnd() {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Scale end");
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
    }

    @Override
    public void buttonPressed(View view) {
        view.setBackgroundColor(this.mContext.getResources().getColor(R.color.button_background_pressed));
    }

    @Override
    public void buttonReleased(View view) {
        view.setBackgroundColor(this.mContext.getResources().getColor(R.color.button_background_unpressed));
    }


    @Override
    public void onNewValues(Number[][][] newValuesArray) {
        this.mAppFragmentView.updateGraphValues(newValuesArray);
    }


    /**
     * METHODES PRIVATE
     */


    private double getTimeRangeDelta() {
        return this.mGraphTimeValue[1] - this.mGraphTimeValue[0];
    }


    private void resetValues() {
        this.mChannelsOffset[0] = 0.0;
        this.mChannelsOffset[1] = 0.0;

        //Time domain range
        this.mGraphTimeValue[0] = 0.0;
        this.mGraphTimeValue[1] = 131.0;


        // Trigger configuration
        this.mTriggerDelay = 0.0;
        this.mTriggerLevel = 0.0;

        // Update the view
        this.mAppFragmentView.updateOscMode(this.mMode);
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);

        //Update the model
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL1, ChannelGain.HV);
        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL2, ChannelGain.HV);
        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
    }
}
