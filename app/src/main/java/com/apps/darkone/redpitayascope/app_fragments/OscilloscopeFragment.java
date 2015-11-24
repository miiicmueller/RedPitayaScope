package com.apps.darkone.redpitayascope.app_fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.apps.darkone.glplot.SignalChartView;
import com.apps.darkone.redpitayascope.R;
import com.apps.darkone.redpitayascope.application_services.AppServiceFactory;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.OscilloscopeTimeValueSerie;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOnChannelsValueListener;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;

import java.util.Arrays;

/**
 * Created by DarkOne on 02.11.15.
 */
public class OscilloscopeFragment extends Fragment implements IOnChannelsValueListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    private Context mContext;
    private XYPlot plot;
    private IOscilloscopeApp mOscilloscopeApp;
    private LineAndPointFormatter mChannelFormaterCh1;
    private LineAndPointFormatter mChannelFormaterCh2;
    private String timeUnits;
    private GestureDetector.SimpleOnGestureListener mGestureListener;
    private double actualTriggerLevel;


    private OscilloscopeTimeValueSerie mOscilloscopeSerieCh1;
    private OscilloscopeTimeValueSerie mOscilloscopeSerieCh2;

    private Redrawer mRedrawer;


    private static final String OSC_SERIE_NAME = "";
    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector mScaleDetector;

   private ActionBar myActionbar;

    public static OscilloscopeFragment newInstance() {
        OscilloscopeFragment settingFragment = new OscilloscopeFragment();
        return settingFragment;
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        mContext = context;

        // Add the listener for the new value
        mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();
        mOscilloscopeApp.addAppValuesListener(this);

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
        plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);


        // reduce the number of range labels
        plot.setBackgroundPaint(null);
        plot.setBorderPaint(null);
        plot.setBackground(null);
        plot.setLayerPaint(null);
        plot.setRenderMode(Plot.RenderMode.USE_BACKGROUND_THREAD);
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().

                setDomainLabelOrientation(-45);

        plot.setDomainBoundaries(0, BoundaryMode.FIXED, 131, BoundaryMode.FIXED);
        plot.setRangeBoundaries(-5, BoundaryMode.FIXED, 5, BoundaryMode.FIXED);
        plot.getGraphWidget().

                setGridBackgroundPaint(null);

        plot.getGraphWidget().

                setClippingEnabled(true);

        plot.setDomainLabel(timeUnits);


        mChannelFormaterCh1 = new

                LineAndPointFormatter(Color.rgb(255, 255, 0),

                null, null, null);
        mChannelFormaterCh2 = new

                LineAndPointFormatter(Color.rgb(255, 0, 102),

                null, null, null);


        // Create the serie only one time
        mOscilloscopeSerieCh1 = new

                OscilloscopeTimeValueSerie(OSC_SERIE_NAME);

        plot.addSeries(mOscilloscopeSerieCh1, mChannelFormaterCh1);

        mOscilloscopeSerieCh2 = new

                OscilloscopeTimeValueSerie(OSC_SERIE_NAME);

        plot.addSeries(mOscilloscopeSerieCh2, mChannelFormaterCh2);


        mRedrawer = new

                Redrawer(
                Arrays.asList(new Plot[]{
                                plot
                        }
                ),
                20, false);


        mRedrawer.start();

        // Gesture and detectors
        // --------------------
        mDetector = new GestureDetectorCompat(mContext, this);
        mDetector.setOnDoubleTapListener(this);

        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mScaleDetector.onTouchEvent(event);
                mDetector.onTouchEvent(event);
                return true;
            }
        });

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroyView() {

        mOscilloscopeApp = AppServiceFactory.getOscilloscopeInstance();
        this.mOscilloscopeApp.removeAppValuesListener(this);

        mRedrawer.finish();

        super.onDestroyView();
    }

    @Override
    public void onNewValues(Number[][][] newValuesArray) {
        mOscilloscopeSerieCh1.updateFromXYSerie(newValuesArray[0][0], newValuesArray[0][1]);
        mOscilloscopeSerieCh2.updateFromXYSerie(newValuesArray[1][0], newValuesArray[1][1]);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("DEBUG_TAG", "On Single TAP Up Event!");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("DEBUG_TAG", "On Scroll Event!");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("DEBUG_TAG", "LongPress Event!");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("DEBUG_TAG", "On Flint!");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d("DEBUG_TAG", "Double TAP!");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d("DEBUG_TAG", "Double TAP Event!");
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("DEBUG_TAG", "Single TAP Confirmed!");
        if(myActionbar.isShowing())
        {
            myActionbar.hide();
        }
        else
        {
            myActionbar.show();
        }
        return false;
    }



    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if(detector.getCurrentSpanX() > detector.getCurrentSpanY())
            {
                //TODO appeler le callback scaleX
                //mScaleFactorX *= detector.getScaleFactor();
            }else
            {
                //TODO appeler le callback scaleY
                //mScaleFactorY *= detector.getScaleFactor();
            }

            // Don't let the object get too small or too large.
            //mScaleFactorX = Math.max(0.1f, Math.min(mScaleFactorX, 5.0f));
            //mScaleFactorY = Math.max(0.1f, Math.min(mScaleFactorY, 5.0f));

            //Log.d("DEBUG_TAG", String.format("Scale factor:\tX = %f\tY = %f", mScaleFactorX, mScaleFactorY));

            return true;
        }
    }

}
