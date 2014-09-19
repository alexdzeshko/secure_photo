package com.sckftr.android.securephoto.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.image.CryptoBitmapLoader;
import com.sckftr.android.utils.UI;
import com.sckftr.android.utils.UiUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import by.mcreader.imageloader.callback.ImageLoaderCallback;
import by.mcreader.imageloader.view.RecyclingImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

@EFragment(R.layout.view_pager_item)
public class ViewPagerItemFragment extends BaseFragment implements ViewTreeObserver.OnGlobalLayoutListener, ImageLoaderCallback {

    @FragmentArg
    Image image;

    @ViewById(R.id.imageView)
    RecyclingImageView mImageView;

    private final CryptoBitmapLoader mCryptoLoader = new CryptoBitmapLoader();
    private PhotoViewAttacher mAttacher;

    @AfterViews
    void onAfterViews() {

        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mAttacher = new PhotoViewAttacher(mImageView);

    }

    @Override
    public void onGlobalLayout() {
        Bundle params = new Bundle();

        params.putString(EXTRA.IMAGE, image.getKey());
        params.putInt(EXTRA.ORIENTATION, image.getOrientation());

        UI.displayImage(mImageView, image.getFileUri().toString(), mImageView.getWidth(), mImageView.getHeight(), params, this, mCryptoLoader);

        UiUtil.removeOnGlobalLayoutListener(mImageView, this);
    }

    public static ViewPagerItemFragment build(Image image) {
        return ViewPagerItemFragment_.builder().image(image).build();
    }

    @Override public void onLoadingStarted(String url) {
    }

    @Override public void onLoadingError(Exception e, String url) {
    }

    @Override public void onLoadingFinished(BitmapDrawable drawable) {
        mAttacher.update();
        mImageView.setBackgroundResource(0);
    }
}
