package com.apps.darkone.redpitayascope.application_services;

import android.content.Context;

import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.OscilloscopeServiceImpl;
import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.IOscilloscopeApp;
import com.apps.darkone.redpitayascope.application_services.spectrum.SpectrumServiceImpl;
import com.apps.darkone.redpitayascope.application_services.spectrum.spectrum_sap.ISpectrumApp;

/**
 * Created by DarkOne on 21.10.15.
 */
public class AppServiceFactory {

    private static IAppService mOscilloApp;
    private static IAppService mSpectrumApp;


    private static AppServiceManager mAppServiceManager;


    public static IOscilloscopeApp getOscilloscopeInstance()
    {
        if(null == mOscilloApp)
        {
            mOscilloApp = new OscilloscopeServiceImpl();
        }

        return (IOscilloscopeApp) mOscilloApp;
    }

    public static ISpectrumApp getSprectrumInstance()
    {
        if(null == mSpectrumApp)
        {
            mSpectrumApp = new SpectrumServiceImpl();
        }

        return (ISpectrumApp) mSpectrumApp;
    }


    public static AppServiceManager getAppServiceManager(Context context)
    {
        if(null == mAppServiceManager)
        {
            mAppServiceManager = new AppServiceManager(context);
        }

        return  mAppServiceManager;
    }

}
