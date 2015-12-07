package com.apps.darkone.redpitayascope.app_fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.apps.darkone.redpitayascope.R;
import com.apps.darkone.redpitayascope.app_controller.ITouchAppViewController;
import com.apps.darkone.redpitayascope.app_controller.OscilloscopeFragmentControllerApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.OscilloscopeTimeValueSerie;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.ChannelEnum;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

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


    private Redrawer mRedrawer;


    private static final String OSC_SERIE_NAME = "";

    private ActionBar mMainActionBar;
    private Toolbar mMainToolBar;
    private Toolbar mBottomActionBar;
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


        mRedrawer.start();

        mMainActionBar = (ActionBar) ((AppCompatActivity) getActivity()).getSupportActionBar();
        mMainToolBar = (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.maintoolbar);
        mBottomActionBar = (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_bottom);


        mToolBarVisible = true;
        LinearLayout layout = (LinearLayout) mBottomActionBar.findViewById(R.id.toolbar_bottom_layer);
        layout.setBackgroundColor(this.mContext.getResources().getColor(R.color.button_background_pressed));
        View osc_toolbar = inflater.inflate(R.layout.osc_control_bar, layout, false);
        osc_toolbar.setBackgroundColor(this.mContext.getResources().getColor(R.color.button_background_pressed));
        layout.removeAllViews();
        layout.addView(osc_toolbar);


        // Single shot floating action button
        mSingleShotBtn = (FloatingActionButton) rootView.findViewById(R.id.single_shot_btn);
        mSingleShotBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("DEBUG_TAG", "Button pressed");
            }
        });

        //Hide the button
        hideSingleShotButton(mSingleShotBtn);
        mSingleShotBtn.hide();

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
                    mToolBarVisible = false;
                    mMainToolBar.animate().translationX(-mMainToolBar.getRight()).setInterpolator(new AccelerateInterpolator((float) 2.0)).start();
                    mBottomActionBar.animate().translationX(mBottomActionBar.getRight()).setInterpolator(new AccelerateInterpolator((float) 2.0)).start();
                } else {

                    // If the bar was hidden
                    if (!mMainActionBar.isShowing()) {
                        mMainActionBar.show();
                    }

                    mToolBarVisible = true;
                    mMainToolBar.animate().translationX(0).setInterpolator(new DecelerateInterpolator((float) 2.0)).start();
                    mBottomActionBar.animate().translationX(0).setInterpolator(new DecelerateInterpolator((float) 2.0)).start();
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
                return true;
            }
        });
        // ---------------------------------------------------------------------------------------------------
        // END Set des gestures sur les boutons et le graphe
        // ---------------------------------------------------------------------------------------------------


        Log.d("DEBUG_TAG", "onCreateView end");

        return rootView;
    }


    private void hideSingleShotButton(FloatingActionButton btn) {
        btn.animate().translationY(-btn.getBottom()).setInterpolator(new AccelerateInterpolator((float) 2.0)).start();
    }


    private void showSingleShotButton(FloatingActionButton btn) {
        mSingleShotBtn.show();
        btn.animate().translationY(-mOscPlot.getHeight() / 2.f).setInterpolator(new DecelerateInterpolator((float) 2.0)).start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        Log.d("DEBUG_TAG", "OnStart");
        // View controller instance and start
        mOscilloscopeFragmentController = (ITouchAppViewController) new OscilloscopeFragmentControllerApp(this, mContext);
//        mOscilloscopeFragmentController.startController();
        super.onStart();
    }

    private void touchAnalyse(View v, MotionEvent event) {


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("DEBUG_TAG", "OnStop");
        mRedrawer.finish();
        //  mOscilloscopeFragmentController.stopController();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * UPDATE METHODS OF THE GRAPHE
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
    public void updateTimeRange(double tMin, double tMax) {
        mOscPlot.setDomainBoundaries(tMin, BoundaryMode.FIXED, tMax, BoundaryMode.FIXED);
    }

    @Override
    public void updateTriggerValue(float triggerValue) {

    }

    @Override
    public void updateTriggerMode(TriggerEdge trigEdge) {

    }

    @Override
    public void updateSelectedChannel(ChannelEnum selectedChannel) {
        Log.d("DEBUG_TAG", "Selected channels " + selectedChannel);
    }

    @Override
    public void updateEnabledChannels(Vector<ChannelEnum> enabledChannel) {
        Log.d("DEBUG_TAG", "Enabled channels " + enabledChannel);

        this.mChannel1Enabled = false;
        this.mChannel2Enabled = false;

        for (ChannelEnum channel : enabledChannel) {
            if (channel == ChannelEnum.CHANNEL1) {
                this.mChannel1Enabled = true;
            } else if (channel == ChannelEnum.CHANNEL2) {
                this.mChannel2Enabled = true;
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
    public void updateOscilloscopeTimeUnits(TimeUnits timeUnits) {

        switch (timeUnits) {
            case NS:
                this.mOscPlot.setDomainLabel("ns");
                break;
            case US:
                this.mOscPlot.setDomainLabel("us");
                break;
            case MS:
                this.mOscPlot.setDomainLabel("ms");
                break;
            case S:
                this.mOscPlot.setDomainLabel("s");
                break;
            default:
                break;
        }

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
