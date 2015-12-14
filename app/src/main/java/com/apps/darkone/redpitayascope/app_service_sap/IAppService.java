package com.apps.darkone.redpitayascope.app_service_sap;

import com.apps.darkone.redpitayascope.application_services.IOnAppParamsListener;

/**
 * Created by DarkOne on 20.10.15.
 */
public interface IAppService {

    /**
     * Start an application service
     */
    public void startAppService();

    /**
     * Stop an application service
     */
    public void stopAppService();


    /**
     * Set the service listener
     */
    public  void setOnServiceListener(IOnServiceListener onServiceListener);


    /**
     * Set the service listener
     */
    public  void removeOnServiceListener();


    /**
     * Set the application parameters listener
     * @param appParamsListener
     */
    public void setOnAppParamsListener(IOnAppParamsListener appParamsListener);

    /**
     * Get the service status
     * @return the Service status.
     */
    public ServiceStatus getAppServiceStatus();


    /**
     * Get the service name
     * @return service name
     */
    public String getAppServiceName() ;

}
