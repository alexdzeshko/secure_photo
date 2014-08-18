package com.sckftr.android.securephoto.contract;

import com.sckftr.android.securephoto.AppConst;

import com.sckftr.android.securephoto.db.base.annotations.ContractInfo;
import com.sckftr.android.securephoto.db.base.annotations.db.DBContract;
import com.sckftr.android.securephoto.db.base.annotations.db.DBTableName;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBInteger;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBVarchar;
import com.sckftr.android.securephoto.db.base.content.BaseContract;

public class Contracts implements BaseContract {

    private static final String TYPE = "vnd.android.cursor.dir/";

    private static final String URI = "content://" + AppConst.PACKAGE + ".provider.";

    @DBContract
    @DBTableName(tableName = "IMAGES")
    @ContractInfo(type = TYPE + "IMAGES", uri = URI + "ImagesProvider/IMAGES")
    public static final class ImageContract implements BaseContract {

        @DBVarchar
        public static final String URI = "URI";

        @DBVarchar
        public static final String KEY = "KEY";

        @DBInteger
        public static final String ORIENTATION = "ORIENTATION";

    }

}
