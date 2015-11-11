package com.apps.darkone.glplot;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

/**
 * Created by DarkOne on 06.11.15.
 */
public class PlotRenderer implements Renderer {

    private SignalChart sineChart;
    GL10 glx;
    public volatile float[] chartData = new float[400];
    int width;
    int height;
    Context context;
    /** Constructor */
    public PlotRenderer(Context context) {
        this.sineChart = new SignalChart();
        this.context = context;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // clear Screen and Depth Buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Reset the Modelview Matrix
        gl.glLoadIdentity();
        // Drawing
        //Log.d("Chart Ratio1 "," width " +width + " H " + height);
        gl.glTranslatef(0.0f, 0.0f, -3.0f);     // move 5 units INTO the screen
        // is the same as moving the camera 5 units away
        this.sineChart.setResolution(width, height);
        this.sineChart.setChartData(chartData);
        sineChart.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;

        if(height == 0) {                       //Prevent A Divide By Zero By
            height = 1;                         //Making Height Equal One
        }
        gl.glViewport(0, 0, width, height);     //Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);    //Select The Projection Matrix
        gl.glLoadIdentity();                    //Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        //Log.d("Chart Ratio2 "," width " +width + " H " + height);
        GLU.gluPerspective(gl, 45.0f, (float) height * 2.0f/(float)width, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);     //Select The Modelview Matrix
        gl.glLoadIdentity();                    //Reset The Modelview Matrix
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }
}
