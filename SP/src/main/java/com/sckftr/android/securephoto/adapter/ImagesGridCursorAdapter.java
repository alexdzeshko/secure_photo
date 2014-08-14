package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.UI;

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

        params.putString(AppConst.EXTRA.IMAGE, CursorUtils.getString(Contracts.ImageContract.KEY, cursor));
        params.putInt(AppConst.EXTRA.ORIENTATION, CursorUtils.getInteger(Contracts.ImageContract.ORIENTATION, cursor));

        AppConst.Log.d("TAG", CursorUtils.getInteger(Contracts.ImageContract.ORIENTATION, cursor) + "");

        UI.displayImage(imageView, CursorUtils.getString(Contracts.ImageContract.URI, cursor), imageSize, imageSize, params, null);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return View.inflate(context, R.layout.image_item, null);
    }
}
