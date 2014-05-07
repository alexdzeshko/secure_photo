package com.sckftr.android.securephoto.db;

import android.net.Uri;
import android.os.Parcelable;

public interface Cryptonite extends Parcelable{

    public String getKey();

    public Uri getFileUri();
}
