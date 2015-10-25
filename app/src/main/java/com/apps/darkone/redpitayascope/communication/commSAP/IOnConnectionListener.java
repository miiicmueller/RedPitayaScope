package com.apps.darkone.redpitayascope.communication.commSAP;

import com.apps.darkone.redpitayascope.communication.ConnectionEvent;

/**
 * Created by DarkOne on 07.10.15.
 */
public interface IOnConnectionListener {

    /**
     * A new event occur on the communication
     * @param appName : App name concerned
     * @param connectionEvent : Event who occur
     */
    public void onConnectionChanged(String appName, ConnectionEvent connectionEvent);
}
