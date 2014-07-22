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

        // PLUGINS

        AppConst.API.init(this);

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();

        mSuperImageLoader = new SuperImageLoader.ImageLoaderBuilder(this)
                .setLoadingImage(R.drawable.ic_blue_lock)
                .enableFadeIn(false)
                .setDiscCacheEnabled(false)
                .setMemoryCacheEnabled(true)
                .setMemoryCacheSize((memoryClass * 1024 * 1024) / 4) // 0.25 of memory
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
