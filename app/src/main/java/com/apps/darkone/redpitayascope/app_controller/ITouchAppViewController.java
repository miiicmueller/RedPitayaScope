package com.apps.darkone.redpitayascope.app_controller;

import android.view.View;

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

    /**
     * Callbacks du bouton 1 butOscMode
     */
    public void butOscModeOnDoubleTap();
    public void butOscModeOnSingleTapConfirmed();
    public void butOscModeOnLongPress();
    /**
     * Callbacks du bouton 2 butTrigSettings
     */
    public void butTrigSettingsOnDoubleTap();
    public void butTrigSettingsOnSingleTapConfirmed();
    public void butTrigSettingsOnLongPress();
    /**
     * Callbacks du bouton 3 butTimeSettings
     */
    public void butTimeSettingsOnDoubleTap();
    public void butTimeSettingsOnSingleTapConfirmed();
    public void butTimeSettingsOnLongPress();
    /**
     * Callbacks du bouton 4 butC1Settings
     */
    public void butC1SettingsOnDoubleTap();
    public void butC1SettingsOnSingleTapConfirmed();
    public void butC1SettingsOnLongPress();
    /**
     * Callbacks du bouton 5 butC2Settings
     */
    public void butC2SettingsOnDoubleTap();
    public void butC2SettingsOnSingleTapConfirmed();
    public void butC2SettingsOnLongPress();

    /**
     * Callbacks du graphe mOscPlot
     */
    public void mOscPlotOnDoubleTap();
    public void mOscPlotOnSingleTapConfirmed();
    public void mOscPlotOnLongPress();
    public void mOscPlotOnScroll(float distanceX, float distanceY);
    public void mOscPlotOnFling(float velocityX, float velocityY);
    public void mOscPlotOnScaleBegin();
    public void mOscPlotOnScaleX(float X);
    public void mOscPlotOnScaleY(float Y);
    public void mOscPlotOnScaleEnd();


    /**
     * Methodes pour les boutons
     */
    public  void buttonPressed(View view);
    public  void buttonReleased(View view);



}
