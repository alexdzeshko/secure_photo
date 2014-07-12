package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sckftr.android.app.adapter.CursorFragmentPagerAdapter;
import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.contract.Contracts;

import by.deniotokiari.core.helpers.CursorHelper;

public class ImagePagerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String KEY_POSITION = "KEY_POSITION";

    private ImagePagerAdapter pagerAdapter;
    private int position;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            viewPager = (ViewPager) view.findViewById(R.id.pager);
            pagerAdapter = new ImagePagerAdapter(getContext(), getFragmentManager(), null);
            viewPager.setAdapter(pagerAdapter);
            position = getArguments().getInt(KEY_POSITION);

        }
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return API.data().getImagesLoader(getContext());
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pagerAdapter.swapCursor(data);
        viewPager.setCurrentItem(position);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    class ImagePagerAdapter extends CursorFragmentPagerAdapter {


        public ImagePagerAdapter(Context context, FragmentManager fm, Cursor cursor) {
            super(context, fm, cursor);
        }

        @Override public Fragment getItem(Context context, Cursor cursor) {
            final String[] strings = {CursorHelper.get(cursor, Contracts.ImageContract.URI), CursorHelper.get(cursor, Contracts.ImageContract.KEY)};
            Fragment fragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArray(ImageFragment.KEY_ARG_VALUE, strings);
            fragment.setArguments(bundle);
            return fragment;
        }
    }
}
