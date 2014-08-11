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
import com.sckftr.android.utils.UI;

import by.deniotokiari.core.helpers.CursorHelper;
import by.grsu.mcreader.mcrimageloader.imageloader.callback.ImageLoaderCallback;

public class ImagesGridCursorAdapter extends BaseCursorAdapter {

    private final int imageSize;

    public ImagesGridCursorAdapter(Context context) {
        super(context, null, false);

        imageSize = Math.round(context.getResources().getDimension(R.dimen.column_width));
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_grid);

        Bundle params = new Bundle(context.getClassLoader());
        params.putString(AppConst.EXTRA.IMAGE, CursorHelper.getString(cursor, Contracts.ImageContract.KEY));

        UI.displayImage(imageView, CursorHelper.getString(cursor, Contracts.ImageContract.URI), imageSize, imageSize, params, null);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return View.inflate(context, R.layout.image_item, null);

    }
}
