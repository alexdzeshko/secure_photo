package com.sckftr.android.securephoto.image;

import android.graphics.BitmapFactory;

import com.sckftr.android.securephoto.AppConst;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import by.grsu.mcreader.mcrimageloader.imageloader.BaseBitmapSourceLoader;

/**
 * Created by dzianis_roi on 23.07.2014.
 */
public class FileBitmapSourceLoader extends BaseBitmapSourceLoader<FileInputStream> {

    private static final String TAG = FileBitmapSourceLoader.class.getSimpleName();

    @Override
    protected FileInputStream getSource(String url, BitmapFactory.Options options) {

        try {
            return new FileInputStream(url);
        } catch (FileNotFoundException e) {
            AppConst.Log.d(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected int getRotationDegree(String url) {
        return defineRotationDegree(url);
    }
}
