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

    private final Context mContext;

    public CryptoBitmapLoader(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap loadBitmap(String uri, int width, int height) throws IOException {

        Bundle params = getParams();

        if (params == null) return null;

        String key = params.getString(BUNDLE_KEY);

        if (Strings.isEmpty(uri) || Strings.isEmpty(key)) {
            // TODO
            return null;

        }

        FileInputStream stream = null;

        try {

            stream = new FileInputStream(uri);

            byte[] buffer = new byte[stream.available()];

            stream.read(buffer);

            byte[] decrypted = Cryptograph.decrypt(buffer, key);

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);

            options.inJustDecodeBounds = false;

            options.inSampleSize = ImageHelper.calculateInSampleSize(options, width, height);

            if (options.outMimeType != null && !options.outMimeType.equals(GIF)) {

                addInBitmapOptions(options);

            }

            options.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);

            if (bitmap != null) {

                AppConst.Log.d(TAG, "out width = %s, out height = %s, inSampleSize=%s", bitmap.getWidth(), bitmap.getHeight(), options.inSampleSize);

            }

            return bitmap;

        } catch (FileNotFoundException e) {

            AppConst.Log.e(TAG, uri, e);

        } finally {

            IO.close(stream);

        }

        return null;
    }

    private static void addInBitmapOptions(BitmapFactory.Options options) {

        CacheHelper cache = SuperImageLoader.getCacheHelper();

        options.inMutable = true;

        if (cache != null) {

            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {

                Log.d(TAG, "Found bitmap to use for inBitmap");

                options.inBitmap = inBitmap;

            }
        }
    }

}
