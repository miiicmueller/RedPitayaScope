package com.apps.darkone.redpitayascope.app_fragments.oscilloscope;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.apps.darkone.redpitayascope.R;
import com.apps.darkone.redpitayascope.app_controller.oscilloscope.ChannelInfo;
import com.apps.darkone.redpitayascope.app_controller.oscilloscope.ITouchAppViewController;
import com.apps.darkone.redpitayascope.app_controller.oscilloscope.OscilloscopeFragmentControllerApp;
import com.apps.darkone.redpitayascope.app_controller.oscilloscope.TimeInfo;
import com.apps.darkone.redpitayascope.app_controller.oscilloscope.TriggerInfo;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.OscilloscopeTimeValueSerie;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;
import com.apps.darkone.redpitayascope.menu.CustomButtonMenu;
import com.apps.darkone.redpitayascope.menu.oscilloscope.ChannelMenu;

import java.util.Arrays;
import java.util.Vector;


/**
 * Created by DarkOne on 02.11.15.
 */
public class OscilloscopeFragment extends Fragment implements IAppFragmentView {


    private Context mContext;
    private XYPlot mOscPlot;
    private IOscilloscopeApp mOscilloscopeApp;
    private LineAndPointFormatter mChannelFormaterCh1;
    private LineAndPointFormatter mChannelFormaterCh2;
    private String timeUnits;
    private double actualTriggerLevel;


    private OscilloscopeTimeValueSerie mOscilloscopeSerieCh1;
    private OscilloscopeTimeValueSerie mOscilloscopeSerieCh2;
    private boolean mChannel1Enabled;
    private boolean mChannel2Enabled;
    private boolean mChannel1Selected;
    private boolean mChannel2Selected;


    private Redrawer mRedrawer;


    private static final String OSC_SERIE_NAME = "";

    private ActionBar mMainActionBar;
    private Toolbar mMainToolBar;
    private LinearLayout mBottomActionBar;
    boolean mToolBarVisible;


    // boutons
    private TableLayout butOscMode;
    private TableLayout butTrigSettings;
    private TableLayout butTimeSettings;
    private TableLayout butC1Settings;
    private TableLayout butC2Settings;
    private ITouchAppViewController mOscilloscopeFragmentController;

    private GestureDetectorCompat mButOscModeDetector;
    private GestureDetectorCompat mButTrigSettingsDetector;
    private GestureDetectorCompat mButTimeSettingsDetector;
    private GestureDetectorCompat mButC1SettingsDetector;
    private GestureDetectorCompat mButC2SettingsDetector;
    private GestureDetectorCompat mXYPlotDetector;
    private ScaleGestureDetector mXYPlotScaleDetector;
    private FloatingActionButton mSingleShotBtn;
    private boolean singleShotButtonHidden;

    private ChannelMenu mCustomChannel1Menu;
    private ChannelMenu mCustomChannel2Menu;


    public static OscilloscopeFragment newInstance() {
        OscilloscopeFragment settingFragment = new OscilloscopeFragment();
        return settingFragment;
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        mContext = context;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.oscilloscope_fragment_app, container, false);

        rootView.setBackgroundColor(Color.BLACK);


        Log.d("DEBUG_TAG", "onCreateView");

        // Plot utility initialization

        this.timeUnits = "ms";
        this.actualTriggerLevel = 0.0;
        this.mChannel1Enabled = false;
        this.mChannel2Enabled = false;

        //initialize our XYPlot reference:
        mOscPlot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);


        // reduce the number of range labels
        mOscPlot.setBackgroundPaint(null);
        mOscPlot.setBorderPaint(null);
        mOscPlot.setBackground(null);
        mOscPlot.setLayerPaint(null);
        mOscPlot.setRenderMode(Plot.RenderMode.USE_BACKGROUND_THREAD);
        mOscPlot.setTicksPerRangeLabel(3);
        mOscPlot.getGraphWidget().

                setDomainLabelOrientation(-45);

        mOscPlot.setDomainBoundaries(0, BoundaryMode.FIXED, 131, BoundaryMode.FIXED);
        mOscPlot.setRangeBoundaries(-5, BoundaryMode.FIXED, 5, BoundaryMode.FIXED);
        mOscPlot.getGraphWidget().

                setGridBackgroundPaint(null);

        mOscPlot.getGraphWidget().

                setClippingEnabled(true);

        mOscPlot.setDomainLabel(timeUnits);


        mChannelFormaterCh1 = new

                LineAndPointFormatter(Color.rgb(255, 255, 0),

                null, null, null);
        mChannelFormaterCh2 = new

                LineAndPointFormatter(Color.rgb(255, 0, 102),

                null, null, null);


        // Create the serie only one time
        mOscilloscopeSerieCh1 = new

                OscilloscopeTimeValueSerie(OSC_SERIE_NAME);

        mOscPlot.addSeries(mOscilloscopeSerieCh1, mChannelFormaterCh1);

        mOscilloscopeSerieCh2 = new

                OscilloscopeTimeValueSerie(OSC_SERIE_NAME);

        mOscPlot.addSeries(mOscilloscopeSerieCh2, mChannelFormaterCh2);


        mRedrawer = new

                Redrawer(
                Arrays.asList(new Plot[]{
                                mOscPlot
                        }
                ),
                20, false);


        mMainActionBar = (ActionBar) ((AppCompatActivity) getActivity()).getSupportActionBar();
        mMainToolBar = (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.maintoolbar);
        mBottomActionBar = (LinearLayout) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_bottom);


        mToolBarVisible = true;

        LinearLayout layout = (LinearLayout) mBottomActionBar.findViewById(R.id.toolbar_bottom_layer_menu);
        layout.setBackgroundColor(this.mContext.getResources().getColor(R.color.button_background_pressed));
        View osc_toolbar = inflater.inflate(R.layout.osc_control_bar, layout, true);
        osc_toolbar.setBackgroundColor(this.mContext.getResources().getColor(R.color.button_background_pressed));

        // Single shot floating action button
        mSingleShotBtn = (FloatingActionButton) rootView.findViewById(R.id.single_shot_btn);

        mSingleShotBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("DEBUG_TAG", "Button pressed");
                mOscilloscopeFragmentController.onSingleShotButtonPressed();
            }
        });

        //Hide the button
        hideSingleShotButton(mSingleShotBtn);
        mSingleShotBtn.hide();


        mCustomChannel1Menu = new ChannelMenu(this.mContext, this.mBottomActionBar, this.mContext.getResources().getColor(R.color.channel1_color));
        mCustomChannel2Menu = new ChannelMenu(this.mContext, this.mBottomActionBar, this.mContext.getResources().getColor(R.color.channel2_color));


        mCustomChannel1Menu.setIOnCustomMenuStateChange(new CustomButtonMenu.IOnCustomMenuStateChange() {
            @Override
            public void onNewState(boolean isHidden) {
                if (isHidden) {
                    mOscilloscopeFragmentController.onCustomMenuHidden();
                }
            }
        });

        mCustomChannel2Menu.setIOnCustomMenuStateChange(new CustomButtonMenu.IOnCustomMenuStateChange() {
            @Override
            public void onNewState(boolean isHidden) {
                if (isHidden) {
                    mOscilloscopeFragmentController.onCustomMenuHidden();
                }
            }
        });

        mCustomChannel1Menu.setOnChannelMenuListener(new ChannelMenu.IOnChannelMenuListener() {
            @Override
            public void onGainMenuButtonClick() {
                // TODO Implement
            }

            @Override
            public void onProbeAttMenuButtonClick() {
                // TODO Implement
            }

            @Override
            public void onChannelOffsetMenuButtonClick() {
                // Get the channel infos
                final ChannelInfo localChannelInfos = new ChannelInfo();
                mOscilloscopeFragmentController.getChannelInfo(ChannelEnum.CHANNEL1, localChannelInfos);

                // Creation of the popup
                boolean wrapInScrollView = true;
                MaterialDialog view = new MaterialDialog.Builder(getContext())
                        .title("Channel 1: Offset voltage [V]")
                        .customView(R.layout.layout_popup_offset, wrapInScrollView)
                        .positiveText("OK")
                        .build();


                final EditText numberEditText = (EditText) view.getCustomView().findViewById(R.id.editText2);

                view.getBuilder()
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d("DEBUG_TAG", "On PopupMenu OK Event!");

                                ChannelInfo info = new ChannelInfo(localChannelInfos);
                                // read the value number and put it in channelInfo object
                                try {
                                    info.setmOffset(Double.valueOf(numberEditText.getText().toString()));
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "No numerical value has been entered!", 5).show();
                                }
                                ;
                                // Refresh the channel info
                                mOscilloscopeFragmentController.setChannelInfo(ChannelEnum.CHANNEL1, info);
                            }
                        })
                        .show();

                // Display an int in the numberTextview of the actual offset
                ((EditText) view.getCustomView().findViewById(R.id.editText2)).setHint(String.valueOf(localChannelInfos.getOffset()));

            }

            @Override
            public void onChannelScaleMenuClick() {
                // Get the channel infos
                final ChannelInfo localChannelInfos = new ChannelInfo();
                mOscilloscopeFragmentController.getChannelInfo(ChannelEnum.CHANNEL1, localChannelInfos);

                // Creation of the popup
                boolean wrapInScrollView = true;
                MaterialDialog view = new MaterialDialog.Builder(getContext())
                        .title("Channel 1: Scale voltage [V / div]")
                        .customView(R.layout.layout_popup_offset, wrapInScrollView)
                        .positiveText("OK")
                        .build();


                final EditText numberEditText = (EditText) view.getCustomView().findViewById(R.id.editText2);

                view.getBuilder()
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d("DEBUG_TAG", "On PopupMenu OK Event!");

                                ChannelInfo info = new ChannelInfo(localChannelInfos);
                                // read the value number and put it in channelInfo object
                                try {
                                    info.setmVoltagePerDiv(Double.valueOf(numberEditText.getText().toString()));
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "No numerical value has been entered!", 5).show();
                                }
                                ;
                                // Refresh the channel info
                                mOscilloscopeFragmentController.setChannelInfo(ChannelEnum.CHANNEL1, info);
                            }
                        })
                        .show();

                // Display an int in the numberTextview of the actual offset
                ((EditText) view.getCustomView().findViewById(R.id.editText2)).setHint(String.valueOf(localChannelInfos.getVoltagePerDiv()));
            }
        });

        mCustomChannel2Menu.setOnChannelMenuListener(new ChannelMenu.IOnChannelMenuListener() {
            @Override
            public void onGainMenuButtonClick() {
                // TODO Implement
            }

            @Override
            public void onProbeAttMenuButtonClick() {
                // TODO Implement
            }

            @Override
            public void onChannelOffsetMenuButtonClick() {
                // Get the channel infos
                final ChannelInfo localChannelInfos = new ChannelInfo();
                mOscilloscopeFragmentController.getChannelInfo(ChannelEnum.CHANNEL2, localChannelInfos);

                // Creation of the popup
                boolean wrapInScrollView = true;
                MaterialDialog view = new MaterialDialog.Builder(getContext())
                        .title("Channel 2: Offset voltage [V]")
                        .customView(R.layout.layout_popup_offset, wrapInScrollView)
                        .positiveText("OK")
                        .build();


                final EditText numberEditText = (EditText) view.getCustomView().findViewById(R.id.editText2);

                view.getBuilder()
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d("DEBUG_TAG", "On PopupMenu OK Event!");

                                ChannelInfo info = new ChannelInfo(localChannelInfos);
                                // read the value number and put it in channelInfo object
                                try {
                                    info.setmOffset(Double.valueOf(numberEditText.getText().toString()));
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "No numerical value has been entered!", 5).show();
                                }
                                ;
                                // Refresh the channel info
                                mOscilloscopeFragmentController.setChannelInfo(ChannelEnum.CHANNEL2, info);
                            }
                        })
                        .show();

                // Display an int in the numberTextview of the actual offset
                ((EditText) view.getCustomView().findViewById(R.id.editText2)).setHint(String.valueOf(localChannelInfos.getOffset()));
            }

            @Override
            public void onChannelScaleMenuClick() {
                // Get the channel infos
                final ChannelInfo localChannelInfos = new ChannelInfo();
                mOscilloscopeFragmentController.getChannelInfo(ChannelEnum.CHANNEL2, localChannelInfos);

                // Creation of the popup
                boolean wrapInScrollView = true;
                MaterialDialog view = new MaterialDialog.Builder(getContext())
                        .title("Channel 2: Scale voltage [V / div]")
                        .customView(R.layout.layout_popup_offset, wrapInScrollView)
                        .positiveText("OK")
                        .build();


                final EditText numberEditText = (EditText) view.getCustomView().findViewById(R.id.editText2);

                view.getBuilder()
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d("DEBUG_TAG", "On PopupMenu OK Event!");

                                ChannelInfo info = new ChannelInfo(localChannelInfos);
                                // read the value number and put it in channelInfo object
                                try {
                                    info.setmVoltagePerDiv(Double.valueOf(numberEditText.getText().toString()));
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "No numerical value has been entered!", 5).show();
                                }
                                ;
                                // Refresh the channel info
                                mOscilloscopeFragmentController.setChannelInfo(ChannelEnum.CHANNEL2, info);
                            }
                        })
                        .show();

                // Display an int in the numberTextview of the actual offset
                ((EditText) view.getCustomView().findViewById(R.id.editText2)).setHint(String.valueOf(localChannelInfos.getVoltagePerDiv()));
            }
        });


        // ---------------------------------------------------------------------------------------------------
        // Set des gestures sur les boutons et le graphe
        // Technical reference: http://developer.android.com/training/gestures/detector.html
        // ---------------------------------------------------------------------------------------------------

        // Get object reference
        butOscMode = (TableLayout) osc_toolbar.findViewById(R.id.oscMode);

        // Set gesture detector
        mButOscModeDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap OscMode Event!");
                // Callback interface
                mOscilloscopeFragmentController.butOscModeOnDoubleTap();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed OscMode Event!");
                // Callback interface
                mOscilloscopeFragmentController.butOscModeOnSingleTapConfirmed();

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress OscMode Event!");
                // Callback interface
                mOscilloscopeFragmentController.butOscModeOnLongPress();

                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                Log.d("DEBUG_TAG", "On Down OscMode Event!");

                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapUp OscMode Event!");
                // set background color when pressed
                return super.onSingleTapUp(e);
            }

        });
        butOscMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButOscModeDetector.onTouchEvent(event);
                return false;
            }
        });


        // ---------------------
        // Get object reference
        butTrigSettings = (TableLayout) osc_toolbar.findViewById(R.id.trig);
        // Set gesture detector
        mButTrigSettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butTrigSettings Event!");

                // Callback interface
                mOscilloscopeFragmentController.butTrigSettingsOnDoubleTap();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butTrigSettings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butTrigSettingsOnSingleTapConfirmed();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butTrigSettings Event!");




                // Get the channel infos
                final TriggerInfo localTriggerInfos = new TriggerInfo();
                mOscilloscopeFragmentController.getTriggerInfo(localTriggerInfos);

                // Creation of the popup
                boolean wrapInScrollView = true;
                MaterialDialog view = new MaterialDialog.Builder(getContext())
                        .title("Trigger Settings")
                        .customView(R.layout.layout_popup_trigger, wrapInScrollView)
                        .positiveText("OK")
                        .build();
                //.show();


                final EditText numberEditText = (EditText) view.getCustomView().findViewById(R.id.editText4);
                final RadioButton rBut1 = (RadioButton) view.getCustomView().findViewById(R.id.radioButton);
                final RadioButton rBut2 = (RadioButton) view.getCustomView().findViewById(R.id.radioButton2);
                // Display an int in the numberTextview of the actual offset
                numberEditText.setHint(String.valueOf(localTriggerInfos.getTriggerLevel()));
                // Set the radioButton state to it's actual state
                rBut1.setChecked(localTriggerInfos.getTriggerEdge() == TriggerEdge.RISING);
                rBut2.setChecked(localTriggerInfos.getTriggerEdge() == TriggerEdge.FALLING);


                view.getBuilder()
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d("DEBUG_TAG", "On PopupMenu OK Event!");

                                TriggerInfo trInf = new TriggerInfo(localTriggerInfos);
                                // read the value number and put it in triggerInfo object
                                try {
                                    trInf.setmTriggerLevel(Double.valueOf(numberEditText.getText().toString()));
                                    // Refresh the channel info
                                    //mOscilloscopeFragmentController.setTriggerInfo(trInf);
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "No numerical value has been entered!", 5).show();
                                }

                                // read the value radio button selected and affect the state to triggerInfo
                                try {
                                    if(rBut1.isChecked()) {
                                        trInf.setmTriggerEdge(TriggerEdge.RISING);
                                    }
                                    else if(rBut2.isChecked()) {
                                        trInf.setmTriggerEdge(TriggerEdge.FALLING);
                                    }
                                    // Refresh the channel info
                                    //mOscilloscopeFragmentController.setTriggerInfo(trInf);
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "Bad Edge value selected!", 5).show();
                                }
                                mOscilloscopeFragmentController.setTriggerInfo(trInf);

                            }
                        })
                        .show();





                // Callback interface
                mOscilloscopeFragmentController.butTrigSettingsOnLongPress();
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }
        });
        butTrigSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButTrigSettingsDetector.onTouchEvent(event);
                return false;
            }
        });
        // ---------------------
        butTimeSettings = (TableLayout) osc_toolbar.findViewById(R.id.timeBase);
        mButTimeSettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butTimeSettings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butTimeSettingsOnDoubleTap();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butTimeSettings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butTimeSettingsOnSingleTapConfirmed();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butTimeSettings Event!");



                // Get the channel infos
                final TimeInfo localTimeInfo = new TimeInfo();
                mOscilloscopeFragmentController.getTimeInfo(localTimeInfo);

                // Creation of the popup
                boolean wrapInScrollView = true;
                MaterialDialog view = new MaterialDialog.Builder(getContext())
                        .title("Time Settings")
                        .customView(R.layout.layout_popup_time, wrapInScrollView)
                        .positiveText("OK")
                //        .build();
                .show();

                final EditText timeOffsetText = (EditText) view.getCustomView().findViewById(R.id.editText5);
                final EditText timeScaleText = (EditText) view.getCustomView().findViewById(R.id.editText6);

                view.getBuilder()
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d("DEBUG_TAG", "On PopupMenu OK Event!");

                                TimeInfo timeInfoObj = new TimeInfo(localTimeInfo);


                                // read the trig level and assign it to the object
                                try {
                                    // set time offset
                                    timeInfoObj.setGraphTimeValue0(Double.valueOf(timeOffsetText.getText().toString()));
                                    timeInfoObj.setGraphTimeValue1(((localTimeInfo.getGraphTimeValue1() - localTimeInfo.getGraphTimeValue0()) + timeInfoObj.getGraphTimeValue0()));
                                    // Refresh the channel info
                                } catch (Exception e) {
                                   // Toast.makeText(mContext, "No positive numerical value has been entered!", 5).show();
                                }

                                // read the value radio button selected and affect the state to triggerInfo
                                try {
                                    timeInfoObj.setGraphTimeValue1((Double.valueOf(timeScaleText.getText().toString())) * OscilloscopeFragmentControllerApp.DIVISION_COUNT + timeInfoObj.getGraphTimeValue0());
                                    //double timePerDivision = (tMax - tMin) / OscilloscopeFragmentControllerApp.DIVISION_COUNT;
                                    // Refresh the channel info
                                    //mOscilloscopeFragmentController.setTimeInfo(timeInfoObj);
                                } catch (Exception e) {
                                    //Toast.makeText(mContext, "No numerical value has been entered!", 5).show();
                                }
                                mOscilloscopeFragmentController.setTimeInfo(timeInfoObj);

                            }
                        });


                // Callback interface
                mOscilloscopeFragmentController.butTimeSettingsOnLongPress();

                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }
        });
        butTimeSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButTimeSettingsDetector.onTouchEvent(event);
                return false;
            }
        });
        // ---------------------
        butC1Settings = (TableLayout) osc_toolbar.findViewById(R.id.chan1);
        mButC1SettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butC1Settings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butC1SettingsOnDoubleTap();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butC1Settings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butC1SettingsOnSingleTapConfirmed();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butC1Settings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butC1SettingsOnLongPress();
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }
        });
        butC1Settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButC1SettingsDetector.onTouchEvent(event);
                return false;
            }
        });
        // ---------------------
        butC2Settings = (TableLayout) osc_toolbar.findViewById(R.id.chan2);
        mButC2SettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butC2Settings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butC2SettingsOnDoubleTap();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butC2Settings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butC2SettingsOnSingleTapConfirmed();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butC2Settings Event!");
                // Callback interface
                mOscilloscopeFragmentController.butC2SettingsOnLongPress();
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }

        });
        butC2Settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mButC2SettingsDetector.onTouchEvent(event);
                return false;
            }
        });
        // ---------------------
        // mOscPlot = (TableLayout) rootView.findViewById(R.id.chan2); // This line has been done above...
        mXYPlotDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap mOscPlot Event!");
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnDoubleTap();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed mOscPlot Event!");
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnSingleTapConfirmed();


                // Show/Hide the action bar
                if (mToolBarVisible) {
                    hideToolBars();
                } else {
                    showToolBars();


                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress mOscPlot Event!");
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnLongPress();
                super.onLongPress(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("DEBUG_TAG", "On onScroll mOscPlot Event!");
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnScroll(distanceX, distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                Log.d("DEBUG_TAG", "On onFling mOscPlot Event!");
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnFling(velocityX, velocityY);
                return super.onFling(event1, event2, velocityX, velocityY);
            }
        });

        mXYPlotScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnScaleBegin();
                Log.d("DEBUG_TAG", "Scale action begin");
                return super.onScaleBegin(detector);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                if (detector.getCurrentSpanX() > detector.getCurrentSpanY()) {
                    // the scalefactor is applied for X, callback interface
                    Log.d("DEBUG_TAG", "Scale action X");
                    mOscilloscopeFragmentController.mOscPlotOnScaleX(detector.getScaleFactor());
                } else {
                    // the scalefactor is applied for Y, callback interface
                    Log.d("DEBUG_TAG", "Scale action Y");
                    mOscilloscopeFragmentController.mOscPlotOnScaleY(detector.getScaleFactor());
                }
                return super.onScale(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                // Callback interface
                mOscilloscopeFragmentController.mOscPlotOnScaleEnd();
                Log.d("DEBUG_TAG", "Scale action End");
                super.onScaleEnd(detector);
            }
        });
        mOscPlot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mXYPlotDetector.onTouchEvent(event);
                mXYPlotScaleDetector.onTouchEvent(event);

                // Ugly hack to detect scroll end
                if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mOscilloscopeFragmentController.mOscPlotOnScrollEnd();
                }

                return true;
            }
        });

        // ---------------------------------------------------------------------------------------------------
        // END Set des gestures sur les boutons et le graphe
        // ---------------------------------------------------------------------------------------------------


        Log.d("DEBUG_TAG", "onCreateView end");


        showToolBars();

        return rootView;
    }

    private void hideToolBars() {
        mToolBarVisible = false;
        mMainToolBar.animate().translationX(-mMainToolBar.getRight()).setInterpolator(new AccelerateInterpolator((float) 2.0)).start();
        mBottomActionBar.animate().translationX(mBottomActionBar.getRight()).setInterpolator(new AccelerateInterpolator((float) 2.0)).start();
    }


    private void showToolBars() {
        // If the bar was hidden
        if (!mMainActionBar.isShowing()) {
            mMainActionBar.show();
        }

        mToolBarVisible = true;
        mMainToolBar.animate().translationX(0).setInterpolator(new DecelerateInterpolator((float) 2.0)).start();
        mBottomActionBar.animate().translationX(0).setInterpolator(new DecelerateInterpolator((float) 2.0)).start();
    }


    private void hideSingleShotButton(FloatingActionButton btn) {
        btn.animate().translationY(-btn.getBottom()).setInterpolator(new AccelerateInterpolator((float) 2.0)).start();
    }


    private void showSingleShotButton(FloatingActionButton btn) {
        mSingleShotBtn.show();
        btn.animate().translationY((-getResources().getDisplayMetrics().heightPixels / 2.f) + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56 / 2, getResources().getDisplayMetrics())).setInterpolator(new DecelerateInterpolator((float) 2.0)).start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        Log.d("DEBUG_TAG", "OnStart");
        super.onStart();

        mRedrawer.start();

        // View controller instance and start
        mOscilloscopeFragmentController = (ITouchAppViewController) new OscilloscopeFragmentControllerApp(this, mContext);
        mOscilloscopeFragmentController.startController();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("DEBUG_TAG", "OnStop");
        mOscilloscopeFragmentController.stopController();
        mRedrawer.finish();
        hideToolBars();
    }

    @Override
    public void onDestroyView() {
        this.mCustomChannel1Menu.destroyMenu();
        this.mCustomChannel2Menu.destroyMenu();
        super.onDestroyView();
    }


    /**
     * UPDATE METHODS OF THE GRAPHS
     */

    @Override
    public void updateGraphValues(Number[][][] newValuesArray) {

        mRedrawer.pause();
        mOscilloscopeSerieCh1.clear();
        mOscilloscopeSerieCh2.clear();

        if (this.mChannel1Enabled) {
            mOscilloscopeSerieCh1.updateFromXYSerie(newValuesArray[0][0], newValuesArray[0][1]);
        }
        if (this.mChannel2Enabled) {
            mOscilloscopeSerieCh2.updateFromXYSerie(newValuesArray[1][0], newValuesArray[1][1]);
        }
        mRedrawer.start();
    }

    @Override
    public void updateTimeRange(double tMin, double tMax, TimeUnits timeUnits) {

        double timePerDivision = (tMax - tMin) / OscilloscopeFragmentControllerApp.DIVISION_COUNT;
        double timeDelay = tMin;

        TextView timeUnitsText = (TextView) butTimeSettings.findViewById(R.id.timeBaseLine1);
        TextView timePerDivText = (TextView) butTimeSettings.findViewById(R.id.timeBaseLine2);
        TextView timeDelayText = (TextView) butTimeSettings.findViewById(R.id.timeBaseLine3);


        timeUnitsText.setText(String.format("Units : %s", timeUnitToString(timeUnits)));
        timePerDivText.setText(String.format("t/d : %03.03f %s", timePerDivision, timeUnitToString(timeUnits)));
        timeDelayText.setText(String.format("Delay : %03.03f %s", timeDelay, timeUnitToString(timeUnits)));

        mOscPlot.setDomainBoundaries(tMin, BoundaryMode.FIXED, tMax, BoundaryMode.FIXED);
    }

    @Override
    public void updateTriggerValue(TriggerInfo triggerInfo) {
        ChannelInfo channel = new ChannelInfo();
        this.mOscilloscopeFragmentController.getChannelInfo(triggerInfo.getTriggerChannel(), channel);
        this.mOscPlot.getGraphWidget().setTriggerLevel(triggerInfo.getTriggerLevel() / channel.getVoltagePerDiv() + channel.getViewOffset());
    }

    @Override
    public void updateTriggerMode(TriggerEdge trigEdge) {

    }

    @Override
    public void updateSelectedChannel(ChannelEnum selectedChannel) {
        Log.d("DEBUG_TAG", "Selected channels " + selectedChannel);

        TextView c1Title = (TextView) butC1Settings.findViewById(R.id.chan1Title);
        TextView offset1Line = (TextView) butC1Settings.findViewById(R.id.chan1Line1);
        TextView amplitude1Line = (TextView) butC1Settings.findViewById(R.id.chan1Line2);
        TextView freq1Line = (TextView) butC1Settings.findViewById(R.id.chan1Line3);

        TextView c2Title = (TextView) butC2Settings.findViewById(R.id.chan2Title);
        TextView offset2Line = (TextView) butC2Settings.findViewById(R.id.chan2Line1);
        TextView amplitude2Line = (TextView) butC2Settings.findViewById(R.id.chan2Line2);
        TextView freq2Line = (TextView) butC2Settings.findViewById(R.id.chan2Line3);

        c1Title.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        offset1Line.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        amplitude1Line.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        freq1Line.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        c2Title.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        offset2Line.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        freq2Line.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        amplitude2Line.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));


        switch (selectedChannel) {
            case CHANNEL1:
                c1Title.setTextColor(this.mContext.getResources().getColor(R.color.channel1_color));
                offset1Line.setTextColor(this.mContext.getResources().getColor(R.color.channel1_color));
                amplitude1Line.setTextColor(this.mContext.getResources().getColor(R.color.channel1_color));
                freq1Line.setTextColor(this.mContext.getResources().getColor(R.color.channel1_color));
                break;
            case CHANNEL2:
                c2Title.setTextColor(this.mContext.getResources().getColor(R.color.channel2_color));
                offset2Line.setTextColor(this.mContext.getResources().getColor(R.color.channel2_color));
                freq2Line.setTextColor(this.mContext.getResources().getColor(R.color.channel2_color));
                amplitude2Line.setTextColor(this.mContext.getResources().getColor(R.color.channel2_color));
                break;
        }
    }

    @Override
    public void updateChannelInfo(ChannelEnum channel, ChannelInfo channelInfo) {

        switch (channel) {
            case CHANNEL1: {
                TextView offsetLine = (TextView) butC1Settings.findViewById(R.id.chan1Line1);
                offsetLine.setText(String.format("Offset : %03.03fV", channelInfo.getOffset()));

                TextView voltPerDivLine = (TextView) butC1Settings.findViewById(R.id.chan1Line2);
                voltPerDivLine.setText(String.format("Scale : %03.03fV/div", channelInfo.getVoltagePerDiv()));

                TextView freqLine = (TextView) butC1Settings.findViewById(R.id.chan1Line3);
                freqLine.setText(String.format("Frequency : %04.03fHz", channelInfo.getMeanFreq()));
            }
            break;
            case CHANNEL2: {

                TextView offsetLine = (TextView) butC2Settings.findViewById(R.id.chan2Line1);
                offsetLine.setText(String.format("Offset : %03.03fV", channelInfo.getOffset()));

                TextView voltPerDivLine = (TextView) butC2Settings.findViewById(R.id.chan2Line2);
                voltPerDivLine.setText(String.format("Scale : %03.03fV/div", channelInfo.getVoltagePerDiv()));

                TextView freqLine = (TextView) butC2Settings.findViewById(R.id.chan2Line3);
                freqLine.setText(String.format("Frequency : %04.03fHz", channelInfo.getMeanFreq()));
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void updateTriggerInfo(TriggerInfo triggerInfo) {
        TextView triggerTitle = (TextView) butTrigSettings.findViewById(R.id.trigTitle);
        TextView triggerChannel = (TextView) butTrigSettings.findViewById(R.id.trigLine1);
        TextView triggerLevel = (TextView) butTrigSettings.findViewById(R.id.trigLine2);
        TextView triggerEdge = (TextView) butTrigSettings.findViewById(R.id.trigLine3);

        triggerTitle.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        triggerChannel.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        triggerLevel.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));
        triggerEdge.setTextColor(this.mContext.getResources().getColor(R.color.channel_nenable_color));

        triggerTitle.setTypeface(null, Typeface.NORMAL);

        triggerLevel.setText(String.format("Level : %03.03fV", triggerInfo.getTriggerLevel()));

        switch (triggerInfo.getTriggerChannel()) {
            case CHANNEL1:
                triggerChannel.setText("Channel : C1");
                this.mOscPlot.getGraphWidget().setTriggerColor(this.mContext.getResources().getColor(R.color.channel1_color));
                break;
            case CHANNEL2:
                triggerChannel.setText("Channel : C2");
                this.mOscPlot.getGraphWidget().setTriggerColor(this.mContext.getResources().getColor(R.color.channel2_color));
                break;
        }

        switch (triggerInfo.getTriggerEdge()) {
            case RISING:
                triggerEdge.setText("Edge : rising");
                break;
            case FALLING:
                triggerEdge.setText("Edge : falling");
                break;
        }


        if (triggerInfo.isSelected()) {
            triggerTitle.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public void updateEnabledChannels(Vector<ChannelEnum> enabledChannel) {
        Log.d("DEBUG_TAG", "Enabled channels " + enabledChannel);

        this.mChannel1Enabled = false;
        this.mChannel2Enabled = false;


        TextView c1Title = (TextView) butC1Settings.findViewById(R.id.chan1Title);
        TextView c2Title = (TextView) butC2Settings.findViewById(R.id.chan2Title);

        c1Title.setTypeface(null, Typeface.NORMAL);
        c2Title.setTypeface(null, Typeface.NORMAL);

        for (ChannelEnum channel : enabledChannel) {
            if (channel == ChannelEnum.CHANNEL1) {
                this.mChannel1Enabled = true;
                c1Title.setTypeface(null, Typeface.BOLD);
            } else if (channel == ChannelEnum.CHANNEL2) {
                this.mChannel2Enabled = true;
                c2Title.setTypeface(null, Typeface.BOLD);
            }

        }


    }

    @Override
    public void updateOscMode(OscilloscopeMode mode) {

        Log.d("DEBUG_TAG", "View oscilloscope mode update!");
        TextView modeTextView = (TextView) this.getActivity().findViewById(R.id.oscModeTitle);

        switch (mode) {
            case AUTO:
                hideSingleShotButton(mSingleShotBtn);
                modeTextView.setText("AUTO");
                break;
            case NORMAL:
                hideSingleShotButton(mSingleShotBtn);
                modeTextView.setText("NORMAL");
                break;
            case SINGLE_SHOT:
                showSingleShotButton(mSingleShotBtn);
                modeTextView.setText("SINGLE");
                break;
            default:
                break;
        }
    }

    @Override
    public void updateChannelsOffset(ChannelEnum channel, double offset) {

        switch (channel) {
            case CHANNEL1:
                this.mOscPlot.getGraphWidget().setChannel1Offset((float) offset);
                break;
            case CHANNEL2:
                this.mOscPlot.getGraphWidget().setChannel2Offset((float) offset);
                break;
            default:
                break;
        }

    }

    @Override
    public void updateSingleShotTrigged(boolean isTriggedWaiting) {

        mSingleShotBtn.setBackgroundTintList(ColorStateList.valueOf(this.mContext.getResources().getColor(R.color.single_shot_bnt_wait)));

        if (!isTriggedWaiting) {
            mSingleShotBtn.setBackgroundTintList(ColorStateList.valueOf(this.mContext.getResources().getColor(R.color.single_shot_bnt_normal)));
        }
    }

    @Override
    public void updateOscilloscopeTimeUnits(TimeUnits timeUnits) {

        this.mOscPlot.setDomainLabel(timeUnitToString(timeUnits));

    }

    private String timeUnitToString(TimeUnits timeUnits) {
        switch (timeUnits) {
            case NS:
                return "ns";
            case US:
                return "us";
            case MS:
                return "ms";
            case S:
                return "s";
            default:
                return "";
        }
    }

    @Override
    public void showChannelCustomMenu(ChannelEnum channelMenuToShow) {

        switch (channelMenuToShow) {
            case CHANNEL1:
                mCustomChannel1Menu.showMenu();
                break;
            case CHANNEL2:
                mCustomChannel2Menu.showMenu();
                break;
            case NONE:
                break;
        }
    }


    @Override
    public void hideChannelCustomMenu(ChannelEnum channelMenuToHide) {
        switch (channelMenuToHide) {
            case CHANNEL1:
                mCustomChannel1Menu.hideMenu();
                break;
            case CHANNEL2:
                mCustomChannel2Menu.hideMenu();
                break;
            case NONE:
                break;
        }
    }

    @Override
    public void hideChannelCustomMenuLayout() {
        mCustomChannel1Menu.hideLayout();
    }

    // ----------------------------------------------------------------------------------
    // Internal class: Extend of the gestureDetectorListener
    // ----------------------------------------------------------------------------------
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }


    }

    // ----------------------------------------------------------------------------------
    // Internal class: Extend of the ScaleGestureListener
    // ----------------------------------------------------------------------------------
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

}
