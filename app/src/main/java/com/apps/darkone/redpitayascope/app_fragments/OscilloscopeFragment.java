package com.apps.darkone.redpitayascope.app_fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

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
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOnChannelsValueListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.OscilloscopeMode;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TriggerEdge;

import java.util.Arrays;

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

    private Redrawer mRedrawer;


    private static final String OSC_SERIE_NAME = "";

    private ActionBar myActionbar;

    // boutons
    private TableLayout butOscMode;
    private TableLayout butTrigSettings;
    private TableLayout butTimeSettings;
    private TableLayout butC1Settings;
    private TableLayout butC2Settings;
    private OscilloscopeFragmentControllerApp mOscilloscopeFragmentController;

    private GestureDetectorCompat butOscModeDetector;
    private GestureDetectorCompat butTrigSettingsDetector;
    private GestureDetectorCompat butTimeSettingsDetector;
    private GestureDetectorCompat butC1SettingsDetector;
    private GestureDetectorCompat butC2SettingsDetector;
    private GestureDetectorCompat XYPlotDetector;
    private ScaleGestureDetector XYPlotScaleDetector;

    private ITouchAppViewController oscilloscopeFragmentController;


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

        // Plot utility initialization

        this.timeUnits = "ms";
        this.actualTriggerLevel = 0.0;


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

        myActionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // ---------------------------------------------------------------------------------------------------
        // Set des gestures sur les boutons et le graphe
        // Technical reference: http://developer.android.com/training/gestures/detector.html
        // ---------------------------------------------------------------------------------------------------

        // Get object reference
        butOscMode = (TableLayout) rootView.findViewById(R.id.oscMode);
        // Set background color
        //butOscMode.setBackgroundColor(0xaa39c9c9);
        // Set gesture detector
        butOscModeDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap OscMode Event!");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed OscMode Event!");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress OscMode Event!");
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                // set background color when pressed0x009090
                butOscMode.setBackgroundColor(0xaa008080);
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // set background color when pressed
                butOscMode.setBackgroundColor(0xaa39c9c9);
                return super.onSingleTapUp(e);
            }
        });
        butOscMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                butOscModeDetector.onTouchEvent(event);
                return true;
            }
        });
        // ---------------------
        // Get object reference
        butTrigSettings = (TableLayout) rootView.findViewById(R.id.trig);
        // Set gesture detector
        butTrigSettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butTrigSettings Event!");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butTrigSettings Event!");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butTrigSettings Event!");
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                // set background color when pressed0x009090
                butTrigSettings.setBackgroundColor(0xaa008080);
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // set background color when pressed
                butTrigSettings.setBackgroundColor(0xaa39c9c9);
                return super.onSingleTapUp(e);
            }
        });
        butTrigSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                butTrigSettingsDetector.onTouchEvent(event);
                return true;
            }
        });
        // ---------------------
        butTimeSettings = (TableLayout) rootView.findViewById(R.id.timeBase);
        butTimeSettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butTimeSettings Event!");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butTimeSettings Event!");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butTimeSettings Event!");
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                // set background color when pressed0x009090
                butTimeSettings.setBackgroundColor(0xaa008080);
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // set background color when pressed
                butTimeSettings.setBackgroundColor(0xaa39c9c9);
                return super.onSingleTapUp(e);
            }
        });
        butTimeSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                butTimeSettingsDetector.onTouchEvent(event);
                return true;
            }
        });
        // ---------------------
        butC1Settings = (TableLayout) rootView.findViewById(R.id.chan1);
        butC1SettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butC1Settings Event!");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butC1Settings Event!");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butC1Settings Event!");
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                // set background color when pressed0x009090
                butC1Settings.setBackgroundColor(0xaa008080);
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // set background color when pressed
                butC1Settings.setBackgroundColor(0xaa39c9c9);
                return super.onSingleTapUp(e);
            }
        });
        butC1Settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                butC1SettingsDetector.onTouchEvent(event);
                return true;
            }
        });
        // ---------------------
        butC2Settings = (TableLayout) rootView.findViewById(R.id.chan2);
        butC2SettingsDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap butC2Settings Event!");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed butC2Settings Event!");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress butC2Settings Event!");
                super.onLongPress(e);
            }

            @Override
            public boolean onDown(MotionEvent event) {
                // set background color when pressed0x009090
                butC2Settings.setBackgroundColor(0xaa008080);
                return super.onDown(event);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // set background color when pressed
                butC2Settings.setBackgroundColor(0xaa39c9c9);
                return super.onSingleTapUp(e);
            }
        });
        butC2Settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                butC2SettingsDetector.onTouchEvent(event);
                return true;
            }
        });
        // ---------------------
        // mOscPlot = (TableLayout) rootView.findViewById(R.id.chan2); // This line has been done above...
        XYPlotDetector = new GestureDetectorCompat(mContext, new MyGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d("DEBUG_TAG", "On DoubleTap mOscPlot Event!");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("DEBUG_TAG", "On SingleTapConfirmed mOscPlot Event!");
                // Show/Hide the action bar
                if(myActionbar.isShowing())
                {
                    myActionbar.hide();
                }else
                {
                    myActionbar.show();
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d("DEBUG_TAG", "On Longpress mOscPlot Event!");
                super.onLongPress(e);
            }
        });
        mOscPlot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                XYPlotDetector.onTouchEvent(event);
                return true;
            }
        });
        // ---------------------------------------------------------------------------------------------------
        // END Set des gestures sur les boutons et le grahpe
        // ---------------------------------------------------------------------------------------------------



        oscilloscopeFragmentController = (ITouchAppViewController) new OscilloscopeFragmentControllerApp(this,mContext); // this = iFragmentInterface
        // View controller instance and start
        mOscilloscopeFragmentController = new OscilloscopeFragmentControllerApp(this, this.mContext);
        mOscilloscopeFragmentController.startController();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    private void touchAnalyse(View v, MotionEvent event) {


    }


    @Override
    public void onDestroyView() {

        mRedrawer.finish();
        mOscilloscopeFragmentController.stopController();

        super.onDestroyView();
    }


    /**
     * UPDATE METHODS OF THE GRAPHE
     */

    @Override
    public void updateGraphValues(Number[][][] newValuesArray) {

        // TODO implements the case when a channel is disable

        mOscilloscopeSerieCh1.updateFromXYSerie(newValuesArray[0][0], newValuesArray[0][1]);
        mOscilloscopeSerieCh2.updateFromXYSerie(newValuesArray[1][0], newValuesArray[1][1]);
    }

    @Override
    public void updateTimeRange(int tMin, int tMax) {
        mOscPlot.setDomainBoundaries(tMin, BoundaryMode.FIXED, tMax, BoundaryMode.FIXED);
    }

    @Override
    public void updateTriggerValue(float triggerValue) {

    }

    @Override
    public void updateTriggerMode(TriggerEdge trigEdge) {

    }

    @Override
    public void updateSelectedChannel(ChannelEnum[] selectedChannel) {

    }

    @Override
    public void updateOscMode(OscilloscopeMode mode) {

    }

    // Internal class: Extend of the gestureDetectorListener
    // -------------------------------------
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
    }
}
