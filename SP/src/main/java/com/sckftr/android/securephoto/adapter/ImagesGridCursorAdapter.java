package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.image.CryptoBitmapLoader;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.UI;

import by.mcreader.imageloader.callback.ImageLoaderCallback;

public class ImagesGridCursorAdapter extends BaseCursorAdapter implements ImageLoaderCallback {

    private final int imageSize;

    private final CryptoBitmapLoader mCryptoLoader = new CryptoBitmapLoader();

    private ProgressBar mProgressBar;

    public ImagesGridCursorAdapter(Context context) {
        super(context, null, false);

        imageSize = Math.round(context.getResources().getDimension(R.dimen.column_width));
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        mProgressBar = (ProgressBar) view.findViewById(android.R.id.progress);

        Bundle params = new Bundle(context.getClassLoader());

        params.putString(AppConst.EXTRA.IMAGE, CursorUtils.getString(Contracts.ImageContract.KEY, cursor));
        params.putInt(AppConst.EXTRA.ORIENTATION, CursorUtils.getInteger(Contracts.ImageContract.ORIENTATION, cursor));

        UI.displayImage((ImageView) view.findViewById(R.id.image_view_grid), CursorUtils.getString(Contracts.ImageContract.URI, cursor), imageSize, imageSize, params, this, mCryptoLoader);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return View.inflate(context, R.layout.image_item, null);
    }

    @Override
    public void onLoadingStarted(String url) {
        // no need to implement
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
