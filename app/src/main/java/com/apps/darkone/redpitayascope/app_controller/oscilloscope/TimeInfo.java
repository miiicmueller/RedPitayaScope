package com.apps.darkone.redpitayascope.app_controller.oscilloscope;

import com.apps.darkone.redpitayascope.application_services.oscilloscope.oscilloscope_sap.TimeUnits;

/**
 * Created by Matthieu on 22.12.2015.
 */
public class TimeInfo {

    private TimeUnits graphTimeUnits;
    private double graphTimeValue0;
    private double graphTimeValue1;


    public TimeInfo(TimeUnits graphTimeUnits, double graphTimeValue0, double graphTimeValue1) {
        this.graphTimeUnits = graphTimeUnits;
        this.graphTimeValue0 = graphTimeValue0;
        this.graphTimeValue1 = graphTimeValue1;
    }

    public TimeInfo() {
        this.graphTimeUnits = TimeUnits.MS;
        this.graphTimeValue0 = 0;
        this.graphTimeValue1 = 0;
    }

    public TimeInfo(TimeInfo localTimeInfo){
        this.graphTimeUnits = localTimeInfo.getGraphTimeUnits();
        this.graphTimeValue0 = localTimeInfo.getGraphTimeValue0();
        this.graphTimeValue1 = localTimeInfo.getGraphTimeValue1();
    }

    public TimeUnits getGraphTimeUnits() {
        return graphTimeUnits;
    }

    public TimeInfo setGraphTimeUnits(TimeUnits graphTimeUnits) {
        this.graphTimeUnits = graphTimeUnits;
        return this;
    }

    public double getGraphTimeValue0() {
        return graphTimeValue0;
    }

    public TimeInfo setGraphTimeValue0(double graphTimeValue0) {
        this.graphTimeValue0 = graphTimeValue0;
        return this;
    }

    public double getGraphTimeValue1() {
        return graphTimeValue1;
    }

    public TimeInfo setGraphTimeValue1(double graphTimeValue1) {
        this.graphTimeValue1 = graphTimeValue1;
        return this;
    }
}
