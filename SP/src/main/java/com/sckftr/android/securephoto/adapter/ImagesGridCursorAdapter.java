package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.AppApi;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.data.DataApi;
import com.sckftr.android.securephoto.image.CryptoBitmapSourceLoader;

import by.deniotokiari.core.helpers.CursorHelper;
import by.grsu.mcreader.mcrimageloader.imageloader.callback.ImageLoaderCallback;
import uk.co.senab.bitmapcache.CacheableImageView;

public class ImagesGridCursorAdapter extends BaseCursorAdapter {

    private static final String LOG_TAG = ImagesGridCursorAdapter.class.getSimpleName();

    public ImagesGridCursorAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        CacheableImageView imageView = (CacheableImageView) view.findViewById(R.id.image_view_grid);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_grid);

        Bundle params = new Bundle(context.getClassLoader());
        params.putString(AppConst.EXTRA.IMAGE, CursorHelper.getString(cursor, Contracts.ImageContract.KEY));

        AppConst.API.images().loadBitmap(imageView, CursorHelper.getString(cursor, Contracts.ImageContract.URI), imageView.getWidth(), imageView.getHeight(), params, new ImageLoaderCallback() {
            @Override
            public void onLoadingStarted(String url) {

            }

            @Override
            public void onLoadingError(Exception e, String url) {

                AppConst.Log.d(LOG_TAG, "Error loading image %s", url);

                // TODO: set error placeholder

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFinished(BitmapDrawable drawable) {

                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return View.inflate(context, R.layout.image_item, null);

    }
}
