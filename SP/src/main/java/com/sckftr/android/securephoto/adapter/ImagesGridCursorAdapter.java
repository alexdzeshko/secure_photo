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

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.processor.Crypto;

import java.io.FileInputStream;

import by.deniotokiari.core.adapter.ViewHolder;
import by.deniotokiari.core.adapter.cursor.BaseCursorAdapter;
import by.deniotokiari.core.helpers.CursorHelper;
import by.deniotokiari.core.utils.IOUtils;

public class ImagesGridCursorAdapter extends BaseCursorAdapter {

    public static final int RES_LAYOUT = R.layout.adapter_photos;
    public static final int RES_ID_PHOTO = R.id.image_view_grid;
    public static final int RES_ID_PROGRESS = R.id.progress_bar_grid;

    public ImagesGridCursorAdapter(Context context) {
        super(context, null, true);

    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor, ViewHolder holder) {
        final String uri = CursorHelper.get(cursor, Contracts.ImageContract.URI);
        final String key = CursorHelper.get(cursor, Contracts.ImageContract.KEY);

        final ImageView imageView = (ImageView) holder.getViewById(RES_ID_PHOTO);
        final ProgressBar progressBar = (ProgressBar) holder.getViewById(RES_ID_PROGRESS);

        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                FileInputStream stream = null;
                try {
                    stream = new FileInputStream(params[0]);
                    byte[] buffer = new byte[stream.available()];
                    stream.read(buffer);
                    byte[] decr = Crypto.decrypt(buffer, params[1]);

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    //TODO need calculation
                    opts.inSampleSize = 5;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(decr, 0, decr.length, opts);
                    return bitmap;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeStream(stream);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);

                if (bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute(uri, key);

    }

    @Override
    protected int[] getViewsIds() {
        return new int[]{RES_ID_PHOTO, RES_ID_PROGRESS};
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return View.inflate(context, RES_LAYOUT, null);
    }
}
