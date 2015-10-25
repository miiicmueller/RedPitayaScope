package com.apps.darkone.redpitayascope.communication;

/**
 * Created by DarkOne on 16.10.15.
 */
public class ConnectionEvent {

    private EventCodeEnum eventCode;


    /**
     * Public constructor
     */

    public ConnectionEvent(EventCodeEnum eventCode)
    {
            this.eventCode = eventCode;
    }

    /**
     * Retreive the event code
     * @return EventCodeEnum
     */
    public  EventCodeEnum getEventCode()
    {
        return this.eventCode;
    }
}
