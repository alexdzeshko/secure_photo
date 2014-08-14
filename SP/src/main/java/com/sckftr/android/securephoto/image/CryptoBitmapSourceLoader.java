package com.sckftr.android.securephoto.image;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.processor.Cryptograph;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import by.grsu.mcreader.mcrimageloader.imageloader.BaseBitmapSourceLoader;
import by.grsu.mcreader.mcrimageloader.imageloader.utils.IOUtils;

/**
 * Created by dzianis_roi on 21.07.2014.
 */
public class CryptoBitmapSourceLoader extends BaseBitmapSourceLoader<InputStream> {

    private static final String TAG = CryptoBitmapSourceLoader.class.getSimpleName();

    @Override
    protected InputStream getSource(String url, BitmapFactory.Options options) {

        Bundle params = getParams();

        String key = null;

        Integer orientation = null;

        if (params != null) {

            key = params.getString(AppConst.EXTRA.IMAGE);

            orientation = params.getInt(AppConst.EXTRA.ORIENTATION, 0);

        }

        FileInputStream fis = null;

        try {

            fis = new FileInputStream(url);

            byte[] buffer = new byte[fis.available()];

            fis.read(buffer);

            setRotationDegree(orientation);

            return new ByteArrayInputStream(Cryptograph.decrypt(buffer, key));

        } catch (IOException e) {

            AppConst.Log.e(TAG, url, e);

        } finally {

            IOUtils.closeStream(fis);

        }

        return null;
    }
}
