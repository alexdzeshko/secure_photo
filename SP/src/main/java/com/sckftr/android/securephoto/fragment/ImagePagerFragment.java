package com.sckftr.android.securephoto.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
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
import org.androidannotations.annotations.ViewById;

@EFragment
public class ImagePagerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, ViewPager.OnPageChangeListener {

    private static final String EXTRA_POSITION = "com.sckftr.android.securephoto.fragment.EXTRA_POSITION";

    @ViewById
    ViewPager pager;

    @FragmentArg
    int position;

    @FragmentArg
    boolean systemGallery;

    private ViewPagerFragmentAdapter pagerAdapter;

    private int mCurrentPosition;

    @AfterViews
    void onAfterViews() {

        pagerAdapter = new ViewPagerFragmentAdapter(getContext(), getFragmentManager(), null);

        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(this);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {

        super.onResume();

        getActivity().getWindow().setBackgroundDrawable(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getActivityParams().putInt(EXTRA_POSITION, mCurrentPosition);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return systemGallery ? API.data().getGalleryImagesCursorLoader(getActivity()) : API.data().getEncryptedImagesCursorLoader(getContext());

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pagerAdapter.swapCursor(data);

        pager.setCurrentItem(getActivityParams().getInt(EXTRA_POSITION, position), false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void populateInsets(Rect insets) {
        super.populateInsets(insets);

        // TODO
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        mCurrentPosition = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public static ImagePagerFragment build(int position, boolean systemGallery) {
        return ImagePagerFragment_.builder().position(position).systemGallery(systemGallery).build();
    }
}
