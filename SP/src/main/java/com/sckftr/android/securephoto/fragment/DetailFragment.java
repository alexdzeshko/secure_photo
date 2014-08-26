package com.sckftr.android.securephoto.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.DetailActivity;
import com.sckftr.android.securephoto.adapter.ViewPagerFragmentAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_detail)
public class DetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, ViewPager.OnPageChangeListener {

    public static final String TAG = "DetailFragment";

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

        return API.data().getEncryptedImagesCursorLoader(getContext());

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
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        mCurrentPosition = i;

        ((DetailActivity) getBaseActivity()).resetDelay();
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public static DetailFragment build(int position) {
        return DetailFragment_.builder().position(position).build();
    }
}
