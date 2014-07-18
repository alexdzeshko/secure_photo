package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sckftr.android.app.adapter.CursorFragmentPagerAdapter;
import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import by.deniotokiari.core.helpers.CursorHelper;

@EFragment
public class ImagePagerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_POSITION = "KEY_POSITION";

    private ImagePagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private Drawable mWindowDrawable;

    @FragmentArg
    int position;

    @AfterViews
    void onAfterViews() {

        View view = getView();

        if (view != null) {
            viewPager = (ViewPager) view.findViewById(R.id.pager);

            pagerAdapter = new ImagePagerAdapter(getContext(), getFragmentManager(), null);

            viewPager.setAdapter(pagerAdapter);

            // TODO
            viewPager.setBackgroundDrawable(new ColorDrawable(R.color.background_alpha_black));

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

        return API.data().getImagesCursorLoader(getContext());

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        pagerAdapter.swapCursor(data);

        viewPager.setCurrentItem(position, false);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public static ImagePagerFragment build(int position) {

        return ImagePagerFragment_.builder().position(position).build();

    }

    class ImagePagerAdapter extends CursorFragmentPagerAdapter {


        public ImagePagerAdapter(Context context, FragmentManager fm, Cursor cursor) {
            super(context, fm, cursor);
        }

        @Override
        public Fragment getItem(Context context, Cursor cursor) {

            final String[] strings = {CursorHelper.getString(cursor, Contracts.ImageContract.URI), CursorHelper.getString(cursor, Contracts.ImageContract.KEY)};

            Fragment fragment = new ImageFragment();

            Bundle bundle = new Bundle();

            bundle.putStringArray(ImageFragment.KEY_ARG_VALUE, strings);

            fragment.setArguments(bundle);

            return fragment;
        }
    }
}
