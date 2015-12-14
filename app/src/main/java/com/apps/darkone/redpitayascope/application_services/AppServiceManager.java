package com.apps.darkone.redpitayascope.application_services;

import android.content.Context;
import android.util.Log;

import com.apps.darkone.redpitayascope.app_service_sap.IAppService;
import com.apps.darkone.redpitayascope.app_service_sap.ServiceStatus;
import com.apps.darkone.redpitayascope.communication.CommunicationServiceFactory;
import com.apps.darkone.redpitayascope.communication.commSAP.ICommunicationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DarkOne on 21.10.15.
 */
public class AppServiceManager {


    private List<IAppService> mAppServiceList;
    private Context mContext;
    private static String APP_SERVICE_MANAGER = "AppServiceManager";

    public AppServiceManager(Context context) {

        Log.d(APP_SERVICE_MANAGER, "Starting...");

        this.mAppServiceList = new ArrayList<>();
        this.mContext = context;

        // Add services
        addAppService((IAppService) AppServiceFactory.getOscilloscopeInstance());
        addAppService((IAppService) AppServiceFactory.getSprectrumInstance());


    }

    public void runServices(String ipAddress) {

        Log.d(APP_SERVICE_MANAGER, "Running service with IP address: " + ipAddress);

        // Only called once. Other time it will have no effect
        ICommunicationService communicationService = CommunicationServiceFactory.getCommuncationServiceInstance();
        communicationService.startService(ipAddress, this.mContext);

    }

    public void changeServiceSettings(String ipAddress) {
        Log.d(APP_SERVICE_MANAGER, "Changing IP address : " + ipAddress);

        ICommunicationService communicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        String actualRunningApp;

        //Stop all running apps if the service is used
        if (communicationService.isServiceUsed()) {

            // Remind the app who is running...
            actualRunningApp = communicationService.getActualRunningAppName();

            for (IAppService appService : mAppServiceList) {
                appService.stopAppService();
            }

            //Request the app start after service wake up
            communicationService.startApp(actualRunningApp);
        }

        communicationService.stopService();
        communicationService.startService(ipAddress, this.mContext);

    }


    private void addAppService(IAppService appService) {
        Log.d(APP_SERVICE_MANAGER, "Adding service : " + appService.getAppServiceName());
        this.mAppServiceList.add(appService);
    }

    private void removeAppService(IAppService appService) {

        //Check first that the app is not running
        if (appService.getAppServiceStatus() != ServiceStatus.STOPPED) {
            // Stop the service
            appService.stopAppService();
        }

        this.mAppServiceList.remove(appService);
    }


    public void setAppServiceFocus(IAppService appService) {

        ICommunicationService communicationService = CommunicationServiceFactory.getCommuncationServiceInstance();

        if (this.mAppServiceList.contains(appService)) {


            // First check if we start the same service
            if (!communicationService.getActualRunningAppName().equals(appService.getAppServiceName())) {
                // Stop the service
                if (communicationService.isServiceUsed()) {
                    for (IAppService app : mAppServiceList) {

                        if (communicationService.getActualRunningAppName().equals(app.getAppServiceName())) {
                            app.stopAppService();
                            // We wait for completion
                            while (app.getAppServiceStatus() == ServiceStatus.UP) ;
                        }
                    }
                }
            }

            appService.startAppService();
        }

    }

    public boolean isServiceAppUp(IAppService appService) {
        return (appService.getAppServiceStatus() == ServiceStatus.UP);
    }

}
