package com.sckftr.android.securephoto.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.adapter.ViewPagerFragmentAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ImagePagerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPagerFragmentAdapter pagerAdapter;
    private ViewPager viewPager;

    private Drawable mWindowDrawable;

    @FragmentArg
    int position;

    @FragmentArg
    boolean systemGallery;

    @AfterViews
    void onAfterViews() {

        View view = getView();

        if (view != null) {

            viewPager = (ViewPager) view.findViewById(R.id.pager);

            pagerAdapter = new ViewPagerFragmentAdapter(getContext(), getFragmentManager(), null, systemGallery);

            viewPager.setAdapter(pagerAdapter);

            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        mWindowDrawable = mWindowDrawable == null ? getActivity().getWindow().getDecorView().getBackground() : mWindowDrawable;

        getActivity().getWindow().setBackgroundDrawable(null);

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getWindow().setBackgroundDrawable(mWindowDrawable);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return systemGallery ? API.data().getGalleryImagesCursorLoader(getActivity()) : API.data().getEncryptedImagesCursorLoader(getContext());

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        pagerAdapter.swapCursor(data);

        viewPager.setCurrentItem(position, false);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pagerAdapter.swapCursor(null);
    }

    public static ImagePagerFragment build(int position, boolean systemGallery) {
        return ImagePagerFragment_.builder().position(position).systemGallery(systemGallery).build();
    }
}
