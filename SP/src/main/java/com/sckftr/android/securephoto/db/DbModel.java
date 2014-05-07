package com.sckftr.android.securephoto.db;

import android.net.Uri;
import android.os.Parcelable;

public interface DbModel extends Parcelable{

    public String get_id();

    public Uri getContentUri();

}
