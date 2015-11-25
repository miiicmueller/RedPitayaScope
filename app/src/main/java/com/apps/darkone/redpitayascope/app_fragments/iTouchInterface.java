package com.apps.darkone.redpitayascope.app_fragments;

/**
 * Created by Matthieu on 23.11.2015.
 */
public interface iTouchInterface {

    // Is called when a zoom X gesture occure
    // Get the scale factor value
    void onScaleFactorX(float scaleFactorValue);

    // Is called when a zoom Y gesture occure
    // Get the scale factor value
    void onScaleFactorY(float scaleFactorValue);

    // Is called when a doubleTap gesture occure
    void onDoubleTapGesture();

    // Is called when a long

}
