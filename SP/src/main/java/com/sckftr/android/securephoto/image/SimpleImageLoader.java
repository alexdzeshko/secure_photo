package com.sckftr.android.securephoto.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.ImageHelper;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.AQuery;
import com.sckftr.android.utils.IO;
import com.sckftr.android.utils.Strings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;

/**
 * Created by dzianis_roi on 21.07.2014.
 */
public class SimpleImageLoader {

    private static final String LOG_TAG = SimpleImageLoader.class.getSimpleName();

    private static Bitmap mPlaceholderBitmap;
    private static Resources mResources;

    private static SimpleImageLoader instance;

    private SimpleImageLoader(Resources resources) {

        mResources = resources;

        mPlaceholderBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_blue_lock); // TODO config

    }

    public static SimpleImageLoader getInstance(Resources resources) {
        if (instance == null) {
            instance = new SimpleImageLoader(resources);
        }

        return instance;
    }

    public static void loadImage(ImageView imageView, ProgressBar progressBar, String uri, String key, boolean getFromCache) {

        if (Strings.isEmpty(uri)) {

            AppConst.Log.e(LOG_TAG, "empty or null url");

            return;
        }

        if (cancelPotentialDownload(imageView, uri)) {

            ImageAsyncTask bitmapAsyncTask = new ImageAsyncTask();

            AsyncBitmapDrawable asyncbitmapDrawable = new AsyncBitmapDrawable(mResources, mPlaceholderBitmap, bitmapAsyncTask);

            imageView.setImageDrawable(asyncbitmapDrawable);

            bitmapAsyncTask.start(imageView, progressBar, uri, key, getFromCache);

        }
    }


    private static boolean cancelPotentialDownload(ImageView imageView, String url) {

        ImageAsyncTask bitmapAsyncTask = getImageLoaderTask(imageView);

        if (bitmapAsyncTask != null) {

            String bitmapUrl = bitmapAsyncTask.mUri;

            if (Strings.isEmpty(url) || !bitmapUrl.equals(url)) {

                bitmapAsyncTask.cancel(true);

                AppConst.Log.d("ImagesGridCursorAdapter", "cancelPotentialDownload for " + url);

            } else {

                return false;

            }
        }

        return true;
    }

    private static ImageAsyncTask getImageLoaderTask(ImageView imageView) {

        final Drawable drawable = imageView == null ? null : imageView.getDrawable();

        return drawable instanceof AsyncBitmapDrawable ? ((AsyncBitmapDrawable) drawable).getLoaderTask() : null;

    }

    public static class ImageAsyncTask extends AsyncTask<String, Void, BitmapDrawable> {

        public static final String TAG = ImageAsyncTask.class.getSimpleName();

        private WeakReference<ImageView> mImageViewReference;

        private int mWidth, mHeight;

        private boolean mGetFromCache;

        protected String mUri;

        AQuery aq;


        public void start(ImageView imageView, ProgressBar progressBar, String uri, String key, boolean getFromCache) {

            mWidth = imageView.getWidth();
            mHeight = imageView.getHeight();

            mImageViewReference = new WeakReference<ImageView>(imageView);

            aq = new AQuery(progressBar);

            mUri = uri;

            mGetFromCache = getFromCache; // TODO fix this

            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri, key);
        }

        @Override
        protected void onPreExecute() {

            aq.display(true);

        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {

            String url = params[0];

            String key = params[1];

            BitmapLruCache bitmapCache = null;

            if (mGetFromCache) {

                bitmapCache = Application.get().getBitmapCache();
                CacheableBitmapDrawable result = bitmapCache.get(url);

                if (result != null) {

                    AppConst.Log.d(TAG, "from cache: " + url);

                    return result;

                }
            }

            FileInputStream stream = null;

            try {

                stream = new FileInputStream(url);

                byte[] buffer = new byte[stream.available()];

                stream.read(buffer);

                byte[] decrypted = Cryptograph.decrypt(buffer, key);

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inJustDecodeBounds = true;

                BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);

                options.inJustDecodeBounds = false;

                options.inSampleSize = ImageHelper.calculateInSampleSize(options, mWidth, mHeight);

                options.inPurgeable = true;
                options.inMutable = true;

                Bitmap bitmap = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);

                AppConst.Log.d(TAG, "out width = %s, out height = %s, inSampleSize=%s", bitmap.getWidth(), bitmap.getHeight(), options.inSampleSize);

                if (bitmapCache != null) {

                    bitmapCache.put(url, bitmap);

                }

                return new BitmapDrawable(mResources, bitmap);

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
        protected void onPostExecute(BitmapDrawable bitmap) {

            bitmap = isCancelled() ? null : bitmap;

            if (mImageViewReference != null) {

                ImageView imageView = mImageViewReference.get();

                // Change bitmap only if this process is still associated with it
                if (this == getImageLoaderTask(imageView)) {

                    if (imageView != null && bitmap != null) {

                        imageView.setImageDrawable(bitmap);

                    }
                }
            }

            aq.display(false);
        }

    }
}