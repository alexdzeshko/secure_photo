package com.sckftr.android.securephoto.utils;

/**
 * @author Aliaksandr_Litskevic
 * @created 1/27/14.
 */
public interface HasValue<V> {
    void setValue(V value);
    V getValue();
}
