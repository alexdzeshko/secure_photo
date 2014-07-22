package com.sckftr.android.securephoto.image;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.Strings;

import java.io.FileInputStream;
import java.io.IOException;

import by.grsu.mcreader.mcrimageloader.imageloader.BitmapSourceLoader;
import by.grsu.mcreader.mcrimageloader.imageloader.utils.IOUtils;

/**
 * Created by dzianis_roi on 21.07.2014.
 */
public class CryptoBitmapSourceLoader extends BitmapSourceLoader {

    private static final String TAG = CryptoBitmapSourceLoader.class.getSimpleName();

    public static final String BUNDLE_KEY = "KEY";

    @Override
    protected byte[] getBuffer(String url, int width, int height, BitmapFactory.Options options) {

        Bundle params = getParams();

        String key = params == null ? null : params.getString(BUNDLE_KEY);

        if (Strings.isEmpty(url) || Strings.isEmpty(key)) {

            return null;

        }

        byte[] result = null;

        FileInputStream stream = null;

        try {

            stream = new FileInputStream(url);

            byte[] buffer = new byte[stream.available()];

            stream.read(buffer);

            result = Cryptograph.decrypt(buffer, key);

        } catch (IOException e) {

            AppConst.Log.e(TAG, url, e);

        } finally {

            IOUtils.closeStream(stream);

        }

        return result;
    }

}
