package com.sckftr.android.securephoto.db;

import android.os.Parcelable;

public interface Cryptonite extends Parcelable{

    String key = null, uri = null;

    public String getKey();

    public String getUri();
}
