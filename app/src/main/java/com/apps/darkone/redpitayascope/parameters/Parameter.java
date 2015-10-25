package com.apps.darkone.redpitayascope.parameters;

/**
 * Created by DarkOne on 19.10.15.
 */
public class Parameter<T>{

    // Input
    private String mParamName;
    private T mParamValue;


    public Parameter(String paramName, T paramValue)
    {
        this.mParamName = paramName;
        this.mParamValue = paramValue;
    }

    public String getParamName()
    {
        return this.mParamName;
    }

    public T getParamValue()
    {
        return this.mParamValue;
    }

    public void setParamValue(T newValue)
    {
        this.mParamValue = newValue;
    }
}
