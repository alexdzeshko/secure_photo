package com.sckftr.android.securephoto.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.utils.UI;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import by.grsu.mcreader.mcrimageloader.imageloader.callback.ImageLoaderCallback;
import uk.co.senab.photoview.PhotoViewAttacher;

@EFragment(R.layout.view_pager_item)
public class ViewPagerItemFragment extends BaseFragment {

    @FragmentArg
    String url, key;

    @AfterViews
    void onAfterViews() {

        View view = getView();

        if (view != null) {

            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);

            Bundle params = new Bundle(getBaseActivity().getClassLoader());
            params.putString(EXTRA.IMAGE, key);

            UI.displayImage(mImageView, url, mImageView.getWidth(), mImageView.getHeight(), params, new ImageLoaderCallback() {
                @Override
                public void onLoadingStarted(String url) {

                }

                @Override
                public void onLoadingError(Exception e, String url) {

                }

                @Override
                public void onLoadingFinished(BitmapDrawable drawable) {

                }
            });

            new PhotoViewAttacher(mImageView).update();
        }
    }

    public static ViewPagerItemFragment build(String url, String key) {

        return ViewPagerItemFragment_.builder().url(url).key(key).build();

    }

}
