package com.sckftr.android.securephoto.db.base.content;

import com.sckftr.android.securephoto.db.base.annotations.db.DBAutoincrement;
import com.sckftr.android.securephoto.db.base.annotations.db.DBPrimaryKey;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBInteger;

public interface BaseContract {

    @DBAutoincrement
    @DBInteger
    @DBPrimaryKey
    public static final String _ID = "_id";

}
