package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.UI;

public class GalleryAdapter extends BaseCursorAdapter {

    private final int imageSize;

    public GalleryAdapter(Context ctx) {
        super(ctx, null, false);

        imageSize = Math.round(ctx.getResources().getDimension(R.dimen.column_width));
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_grid);

        UI.displayImage(imageView, CursorUtils.getString(MediaStore.Images.Media.DATA, cursor), imageSize, imageSize, null);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return View.inflate(context, R.layout.image_item, null);

    }
}
