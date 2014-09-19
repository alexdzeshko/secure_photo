package com.sckftr.android.securephoto;

import org.androidannotations.annotations.EApplication;

import by.mcreader.imageloader.SuperImageLoader;

@EApplication
public class Application extends android.app.Application {

    private static SuperImageLoader mSuperImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        AppConst.API.init(this);

        // Use 1/4th of the available memory for this memory cache.
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 4);

        mSuperImageLoader = new SuperImageLoader.ImageLoaderBuilder(this)
                .setPlaceholder(R.drawable.placeholder)
                .enableFadeIn(true)
                .setDiscCacheEnabled(false)
                .setMemoryCacheEnabled(true)
                .setMemoryCacheSize(cacheSize)
                .build();
    }

    public static SuperImageLoader getImageLoader() {
        return mSuperImageLoader;
    }

    public static Application get() {
        return Application_.getInstance();
    }
}
