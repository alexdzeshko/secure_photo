package com.sckftr.android.utils;

/**
 * The concept of "Executor".
 *
* @author Aliaksandr_Litskevic
* @created 1/23/14.
 *
*/
public interface Executor<In, Out> {
    public void perform(In input, Object... params);
    public Out getResult();
}
