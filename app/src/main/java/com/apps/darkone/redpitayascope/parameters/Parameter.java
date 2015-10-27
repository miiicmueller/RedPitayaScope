package com.apps.darkone.redpitayascope.parameters;

/**
 * Created by DarkOne on 19.10.15.
 */
public class Parameter<T> {

    // Input
    private String mParamName;
    private T mParamValue;
    private boolean isData; // This parameter is updated by the red pitaya

    public Parameter(String paramName, T paramValue, boolean isUpdatedByBoard) {
        this.mParamName = paramName;
        this.mParamValue = paramValue;
        this.isData = isUpdatedByBoard;
    }

    public String getParamName() {
        return this.mParamName;
    }

    public T getParamValue() {
        return this.mParamValue;
    }

    public void setParamValue(T newValue) {
        this.mParamValue = newValue;
    }

    public boolean isData() {
        return isData;
    }

    @Override
    public boolean equals(Object obj) {
        boolean testEqual = false;
        Parameter<T> paramCompare = (Parameter<T>) obj;


        // We always return true if it is a parameter who is updated by the red pitaya
        testEqual = isData || (mParamName.equals(paramCompare.getParamName()) && isEgalDouble((Double) mParamValue, (Double) paramCompare.getParamValue(), Math.pow(5, -6.0)));


        return testEqual;


    }


    private boolean isEgalDouble(Double dbl1, Double dbl2, Double error) {
        Double sub = dbl1 - dbl2;
        return (Math.abs(sub) < error);
    }
}
