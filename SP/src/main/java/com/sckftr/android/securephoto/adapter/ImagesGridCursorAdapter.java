package com.sckftr.android.securephoto.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.data.DataApi;

import by.deniotokiari.core.helpers.CursorHelper;
import uk.co.senab.bitmapcache.CacheableImageView;

public class ImagesGridCursorAdapter extends BaseCursorAdapter {

    public ImagesGridCursorAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    protected void bindData(View view, Context context, Cursor cursor) {

        final CacheableImageView imageView = (CacheableImageView) view.findViewById(R.id.image_view_grid);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_grid);


        DataApi.images(context).loadBitmap(imageView, "http://cdn.androidbeat.com/wp-content/uploads/2013/08/Paranoid-Android-Chat-Heads.jpg");


//                imageView, // target ImageView
//                progressBar, // progress to show
//                CursorHelper.getString(cursor, Contracts.ImageContract.URI), // image uri
//                CursorHelper.getString(cursor, Contracts.ImageContract.KEY),// decrypt key
//                false); // work with cache
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return View.inflate(context, R.layout.image_item, null);
    }
}
