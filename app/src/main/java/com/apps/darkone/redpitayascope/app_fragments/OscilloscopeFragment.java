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


        // butOscModeDetector gesture detector
        butOscModeDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

        });


        // butTrigSettingsDetector gesture detector
        butTrigSettingsDetector = new GestureDetectorCompat(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        // butTimeSettingsDetector gesture detector
        butTimeSettingsDetector = new GestureDetectorCompat(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        // butC1SettingsDetector gesture detector
        butC1SettingsDetector = new GestureDetectorCompat(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        // butC2SettingsDetector gesture detector
        butC2SettingsDetector = new GestureDetectorCompat(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        // XYPlot gesture detector
        XYPlotDetector = new GestureDetectorCompat(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });


        // XYPlot gesture scale detector
        XYPlotScaleDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });

                // Set des gestures sur les boutons
                // ---------------------------------
                butOscMode = (TableLayout) rootView.findViewById(R.id.oscMode);

        butOscMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                butOscModeDetector.onTouchEvent(event);
                Log.d("DEBUG_TAG", "On Touch OscMode Event!");

                return true;
            }

        });


        butTrigSettings = (TableLayout) rootView.findViewById(R.id.trig);
        butTimeSettings = (TableLayout) rootView.findViewById(R.id.timeBase);
        butC1Settings = (TableLayout) rootView.findViewById(R.id.chan1);
        butC2Settings = (TableLayout) rootView.findViewById(R.id.chan2);



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
     * UPDATE METHODS
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


    /**
     * TOUCH METHODS
     */

//    @Override
//    public boolean onDown(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        Log.d("DEBUG_TAG", "On Single TAP Up Event!");
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        Log.d("DEBUG_TAG", "On Scroll Event!");
//        //TODO appeler le callback on scroll event
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//        // TODO appeler le callback logpressgesture
//        Log.d("DEBUG_TAG", "LongPress Event!");
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.d("DEBUG_TAG", "On Flint!");
//        return false;
//    }
//
//    @Override
//    public boolean onDoubleTap(MotionEvent e) {
//        Log.d("DEBUG_TAG", "Double TAP!");
//        return false;
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent e) {
//        Log.d("DEBUG_TAG", "Double TAP Event!");
//        // TODO appeler le callback doubletap
//        return false;
//    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent e) {
//
//        Log.d("DEBUG_TAG", "Single TAP Confirmed!");
//        if (myActionbar.isShowing()) {
//            myActionbar.hide();
//        } else {
//            myActionbar.show();
//        }
//        return false;
//    }
//
//
//    private class ScaleListener
//            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//
//            if (detector.getCurrentSpanX() > detector.getCurrentSpanY()) {
//                //TODO appeler le callback scaleX
//                //mScaleFactorX *= detector.getScaleFactor();
//            } else {
//                //TODO appeler le callback scaleY
//                //mScaleFactorY *= detector.getScaleFactor();
//            }
//
//            // Don't let the object get too small or too large.
//            //mScaleFactorX = Math.max(0.1f, Math.min(mScaleFactorX, 5.0f));
//            //mScaleFactorY = Math.max(0.1f, Math.min(mScaleFactorY, 5.0f));
//
//            //Log.d("DEBUG_TAG", String.format("Scale factor:\tX = %f\tY = %f", mScaleFactorX, mScaleFactorY));
//
//            return true;
//        }
//    }

}
