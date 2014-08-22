package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.image.FileBitmapLoader;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.UI;

import by.mcreader.imageloader.callback.ImageLoaderCallback;

public class GalleryAdapter extends BaseCursorAdapter implements ImageLoaderCallback {

    private final int imageSize;

    private FileBitmapLoader mFileLoader = new FileBitmapLoader();

    private ProgressBar mProgressBar;

    public GalleryAdapter(Context ctx) {
        super(ctx, null, false);

        imageSize = Math.round(ctx.getResources().getDimension(R.dimen.column_width));
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        mProgressBar = (ProgressBar) view.findViewById(android.R.id.progress);

        UI.displayImage((ImageView) view.findViewById(R.id.image_view_grid), CursorUtils.getString(MediaStore.Images.Media.DATA, cursor), imageSize, imageSize, null, this, mFileLoader);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return View.inflate(context, R.layout.image_item, null);

    }

    @Override
    public void onLoadingStarted(String url) {
    }

    @Override
    public void onLoadingError(Exception e, String url) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingFinished(BitmapDrawable drawable) {
        mProgressBar.setVisibility(View.GONE);
    }
}
