package com.apps.darkone.redpitayascope.parameters;

/**
 * Created by DarkOne on 19.10.15.
 */
public class ParameterManagerFactory {

    private static ParameterManager mParameterManager;



    public static ParameterManager getParameterManagerInstance()
    {
        if(null == mParameterManager)
        {
            mParameterManager = new ParameterManager();
        }

        return mParameterManager;
    }

}
