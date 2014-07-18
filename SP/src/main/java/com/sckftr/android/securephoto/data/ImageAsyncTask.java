package com.sckftr.android.securephoto.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.ImageHelper;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.AQuery;
import com.sckftr.android.utils.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;
import uk.co.senab.bitmapcache.CacheableImageView;

/**
 * Created by dzianis_roi on 18.07.2014.
 */
public class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    public static final String TAG = ImageAsyncTask.class.getSimpleName();

    private CacheableImageView mImageView;

    AQuery aq;

    public void start(CacheableImageView imageView, ProgressBar progressBar, String uri, String key) {

        mImageView = imageView;

        aq = new AQuery(progressBar);

        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri, key);
    }

    @Override
    protected void onPreExecute() {

        aq.display(true);

        mImageView.setImageResource(R.drawable.placeholder);

    }

    @Override
    protected Bitmap doInBackground(String... params) {

        if (mImageView == null || aq == null) {

            throw new IllegalArgumentException("Can't execute AsyncTask, because ImageView or AQuery is null!");

        }

        String url = params[0];

        String key = params[1];

        final BitmapLruCache bitmapCache = Application.get().getBitmapCache();
        CacheableBitmapDrawable result = bitmapCache.get(url);

        FileInputStream stream = null;

        if (result != null) {

            AppConst.Log.d(TAG, "from cache: " + url);

            return result.getBitmap();

        }

        try {

            stream = new FileInputStream(url);

            byte[] buffer = new byte[stream.available()];

            stream.read(buffer);

            byte[] decrypted = Cryptograph.decrypt(buffer, key);

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);

            options.inJustDecodeBounds = false;

            options.inSampleSize = ImageHelper.calculateInSampleSize(options, mImageView.getWidth(), mImageView.getHeight());

            options.inPurgeable = true;
            options.inMutable = true;

            Bitmap bitmap = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);

            AppConst.Log.d(TAG, "out width = %s, out height = %s, inSampleSize=%s", bitmap.getWidth(), bitmap.getHeight(), options.inSampleSize);

            bitmapCache.put(url, bitmap);

            return bitmap;

        } catch (FileNotFoundException e) {

            AppConst.Log.e(TAG, url, e);

        } catch (IOException e) {

            AppConst.Log.e(TAG, url, e);

        } finally {

            IO.close(stream);

        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        aq.display(false);

        if (bitmap != null) {

            mImageView.setImageBitmap(bitmap);

        }
    }
}
