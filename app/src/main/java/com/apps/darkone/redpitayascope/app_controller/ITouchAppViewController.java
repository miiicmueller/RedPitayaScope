package com.apps.darkone.redpitayascope.app_controller;

/**
 * Created by Matthieu on 23.11.2015.
 */
public interface ITouchAppViewController {


    /**
     * Start the controller. That mean, starting the application on the red pitaya and the other services
     */
    public void startController();

    /**
     * Stop the controller
     */
    public void stopController();



    // Is called when a zoom X gesture occure
    // Get the scale factor value
    public void onScaleFactorX(float scaleFactorValue);

    // Is called when a zoom Y gesture occure
    // Get the scale factor value
    public void onScaleFactorY(float scaleFactorValue);

    // Is called when a doubleTap gesture occure
    public  void onDoubleTapGesture();

    // Is called when a long

}
