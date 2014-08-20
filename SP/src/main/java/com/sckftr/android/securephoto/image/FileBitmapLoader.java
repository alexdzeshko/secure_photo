package com.sckftr.android.securephoto.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sckftr.android.securephoto.AppConst;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import by.grsu.mcreader.mcrimageloader.imageloader.BaseBitmapLoader;
import by.grsu.mcreader.mcrimageloader.imageloader.utils.BitmapAnalizer;
import by.grsu.mcreader.mcrimageloader.imageloader.utils.BitmapReformer;

/**
 * Created by dzianis_roi on 23.07.2014.
 */
public class FileBitmapLoader extends BaseBitmapLoader<FileInputStream> {

    private static final String TAG = FileBitmapLoader.class.getSimpleName();

    @Override
    protected FileInputStream getSource(String url, BitmapFactory.Options options, Bundle extra) {

        try {
            return new FileInputStream(url);
        } catch (FileNotFoundException e) {
            AppConst.Log.d(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected Bitmap onBitmapReady(String url, Bitmap result, Bundle extra) {
        int degree = BitmapAnalizer.analizRotationDegree(url);

        return degree > 0 ? BitmapReformer.rotate(result, degree) : result;
    }

}
