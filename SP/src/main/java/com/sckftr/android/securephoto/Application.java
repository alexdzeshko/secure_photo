package com.sckftr.android.securephoto;

import org.androidannotations.annotations.EApplication;

import by.deniotokiari.core.app.CoreApplication;
import uk.co.senab.bitmapcache.BitmapLruCache;

@EApplication
public class Application extends CoreApplication {

    private BitmapLruCache mCache;

    @Override
    public void register() {
        // PLUGINS

        AppConst.API.init(this);

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
        builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
        mCache = builder.build();

    }

    public BitmapLruCache getBitmapCache() {
        return mCache;
    }

    public static Application get() {
        return Application_.getInstance();
    }
}
