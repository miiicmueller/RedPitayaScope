package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

import android.content.Context;
import android.util.Log;

import com.apps.darkone.redpitayascope.app_fragments.oscilloscope.IAppFragmentView;
import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.app_service_sap.IOnServiceListener;
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
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

import java.util.Vector;

/**
 * Created by DarkOne on 25.11.15.
 */
public class OscilloscopeFragmentControllerApp implements ITouchAppViewController, IOnChannelsValueListener, IOnServiceListener {


    private IAppFragmentView mAppFragmentView;
    private IOscilloscopeApp mOscilloscopeApp;
    private AppServiceManager mAppServiceManager;
    private Context mContext;
    private OscilloscopeMode mMode;
    private Vector<ChannelEnum> mEnabledChannels;
    private ChannelEnum mSelectedChannel;
    private ChannelEnum customMenuShowed;

    private double[] mChannelsOffset;
    private double[] mGraphTimeValue;
    private double mTriggerDelay;
    private double mTriggerLevel;

    private ChannelEnum mTriggerChannel;
    private TriggerEdge mTriggerEdge;


    private TimeUnits mGraphTimeUnit;
    private boolean onTouchPan;

    private static final int CHANNEL_COUNT = 2;
    public static final int DIVISION_COUNT = 9;
    private static final String OSC_VIEW_CONTROLLER_TAG = "OSC_VIEW_CTRL";


    public OscilloscopeFragmentControllerApp(IAppFragmentView appFragmentView, Context context) {

        this.mAppFragmentView = appFragmentView;
        this.mContext = context;
        this.mAppServiceManager = AppServiceFactory.getAppServiceManager(this.mContext);


        // Touch attributes
        this.onTouchPan = false;

        // Our application is the oscilloscope one
        this.mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();
        ((IAppService) this.mOscilloscopeApp).setOnServiceListener(this);

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
        this.mTriggerChannel = ChannelEnum.CHANNEL1;
        this.mTriggerEdge = TriggerEdge.RISING;


        //Menus
        this.customMenuShowed = ChannelEnum.NONE;

        // Update the view
        this.initializeView();

        ((AppServiceBase) this.mOscilloscopeApp).setOnAppParamsListener(new IOnAppParamsListener() {

            @Override
            public void onParametersUpdated() {

                double[] lim = mOscilloscopeApp.getTimeLimits();

                Log.d(OSC_VIEW_CONTROLLER_TAG, "parameters updated...");

                // We have to check if the range has change
                if ((mGraphTimeValue[0] != lim[0]) || (mGraphTimeValue[1] != lim[1])) {

                    Log.d(OSC_VIEW_CONTROLLER_TAG, "Time limit changed");

                    // retreive the limits
                    mGraphTimeValue[0] = lim[0];
                    mGraphTimeValue[1] = lim[1];

                    mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);
                    mAppFragmentView.updateOscilloscopeTimeUnits(mOscilloscopeApp.getTimeUnits());
                }
            }
        });
    }


    private void initializeView() {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "initializeView...");

        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel);

        ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1));
        ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2));
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channel1Info);
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channel2Info);
        this.mAppFragmentView.updateTriggerInfo(trigInfo);

        this.mAppFragmentView.updateOscMode(this.mMode);
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);
        this.mAppFragmentView.updateOscilloscopeTimeUnits(this.mGraphTimeUnit);
        this.mAppFragmentView.hideChannelCustomMenu(this.customMenuShowed);

    }


    private void initializeModel() {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "initializeModel...");

        //Update the model
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
        this.mOscilloscopeApp.setTimeUnits(this.mGraphTimeUnit);

        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL1, ChannelGain.HV);
        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL2, ChannelGain.HV);
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
        // Our application is the oscilloscope one
        this.mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();
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

        //Change the trigger channel
        switch (this.mTriggerChannel) {
            case CHANNEL1:
                this.mTriggerChannel = ChannelEnum.CHANNEL2;
                break;
            case CHANNEL2:
                this.mTriggerChannel = ChannelEnum.CHANNEL1;
                break;
        }

        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel);
        this.mAppFragmentView.updateTriggerInfo(trigInfo);
    }

    @Override
    public void butTrigSettingsOnSingleTapConfirmed() {
        //Change the trigger edge
        switch (this.mTriggerEdge) {
            case RISING:
                this.mTriggerEdge = TriggerEdge.FALLING;
                break;
            case FALLING:
                this.mTriggerEdge = TriggerEdge.RISING;
                break;
        }

        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel);
        this.mAppFragmentView.updateTriggerInfo(trigInfo);
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

        //We automaticaly select the channel
        selectEnableChannel(ChannelEnum.CHANNEL1);
        // Show/Hide the custom channel menu
        showCustomMenu(ChannelEnum.CHANNEL1);
    }

    @Override
    public void butC1SettingsOnSingleTapConfirmed() {
        selectEnableChannel(ChannelEnum.CHANNEL1);
    }

    private void selectEnableChannel(ChannelEnum channel) {
        // Single tap + not selected + not enabled mean : selected and enabled
        if ((this.mSelectedChannel != channel) && !(this.mEnabledChannels.contains(channel))) {
            this.mEnabledChannels.add(channel);
            this.mSelectedChannel = channel;
        }
        // If selected, then we disable it
        else if ((this.mSelectedChannel == channel) && (this.mEnabledChannels.contains(channel))) {
            this.mEnabledChannels.remove(channel);

            //Check if the channel was selected
            if (this.mSelectedChannel == channel) {
                // we have to switch either to channel 2 (if enabled), or none
                if (this.mEnabledChannels.size() != 0) {
                    this.mSelectedChannel = this.mEnabledChannels.get(0);
                } else {
                    this.mSelectedChannel = ChannelEnum.NONE;
                }
            }
        }
        // If enable, the we only select it
        else if (this.mEnabledChannels.contains(channel)) {
            this.mSelectedChannel = channel;
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
        //We automaticaly select the channel
        selectEnableChannel(ChannelEnum.CHANNEL1);

        showCustomMenu(ChannelEnum.CHANNEL2);
    }

    private void showCustomMenu(ChannelEnum channel) {

        if (this.customMenuShowed == channel) {
            this.mAppFragmentView.hideChannelCustomMenu(channel);
            this.customMenuShowed = ChannelEnum.NONE;
        } else if (this.customMenuShowed != ChannelEnum.NONE) {
            this.mAppFragmentView.hideChannelCustomMenu(this.customMenuShowed);
            this.mAppFragmentView.showChannelCustomMenu(channel);
            this.customMenuShowed = channel;
        } else {
            this.mAppFragmentView.showChannelCustomMenu(channel);
            this.customMenuShowed = channel;
        }

    }

    @Override
    public void butC2SettingsOnSingleTapConfirmed() {
        selectEnableChannel(ChannelEnum.CHANNEL2);
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


        if (!this.onTouchPan) {
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

                // Update the redpitaya board
                this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);

            } else if (scrollAngle < 20.0) {
                // We have an vertical scroll

                //Choose what channel to change offset. We use - because of inverted axis system
                switch (this.mSelectedChannel) {
                    case CHANNEL1:
                        this.mChannelsOffset[0] += (distanceY / 110.0);
                        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);

                        ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1));
                        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
                        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channel1Info);

                        break;
                    case CHANNEL2:
                        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
                        this.mChannelsOffset[1] += (distanceY / 110.0);
                        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);

                        ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2));
                        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
                        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channel2Info);
                        break;
                    default:
                        break;
                }

            }

            Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot scroll X :" + distanceX + " Y :" + distanceY + " norm : " + acos + " angle : " + scrollAngle);
        }
    }


    @Override
    public void mOscPlotOnFling(float velocityX, float velocityY) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot fling X : " + velocityX + " Y : " + velocityY);
    }

    @Override
    public void mOscPlotOnScaleBegin() {
        this.onTouchPan = true;
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

        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1]);
    }

    @Override
    public void mOscPlotOnScaleY(float Y) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot rescale Y : " + Y);
    }

    @Override
    public void mOscPlotOnScaleEnd() {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Scale end");
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
        this.onTouchPan = false;
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


        ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1));
        ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2));
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channel1Info);
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channel2Info);
    }

    @Override
    public void onServiceUp(String appName) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Service up!");
        this.initializeModel();
    }

    @Override
    public void onServiceStopped(String appName) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Service stopped!");
    }

    @Override
    public void onServiceError(String appName) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Service error!");
    }

    @Override
    public void onServiceLoading(String appName) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Service loading....");
    }
}
