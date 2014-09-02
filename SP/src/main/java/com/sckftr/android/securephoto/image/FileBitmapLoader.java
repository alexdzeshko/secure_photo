package com.sckftr.android.securephoto.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sckftr.android.securephoto.AppConst;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import by.mcreader.imageloader.BaseBitmapLoader;
import by.mcreader.imageloader.utils.BitmapAnalizer;
import by.mcreader.imageloader.utils.BitmapReformer;

/**
 * Created by dzianis_roi on 23.07.2014.
 */
public class FileBitmapLoader extends BaseBitmapLoader<FileInputStream> {

    private static final String TAG = FileBitmapLoader.class.getSimpleName();

    @Override
    protected FileInputStream getSource(String url, BitmapFactory.Options options, Bundle extra) {

        try {

            return FileUtils.openInputStream(new File(url));

        } catch (IOException e) {
            AppConst.Log.d(TAG, "getSource: ", e);
        }

        return null;
    }

    @Override
    protected Bitmap onBitmapReady(String url, Bitmap result, Bundle extra) {
        int degree = BitmapAnalizer.analizRotationDegree(url);

        return degree > 0 ? BitmapReformer.rotate(result, degree) : result;
    }

}
