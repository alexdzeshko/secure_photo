package com.sckftr.android.securephoto.contract;

import com.sckftr.android.securephoto.AppConst;

import by.deniotokiari.core.annotations.ContractInfo;
import by.deniotokiari.core.annotations.db.DBContract;
import by.deniotokiari.core.annotations.db.DBTableName;
import by.deniotokiari.core.annotations.db.types.DBVarchar;
import by.deniotokiari.core.content.CoreContract;

public class Contracts implements CoreContract{

    private static final String TYPE = "vnd.android.cursor.dir/";

    private static final String URI = "content://"+ AppConst.PACKAGE+".provider.";

    @DBContract
    @DBTableName(tableName = "IMAGES")
    @ContractInfo(type = TYPE + "IMAGES", uri = URI + "ImagesProvider/GENRES")
    public static final class ImageContract implements CoreContract {

        @DBVarchar
        public static final String URI = "URI";

        @DBVarchar
        public static final String KEY = "KEY";

    }

}
