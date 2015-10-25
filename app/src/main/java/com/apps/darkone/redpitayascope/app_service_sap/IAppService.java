package com.apps.darkone.redpitayascope.app_service_sap;

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
