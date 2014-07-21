package com.sckftr.android.securephoto.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.helper.ImageHelper;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.IO;
import com.sckftr.android.utils.Strings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import by.grsu.mcreader.mcrimageloader.imageloader.BitmapLoader;
import by.grsu.mcreader.mcrimageloader.imageloader.CacheHelper;
import by.grsu.mcreader.mcrimageloader.imageloader.SuperImageLoader;

/**
 * Created by dzianis_roi on 21.07.2014.
 */
public class CryptoBitmapLoader extends BitmapLoader {

    private static final String TAG = CryptoBitmapLoader.class.getSimpleName();

    private static final String GIF = "image/gif";

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

            options.inPurgeable = true;

        } catch (IOException e) {

            AppConst.Log.e(TAG, url, e);

        } finally {

            IO.close(stream);

        }

        return result;
    }

}
