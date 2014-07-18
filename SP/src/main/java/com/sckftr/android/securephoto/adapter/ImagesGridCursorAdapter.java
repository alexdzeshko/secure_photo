package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.helper.ImageHelper;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import by.deniotokiari.core.adapter.ViewHolder;
import by.deniotokiari.core.adapter.cursor.BaseCursorAdapter;
import by.deniotokiari.core.helpers.CursorHelper;
import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;
import uk.co.senab.bitmapcache.CacheableImageView;

public class ImagesGridCursorAdapter extends BaseCursorAdapter {

    public static final int RES_LAYOUT = R.layout.image_item;
    public static final int RES_ID_PHOTO = R.id.image_view_grid;
    public static final int RES_ID_PROGRESS = R.id.progress_bar_grid;

    public ImagesGridCursorAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor, ViewHolder holder) {

        final CacheableImageView imageView = (CacheableImageView) holder.getViewById(RES_ID_PHOTO);
        final ProgressBar progressBar = (ProgressBar) holder.getViewById(RES_ID_PROGRESS);

        new ImageAsyncTask(imageView, progressBar).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, // executor type
                CursorHelper.getString(cursor, Contracts.ImageContract.URI), // image uri
                CursorHelper.getString(cursor, Contracts.ImageContract.KEY)); // decrypt key
    }

    @Override
    protected int[] getViewsIds() {
        return new int[]{RES_ID_PHOTO, RES_ID_PROGRESS};
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return View.inflate(context, RES_LAYOUT, null);
    }

    private static class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        public static final String TAG = ImageAsyncTask.class.getSimpleName();

        private final CacheableImageView mImageView;
        private final ProgressBar mProgressBar;

        private ImageAsyncTask(CacheableImageView imageView, ProgressBar progressBar) {

            mImageView = imageView;

            mProgressBar = progressBar;

        }

        @Override
        protected void onPreExecute() {

            mProgressBar.setVisibility(View.VISIBLE);

            mImageView.setImageResource(R.drawable.placeholder);

        }

        @Override
        protected Bitmap doInBackground(String... params) {

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

                byte[] decr = Cryptograph.decrypt(buffer, key);

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inJustDecodeBounds = true;

                BitmapFactory.decodeByteArray(decr, 0, decr.length, options);

                options.inJustDecodeBounds = false;

                options.inSampleSize = ImageHelper.calculateInSampleSize(options, mImageView.getWidth(), mImageView.getHeight());

                options.inPurgeable = true;
                options.inMutable = true;

                Bitmap bitmap = BitmapFactory.decodeByteArray(decr, 0, decr.length, options);

                AppConst.Log.d(TAG, "inSampleSize=%s", options.inSampleSize);

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

            mProgressBar.setVisibility(View.GONE);

            if (bitmap != null) {

                mImageView.setImageBitmap(bitmap);

            }
        }
    }
}
