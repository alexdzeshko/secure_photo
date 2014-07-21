package com.sckftr.android.securephoto.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by dzianis_roi on 21.07.2014.
 */
public class AsyncBitmapDrawable extends BitmapDrawable {

    private WeakReference<SimpleImageLoader.ImageAsyncTask> mDrawableTaskReference;

    public AsyncBitmapDrawable(Resources resources, Bitmap loadingBitmap, SimpleImageLoader.ImageAsyncTask loaderTask) {
        super(resources, loadingBitmap);

        mDrawableTaskReference = new WeakReference<SimpleImageLoader.ImageAsyncTask>(loaderTask);
    }

    public SimpleImageLoader.ImageAsyncTask getLoaderTask() {
        return mDrawableTaskReference.get();
    }
}
