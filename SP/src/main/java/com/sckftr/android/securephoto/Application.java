package com.sckftr.android.securephoto;

import android.app.ActivityManager;
import android.content.Context;

import com.sckftr.android.securephoto.image.CryptoBitmapSourceLoader;

import org.androidannotations.annotations.EApplication;

import by.grsu.mcreader.mcrimageloader.imageloader.SuperImageLoader;

@EApplication
public class Application extends android.app.Application {

    private static SuperImageLoader mSuperImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        AppConst.API.init(this);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mSuperImageLoader = new SuperImageLoader.ImageLoaderBuilder(this)
                .setLoadingImage(R.drawable.ic_blue_lock)
                .enableFadeIn(false)
                .setDiscCacheEnabled(false)
                .setMemoryCacheEnabled(true)
                .setMemoryCacheSize(cacheSize) // 0.25 of memory
                .setCustomLoader(new CryptoBitmapSourceLoader())
                .build();
    }

    public static SuperImageLoader getImageLoader() {
        return mSuperImageLoader;
    }

    public static Application get() {
        return Application_.getInstance();
    }
}
