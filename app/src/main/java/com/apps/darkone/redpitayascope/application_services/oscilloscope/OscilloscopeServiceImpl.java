package com.apps.darkone.redpitayascope.application_services.oscilloscope;

import android.util.Log;

import com.apps.darkone.redpitayascope.application_services.AppServiceBase;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelGain;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOnChannelsValueListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ProbeAttenuation;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;
import com.apps.darkone.redpitayascope.communication.CommunicationServiceFactory;
import com.apps.darkone.redpitayascope.communication.commSAP.ICommunicationService;
import com.apps.darkone.redpitayascope.communication.commSAP.IOnDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by DarkOne on 19.10.15.
 */
public class OscilloscopeServiceImpl extends AppServiceBase implements IOnDataListener, IOscilloscopeApp {


    private static final String XMIN = "xmin";
    private static final String XMAX = "xmax";
    private static final String TRIG_MODE = "trig_mode";
    private static final String TRIG_SOURCE = "trig_source";
    private static final String TRIG_EDGE = "trig_edge";
    private static final String TRIG_DELAY = "trig_delay";
    private static final String TRIG_LEVEL = "trig_level";
    private static final String SINGLE_BTN = "single_btn";
    private static final String TIME_RANGE = "time_range";
    private static final String TIME_UNITS = "time_units";
    private static final String EN_AVRG = "en_avg_at_dec";
    private static final String AUTO_FLAG = "auto_flag";
    private static final String YMIN = "min_y";
    private static final String YMAX = "max_y";
    private static final String FORCEX_FLAG = "forcex_flag";
    private static final String MEAS_MIN_CH1 = "meas_min_ch1";
    private static final String MEAS_MAX_CH1 = "meas_max_ch1";
    private static final String MEAS_AMP_CH1 = "meas_amp_ch1";
    private static final String MEAS_AVG_CH1 = "meas_avg_ch1";
    private static final String MEAS_FREQ_CH1 = "meas_freq_ch1";
    private static final String MEAS_PERIOD_CH1 = "meas_per_ch1";
    private static final String MEAS_MIN_CH2 = "meas_min_ch2";
    private static final String MEAS_MAX_CH2 = "meas_max_ch2";
    private static final String MEAS_AMP_CH2 = "meas_amp_ch2";
    private static final String MEAS_AVG_CH2 = "meas_avg_ch2";
    private static final String MEAS_FREQ_CH2 = "meas_freq_ch2";
    private static final String MEAS_PERIOD_CH2 = "meas_per_ch2";
    private static final String PRB_ATT_CH1 = "prb_att_ch1";
    private static final String GAIN_CH1 = "gain_ch1";
    private static final String PRB_ATT_CH2 = "prb_att_ch2";
    private static final String GAIN_CH2 = "gain_ch2";
    private static final String GUI_RESET_Y_RANGE = "gui_reset_y_range";
    private static final String GEN_DC_OFF_1 = "gen_DC_offs_1";
    private static final String GEN_DC_OFF_2 = "gen_DC_offs_2";
    private static final String GUI_XMIN = "gui_xmin";
    private static final String GUI_XMAX = "gui_xmax";
    private static final String MIN_Y_NORM = "min_y_norm";
    private static final String MAX_Y_NORM = "max_y_norm";
    private static final String GEN_DC_NORM_1 = "gen_DC_norm_1";
    private static final String GEN_DC_NORM_2 = "gen_DC_norm_2";
    private static final String SCALE_CH1 = "scale_ch1";
    private static final String SCALE_CH2 = "scale_ch2";
    private static final String GEN_TRIG_MODE_CH1 = "gen_trig_mod_ch1";
    private static final String GEN_SIG_TYPE_CH1 = "gen_sig_type_ch1";
    private static final String GEN_ENABLE_CH1 = "gen_enable_ch1";
    private static final String GEN_SINGLE_CH1 = "gen_single_ch1";
    private static final String GEN_SIG_AMP_CH1 = "gen_sig_amp_ch1";
    private static final String GEN_SIG_FREQ_CH1 = "gen_sig_freq_ch1";
    private static final String GEN_SIG_DCOFF_CH1 = "gen_sig_dcoff_ch1";
    private static final String GEN_TRIG_MODE_CH2 = "gen_trig_mod_ch2";
    private static final String GEN_SIG_TYPE_CH2 = "gen_sig_type_ch2";
    private static final String GEN_ENABLE_CH2 = "gen_enable_ch2";
    private static final String GEN_SINGLE_CH2 = "gen_single_ch2";
    private static final String GEN_SIG_AMP_CH2 = "gen_sig_amp_ch2";
    private static final String GEN_SIG_FREQ_CH2 = "gen_sig_freq_ch2";
    private static final String GEN_SIG_DCOFF_CH2 = "gen_sig_dcoff_ch2";

    private static final String GEN_AWG_REFRESH = "gen_awg_refresh";
    private static final String PID_11_ENABLE = "pid_11_enable";
    private static final String PID_11_REST = "pid_11_rst";
    private static final String PID_11_SP = "pid_11_sp";
    private static final String PID_11_KP = "pid_11_kp";
    private static final String PID_11_KI = "pid_11_ki";
    private static final String PID_11_KD = "pid_11_kd";

    private static final String PID_12_ENABLE = "pid_12_enable";
    private static final String PID_12_REST = "pid_12_rst";
    private static final String PID_12_SP = "pid_12_sp";
    private static final String PID_12_KP = "pid_12_kp";
    private static final String PID_12_KI = "pid_12_ki";
    private static final String PID_12_KD = "pid_12_kd";

    private static final String PID_21_ENABLE = "pid_21_enable";
    private static final String PID_21_REST = "pid_21_rst";
    private static final String PID_21_SP = "pid_21_sp";
    private static final String PID_21_KP = "pid_21_kp";
    private static final String PID_21_KI = "pid_21_ki";
    private static final String PID_21_KD = "pid_21_kd";

    private static final String PID_22_ENABLE = "pid_22_enable";
    private static final String PID_22_REST = "pid_22_rst";
    private static final String PID_22_SP = "pid_22_sp";
    private static final String PID_22_KP = "pid_22_kp";
    private static final String PID_22_KI = "pid_22_ki";
    private static final String PID_22_KD = "pid_22_kd";


    private static final String APP_SERVICE_NAME = "scope+gen";
    private ICommunicationService mCommunicationService;
    private List<IOnChannelsValueListener> mOnChannelsValueListenersList;

    private double channelsOffset[];


    public OscilloscopeServiceImpl() {

        super(APP_SERVICE_NAME);

        //Create the list
        mOnChannelsValueListenersList = new ArrayList<IOnChannelsValueListener>();

        channelsOffset = new double[2];

        for (int i = 0; i < channelsOffset.length; i++) {
            channelsOffset[0] = 0.0;
        }

        // Create the parameters

        if (!mParameterManager.isParamsAlreadyPresent(APP_SERVICE_NAME)) {
            mParameterManager.addNewAppsParams(APP_SERVICE_NAME);
        }
        mParameterManager.addParameter(APP_SERVICE_NAME, XMIN, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, XMAX, 100.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, TRIG_MODE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, TRIG_SOURCE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, TRIG_EDGE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, TRIG_DELAY, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, TRIG_LEVEL, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, SINGLE_BTN, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, TIME_RANGE, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, TIME_UNITS, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, EN_AVRG, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, AUTO_FLAG, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, YMIN, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, YMAX, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, FORCEX_FLAG, 0.0, false);


        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_MIN_CH1, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_MAX_CH1, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_AMP_CH1, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_AVG_CH1, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_FREQ_CH1, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_PERIOD_CH1, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_MIN_CH2, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_MAX_CH2, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_AMP_CH2, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_AVG_CH2, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_FREQ_CH2, 0.0, true);
        mParameterManager.addParameter(APP_SERVICE_NAME, MEAS_PERIOD_CH2, 0.0, true);

        mParameterManager.addParameter(APP_SERVICE_NAME, PRB_ATT_CH1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GAIN_CH1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PRB_ATT_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GAIN_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GUI_RESET_Y_RANGE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_DC_OFF_1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_DC_OFF_2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GUI_XMIN, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GUI_XMAX, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, MIN_Y_NORM, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, MAX_Y_NORM, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_DC_NORM_1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_DC_NORM_2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, SCALE_CH1, 1.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, SCALE_CH2, 1.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_TRIG_MODE_CH1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_TYPE_CH1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_ENABLE_CH1, 1.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SINGLE_CH1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_AMP_CH1, 2.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_FREQ_CH1, 20000.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_DCOFF_CH1, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_TRIG_MODE_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_TYPE_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_ENABLE_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SINGLE_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_AMP_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_FREQ_CH2, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_SIG_DCOFF_CH2, 0.0, false);

        mParameterManager.addParameter(APP_SERVICE_NAME, GEN_AWG_REFRESH, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_11_ENABLE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_11_REST, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_11_SP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_11_KP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_11_KI, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_11_KD, 0.0, false);

        mParameterManager.addParameter(APP_SERVICE_NAME, PID_12_ENABLE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_12_REST, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_12_SP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_12_KP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_12_KI, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_12_KD, 0.0, false);

        mParameterManager.addParameter(APP_SERVICE_NAME, PID_21_ENABLE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_21_REST, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_21_SP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_21_KP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_21_KI, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_21_KD, 0.0, false);

        mParameterManager.addParameter(APP_SERVICE_NAME, PID_22_ENABLE, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_22_REST, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_22_SP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_22_KP, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_22_KI, 0.0, false);
        mParameterManager.addParameter(APP_SERVICE_NAME, PID_22_KD, 0.0, false);

        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();


        // Add the useful listeners
        mCommunicationService.addOnDataListener(this);
        mCommunicationService.addOnParamListener(this);

    }


    /**
     * Oscilloscope Methods
     */

    @Override
    public void setTimeLimits(Double xmin, Double xmax) {

        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {


            mParameterManager.changeParamForAll(APP_SERVICE_NAME, XMAX, xmax);
            mParameterManager.changeParamForAll(APP_SERVICE_NAME, XMIN, xmin);
            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }
    }


    @Override
    public double[] getTimeLimits() {

        double[] timeLim = new double[2];

        timeLim[0] = (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, XMIN).getParamValue();
        timeLim[1] = (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, XMAX).getParamValue();

        return timeLim;
    }

    @Override
    public TimeUnits getTimeUnits() {

        Double timeUnits = (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, TIME_UNITS).getParamValue();

        switch (timeUnits.intValue()) {
            case 0:
               return TimeUnits.US ;
            case 1:
                return TimeUnits.MS ;
            case 2:
                return TimeUnits.S ;
            default:
        }

        return TimeUnits.US;
    }

    @Override
    public void setTriggerLevel(double triggerLevel) {
        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {
            mParameterManager.changeParamForAll(APP_SERVICE_NAME, TRIG_LEVEL, triggerLevel);
            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }
    }

    @Override
    public void setTriggerEdge(TriggerEdge triggerEdge) {

        int triggerEdgeVal = (triggerEdge == TriggerEdge.RISING) ? 0 : 1;

        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {
            mParameterManager.changeParamForAll(APP_SERVICE_NAME, TRIG_EDGE, (double) triggerEdgeVal);
            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }
    }

    @Override
    public void setTriggerChannel(ChannelEnum channel) {

        int triggerChannel = (channel == ChannelEnum.CHANNEL1) ? 0 : 1;

        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {
            mParameterManager.changeParamForAll(APP_SERVICE_NAME, TRIG_SOURCE, (double) triggerChannel);
            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }
    }

    @Override
    public void setMode(OscilloscopeMode mode) {

        int trigMode = 0;

        switch (mode) {
            case AUTO:
                trigMode = 0;
                break;
            case NORMAL:
                trigMode = 1;
                break;
            case SINGLE_SHOT:
                trigMode = 0;
            default:
        }

        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {

            mParameterManager.changeParamForAll(APP_SERVICE_NAME, TRIG_MODE, (double) trigMode);
            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }
    }

    @Override
    public void setTimeUnits(TimeUnits timeUnits) {

        int timeUnitParam = 0;

        switch (timeUnits) {
            case NS:
            case US:
                timeUnitParam = 0;
                break;
            case MS:
                timeUnitParam = 1;
                break;
            case S:
                timeUnitParam = 2;
                break;
            default:
        }

        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {
            mParameterManager.changeParamForAll(APP_SERVICE_NAME, TIME_UNITS, (double) timeUnitParam);
            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }

    }



    @Override
    public void setAvergagingState(boolean avrgState) {

        int averagingStateVal = (avrgState) ? 1 : 0;

        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {
            mParameterManager.changeParamForAll(APP_SERVICE_NAME, EN_AVRG, (double) averagingStateVal);
            mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }
    }

    @Override
    public void setChannelOffset(ChannelEnum channel, double offset) {

        switch (channel) {
            case CHANNEL1:
                channelsOffset[0] = offset;
                break;
            case CHANNEL2:
                channelsOffset[1] = offset;
                break;
            default:
        }


    }

    @Override
    public void setChannelProbeAtt(ChannelEnum channel, ProbeAttenuation attenuation) {

        int channelProbeAtt = (attenuation == ProbeAttenuation.PROBE_ATT_1X) ? 0 : 1;


        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {

            switch (channel) {
                case CHANNEL1:
                    mParameterManager.changeParamForAll(APP_SERVICE_NAME, PRB_ATT_CH1, (double) channelProbeAtt);
                    break;
                case CHANNEL2:
                    mParameterManager.changeParamForAll(APP_SERVICE_NAME, PRB_ATT_CH2, (double) channelProbeAtt);
                    break;
                default:
            }

            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }


    }

    @Override
    public void setChannelGain(ChannelEnum channel, ChannelGain gain) {

        int channelGain = (gain == ChannelGain.LV) ? 0 : 1;


        // Get the communication service instance
        mCommunicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        // Create and post the request
        JSONObject requestParams = null;

        try {

            switch (channel) {
                case CHANNEL1:
                    mParameterManager.changeParamForAll(APP_SERVICE_NAME, GAIN_CH1, (double) channelGain);
                    break;
                case CHANNEL2:
                    mParameterManager.changeParamForAll(APP_SERVICE_NAME, GAIN_CH2, (double) channelGain);
                    break;
                default:
            }

            requestParams = mParameterManager.getJsonObject(APP_SERVICE_NAME);
            postParamSafe(requestParams);

        } catch (JSONException e) {
            Log.e(APP_SERVICE_NAME, "Asych. parameters post error : " + e.toString());
        }


    }

    @Override
    public void setChannelDivisionVoltage(ChannelEnum channel, double divisionVoltage) {
        switch (channel) {
            case CHANNEL1:
                break;
            case CHANNEL2:
                break;
            default:
        }
    }


    @Override
    public void addAppValuesListener(IOnChannelsValueListener onChannelsValueListener) {
        mOnChannelsValueListenersList.add(onChannelsValueListener);
    }

    @Override
    public void removeAppValuesListener(IOnChannelsValueListener onChannelsValueListener) {
        mOnChannelsValueListenersList.remove(onChannelsValueListener);
    }

    @Override
    public double getChannelMeanValue(ChannelEnum channel) {

        switch (channel) {
            case CHANNEL1:
                return (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, MEAS_AVG_CH1).getParamValue();
            case CHANNEL2:
                return (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, MEAS_AVG_CH2).getParamValue();
            default:
                return 0.0;
        }
    }

    @Override
    public double getChannelAmplitude(ChannelEnum channel) {
        switch (channel) {
            case CHANNEL1:
                return (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, MEAS_AMP_CH1).getParamValue();
            case CHANNEL2:
                return (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, MEAS_AMP_CH2).getParamValue();
            default:
                return 0.0;
        }
    }

    @Override
    public double getChannelFreq(ChannelEnum channel) {
        switch (channel) {
            case CHANNEL1:
                return (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, MEAS_FREQ_CH1).getParamValue();
            case CHANNEL2:
                return (Double) mParameterManager.getSingleParameter(APP_SERVICE_NAME, MEAS_FREQ_CH2).getParamValue();
            default:
                return 0.0;
        }
    }

    @Override
    public void newDataAvailable(String appName, List<Map<Number, Number>> newData) {

        if (isAppConcerned(appName)) {
            int channel = 0;
            Number[][][] channelsVals = new Number[2][][];

            Iterator<Map<Number, Number>> it = newData.iterator();


            while (it.hasNext()) {
                Map<Number, Number> points = it.next();

                Set<Number> setEntry = points.keySet();
                int nbPoints = points.keySet().size();
                int i = 0;
                channelsVals[channel] = new Number[2][nbPoints];


                Number iValWithOffset;

                for (Number entry : setEntry) {

                    iValWithOffset = channelsOffset[channel] + points.get(entry).doubleValue();

                    channelsVals[channel][0][i] = entry;
                    channelsVals[channel][1][i] = iValWithOffset;
                    i++;
                }
                channel++;
            }

            for (IOnChannelsValueListener listener : mOnChannelsValueListenersList) {

                listener.onNewValues(channelsVals);
            }
        }
    }
}
