package com.apps.darkone.redpitayascope.communication;

import com.apps.darkone.redpitayascope.communication.commSAP.ICommunicationService;

/**
 * Created by DarkOne on 16.10.15.
 */
public class CommunicationServiceFactory {

    private static CommunicationServiceImpl mCommunicationService;


    private static final String DEFAULT_IP_ADDR = "192.168.1.2";

    /**
     * Singleton based method. Returns the instance of a communcation service
     *
     * @return
     */
    public static ICommunicationService getCommuncationServiceInstance() {

        // Check null reference
        if (null == mCommunicationService) {
            mCommunicationService = new CommunicationServiceImpl(DEFAULT_IP_ADDR);
        }

        return mCommunicationService;

    }


}
