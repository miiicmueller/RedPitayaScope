package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

import android.content.Context;
import android.util.Log;
import android.widget.OverScroller;

import com.apps.darkone.redpitayascope.app_fragments.oscilloscope.IAppFragmentView;
import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.app_service_sap.IOnServiceListener;
import com.apps.darkone.redpitayascope.app_service_sap.ServiceStatus;
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
    private double[] mChannelVoltPerDiv;
    private double[] mGraphTimeValue;

    private double mTriggerDelay;
    private double mTriggerLevel;
    private boolean mTriggerSelected;

    private ChannelEnum mTriggerChannel;
    private TriggerEdge mTriggerEdge;


    private TimeUnits mGraphTimeUnit;
    private boolean onTouchPan;
    private boolean mIsOnScroll;
    private boolean mIsOnFling;
    private boolean mIsFlingAxisUpdated;
    private OverScroller mScroller;


    private static final int CHANNEL_COUNT = 2;
    public static final int DIVISION_COUNT = 9;
    public static final double VOLTAGE_MAX = 5.0;
    public static final double VOLTAGE_MIN = -5.0;
    public static final double VOL_PER_DIV_INIT = 1.0;

    private static final String OSC_VIEW_CONTROLLER_TAG = "OSC_VIEW_CTRL";


    public OscilloscopeFragmentControllerApp(IAppFragmentView appFragmentView, Context context) {

        this.mAppFragmentView = appFragmentView;
        this.mContext = context;
        this.mAppServiceManager = AppServiceFactory.getAppServiceManager(this.mContext);


        // Scroller instantiation
        this.mScroller = new OverScroller(this.mContext);

        // Touch attributes
        this.onTouchPan = false;
        this.mTriggerSelected = false;
        this.mIsOnScroll = false;
        this.mIsOnFling = false;
        this.mIsFlingAxisUpdated = true;

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

        //Channel volt per div
        this.mChannelVoltPerDiv = new double[CHANNEL_COUNT];
        this.mChannelVoltPerDiv[0] = VOL_PER_DIV_INIT;
        this.mChannelVoltPerDiv[1] = VOL_PER_DIV_INIT;


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

                    mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1], mOscilloscopeApp.getTimeUnits());
                    mAppFragmentView.updateOscilloscopeTimeUnits(mOscilloscopeApp.getTimeUnits());
                }
            }
        });

        //Check if service is UP
        if (((AppServiceBase) this.mOscilloscopeApp).getAppServiceStatus() == ServiceStatus.UP) {
            Log.d(OSC_VIEW_CONTROLLER_TAG, "Service is already up!");
            initializeModel();
        }

    }


    private void initializeView() {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "initializeView...");

        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel, this.mTriggerSelected);
        ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0], this.mChannelsOffset[0], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1),
                this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1), this.mChannelVoltPerDiv[0]);
        ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1], this.mChannelsOffset[1], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2),
                this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2), this.mChannelVoltPerDiv[1]);
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channel1Info);
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channel2Info);
        this.mAppFragmentView.updateTriggerInfo(trigInfo);

        this.mAppFragmentView.updateOscMode(this.mMode);
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1], mOscilloscopeApp.getTimeUnits());
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

        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);

        this.mOscilloscopeApp.setMode(this.mMode);

        this.mOscilloscopeApp.setTriggerChannel(this.mSelectedChannel);
        this.mOscilloscopeApp.setTriggerLevel(this.mTriggerLevel / this.mOscilloscopeApp.getChannelScale(this.mSelectedChannel));
    }


    @Override
    public void startController() {

        Log.d(OSC_VIEW_CONTROLLER_TAG, "Controller start....");


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
        ((AppServiceBase) this.mOscilloscopeApp).removeOnServiceListener();

        Log.d(OSC_VIEW_CONTROLLER_TAG, "Controller stopped!");
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

        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel, this.mTriggerSelected);
        this.mAppFragmentView.updateTriggerInfo(trigInfo);
        this.mOscilloscopeApp.setTriggerChannel(this.mTriggerChannel);
    }

    @Override
    public void butTrigSettingsOnSingleTapConfirmed() {
//        //Change the trigger edge
//        switch (this.mTriggerEdge) {
//            case RISING:
//                this.mTriggerEdge = TriggerEdge.FALLING;
//                break;
//            case FALLING:
//                this.mTriggerEdge = TriggerEdge.RISING;
//                break;
//        }

        // If trigger is selected, then we deselect it
        this.mTriggerSelected = !this.mTriggerSelected;

        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel,
                this.mTriggerSelected);
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

        //We automatically select the channel, if not selected
        if (this.mSelectedChannel != ChannelEnum.CHANNEL1) {
            selectEnableChannel(ChannelEnum.CHANNEL1);
        }
        // Show/Hide the custom channel menu
        showCustomMenu(ChannelEnum.CHANNEL1);
    }

    @Override
    public void butC1SettingsOnSingleTapConfirmed() {
        selectEnableChannel(ChannelEnum.CHANNEL1);
        // If we change the channel, we hide the menu
        showCustomMenu(ChannelEnum.NONE);

    }

    private void selectEnableChannel(ChannelEnum channel) {

        // If the trigger was selected, we deselect if
        if (this.mTriggerSelected) {
            this.mTriggerSelected = false;

            TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel, this.mTriggerSelected);
            this.mAppFragmentView.updateTriggerInfo(trigInfo);

        }
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

        //We automatically select the channel, if not selected
        if (this.mSelectedChannel != ChannelEnum.CHANNEL2) {
            selectEnableChannel(ChannelEnum.CHANNEL2);
        }

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

        // If we change the channel, we hide the menu
        showCustomMenu(ChannelEnum.NONE);
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

            double scrollAngle = getMotionAngleFromVector2(distanceX, distanceY);

            // Lock the scroll
            mIsOnScroll = true;


            // Change the value due to the gest
            if (scrollAngle > 80.0 && scrollAngle < 110.0) {
                // We have an horizontal scroll


                // Change trigger delay
                double res = getScaleResTimePerPixel();

                this.mTriggerDelay += res * distanceX;
                this.mGraphTimeValue[0] += res * distanceX;
                this.mGraphTimeValue[1] += res * distanceX;

                this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1], mOscilloscopeApp.getTimeUnits());

            } else if (scrollAngle < 20.0) {
                // We have an vertical scroll

                //Check if we work on the trigger
                if (this.mTriggerSelected) {
                    this.mTriggerLevel += (distanceY / 110.0);

                    TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel, this.mTriggerSelected);
                    this.mAppFragmentView.updateTriggerValue(trigInfo);
                    this.mAppFragmentView.updateTriggerInfo(trigInfo);
                } else {
                    //Choose what channel to change offset. We use - because of inverted axis system
                    switch (this.mSelectedChannel) {
                        case CHANNEL1:
                            this.mChannelsOffset[0] += (distanceY / 110.0);
                            this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);

                            ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0],this.mChannelsOffset[0] * this.mChannelVoltPerDiv[0], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1),
                                    this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1), this.mChannelVoltPerDiv[0]);
                            this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
                            this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channel1Info);

                            break;
                        case CHANNEL2:
                            this.mChannelsOffset[1] += (distanceY / 110.0);
                            this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
                            this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);

                            ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1],this.mChannelsOffset[1] * this.mChannelVoltPerDiv[1], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2),
                                    this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2), this.mChannelVoltPerDiv[1]);
                            this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
                            this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channel2Info);
                            break;
                        default:
                            break;
                    }

                    TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel, this.mTriggerSelected);
                    this.mAppFragmentView.updateTriggerValue(trigInfo);
                }
            }
        }
    }

    private double getMotionAngleFromVector2(float distanceX, float distanceY) {
        // Be careful that we never be over 1 for the cosinus
        return Math.acos(Math.min(Math.abs(distanceY) / Math.sqrt(distanceX * distanceX + distanceY * distanceY), 1.0)) * (180.0 / Math.PI);
    }

    @Override
    public void mOscPlotOnScrollEnd() {

        if (mIsOnScroll) {
            mIsOnScroll = false;
            // Update the redpitaya board
            this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
            this.mOscilloscopeApp.setTriggerLevel(this.mTriggerLevel / this.mOscilloscopeApp.getChannelScale(this.mSelectedChannel));
        }


        mIsOnFling = false;
    }


    @Override
    public void mOscPlotOnFling(float velocityX, float velocityY) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot flingX X : " + velocityX + " Y : " + velocityY);

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

        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1], mOscilloscopeApp.getTimeUnits());
    }

    @Override
    public void mOscPlotOnScaleY(float Y) {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Plot rescale Y : " + Y);


        switch (mSelectedChannel) {
            case CHANNEL1:
                this.mChannelVoltPerDiv[0] *= (double) 1 / Y;
                this.mOscilloscopeApp.setChannelDivisionVoltageGain(ChannelEnum.CHANNEL1, 1 / this.mChannelVoltPerDiv[0]);
                break;
            case CHANNEL2:
                this.mChannelVoltPerDiv[1] *= (double) 1 / Y;
                this.mOscilloscopeApp.setChannelDivisionVoltageGain(ChannelEnum.CHANNEL2, 1 / this.mChannelVoltPerDiv[1]);
                break;
            case NONE:
                break;
        }
    }

    @Override
    public void mOscPlotOnScaleEnd() {
        Log.d(OSC_VIEW_CONTROLLER_TAG, "Scale end");
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
        this.onTouchPan = false;
    }

    private double getScaleResTimePerPixel() {
        return Math.abs((this.mGraphTimeValue[0] - this.mGraphTimeValue[1])) / this.mContext.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void onCustomMenuHidden() {

        //Check if no menu is showed
        if (this.customMenuShowed == ChannelEnum.NONE) {
            this.mAppFragmentView.hideChannelCustomMenuLayout();
        }
    }

    @Override
    public void getChannelInfo(ChannelEnum channel, ChannelInfo channelInfo) {

        switch(channel){
            case CHANNEL1:
                channelInfo.setmOffset(this.mChannelsOffset[0] * this.mChannelVoltPerDiv[0]);
                channelInfo.setmMeanFreq(this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1));
                channelInfo.setmAmplitude(this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1));
                channelInfo.setmVoltagePerDiv(this.mChannelVoltPerDiv[0]);
            break;
            case CHANNEL2:
                channelInfo.setmOffset(this.mChannelsOffset[1] * this.mChannelVoltPerDiv[1]);
                channelInfo.setmMeanFreq(this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2));
                channelInfo.setmAmplitude(this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2));
                channelInfo.setmVoltagePerDiv(this.mChannelVoltPerDiv[1]);
            break;
        }
    }

    @Override
    public void setChannelInfo(ChannelEnum channel, ChannelInfo channelInfo) {
        switch(channel){
            case CHANNEL1:
                this.mChannelsOffset[0] = channelInfo.getOffset() / this.mChannelVoltPerDiv[0];
                this.mChannelVoltPerDiv[0] = channelInfo.getVoltagePerDiv();
                this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channelInfo);
                this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, channelInfo.getOffset());
                break;
            case CHANNEL2:
                this.mChannelsOffset[1] = channelInfo.getOffset() / this.mChannelVoltPerDiv[1];
                this.mChannelVoltPerDiv[1] = channelInfo.getVoltagePerDiv();
                this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channelInfo);
                this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, channelInfo.getOffset());
                break;
        }
    }


    @Override
    public void onNewValues(Number[][][] newValuesArray) {
        // Update of the channels characteristic
        ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0],this.mChannelsOffset[0] * this.mChannelVoltPerDiv[0],
                this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1),
                this.mChannelVoltPerDiv[0]);
        ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1],this.mChannelsOffset[1] * this.mChannelVoltPerDiv[1],
                this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2), this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2),
                this.mChannelVoltPerDiv[1]);
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL1, channel1Info);
        this.mAppFragmentView.updateChannelInfo(ChannelEnum.CHANNEL2, channel2Info);
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

        // Touch attributes
        this.onTouchPan = false;

        // Update the view
        this.mAppFragmentView.updateOscMode(this.mMode);
        this.mAppFragmentView.updateSelectedChannel(this.mSelectedChannel);
        this.mAppFragmentView.updateEnabledChannels(this.mEnabledChannels);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mAppFragmentView.updateChannelsOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
        this.mAppFragmentView.updateTimeRange(mGraphTimeValue[0], mGraphTimeValue[1], mOscilloscopeApp.getTimeUnits());

        //Update the model
        this.mOscilloscopeApp.setTimeLimits(this.mGraphTimeValue[0], this.mGraphTimeValue[1]);
        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL1, ChannelGain.HV);
        this.mOscilloscopeApp.setChannelGain(ChannelEnum.CHANNEL2, ChannelGain.HV);
        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL1, this.mChannelsOffset[0]);
        this.mOscilloscopeApp.setChannelOffset(ChannelEnum.CHANNEL2, this.mChannelsOffset[1]);
        this.mOscilloscopeApp.setTriggerChannel(this.mSelectedChannel);
        this.mOscilloscopeApp.setTriggerEdge(this.mTriggerEdge);
        this.mOscilloscopeApp.setTriggerLevel(this.mTriggerLevel / this.mOscilloscopeApp.getChannelScale(this.mSelectedChannel));


        ChannelInfo channel1Info = new ChannelInfo(this.mChannelsOffset[0],this.mChannelsOffset[0], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL1),
                this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL1), this.mChannelVoltPerDiv[0]);
        ChannelInfo channel2Info = new ChannelInfo(this.mChannelsOffset[1],this.mChannelsOffset[1], this.mOscilloscopeApp.getChannelFreq(ChannelEnum.CHANNEL2),
                this.mOscilloscopeApp.getChannelAmplitude(ChannelEnum.CHANNEL2), this.mChannelVoltPerDiv[1]);
        TriggerInfo trigInfo = new TriggerInfo(this.mTriggerLevel, this.mTriggerEdge, this.mTriggerChannel, this.mTriggerSelected);

        this.mAppFragmentView.updateTriggerInfo(trigInfo);
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
