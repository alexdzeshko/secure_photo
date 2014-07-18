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
import com.sckftr.android.securephoto.data.DataApi;
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

        DataApi.images().start(
                imageView, // target ImageView
                progressBar, // progress to show
                CursorHelper.getString(cursor, Contracts.ImageContract.URI), // image uri
                CursorHelper.getString(cursor, Contracts.ImageContract.KEY)); // decrypt key);

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
