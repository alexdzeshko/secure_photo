package com.luminous.pick;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.utils.UI;

import by.deniotokiari.core.helpers.CursorHelper;
import by.grsu.mcreader.mcrimageloader.imageloader.callback.ImageLoaderCallback;
import uk.co.senab.bitmapcache.CacheableImageView;

public class GalleryAdapter extends BaseCursorAdapter {

    private static final String LOG_TAG = GalleryAdapter.class.getSimpleName();

    private final int imageSize;

    public GalleryAdapter(Context ctx) {
        super(ctx, null, false);

        imageSize = Math.round(ctx.getResources().getDimension(R.dimen.column_width));
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_grid);
//        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_grid);

        UI.displayImage(imageView, CursorHelper.getString(cursor, MediaStore.Images.Media.DATA), imageSize, imageSize, null);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return View.inflate(context, R.layout.image_item, null);

    }
}
