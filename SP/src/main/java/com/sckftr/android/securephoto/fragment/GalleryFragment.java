package com.sckftr.android.securephoto.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.View;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.GalleryAdapter;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 19.08.2014.
 */
@EFragment
public class GalleryFragment extends ImageGridFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle(R.string.gallery);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) {

            MainActivity mainActivity = (MainActivity) getBaseActivity();

            mainActivity.showAddMenuItem(false);

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        API.images().setPauseWork(false);

        if (!isDetached()) {

            MainActivity mainActivity = (MainActivity) getBaseActivity();

            mainActivity.showAddMenuItem(true);

            mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected GalleryAdapter createAdapter() {
        return new GalleryAdapter(getActivity());
    }

    @Override
    Loader<Cursor> getCursorLoader() {
        return API.data().getGalleryImagesCursorLoader(getActivity());
    }

    @Override
    boolean isPhotosSecured() {
        return false;
    }

    @Click
    void camera() {

        if (!isDetached())
            ((MainActivity) getBaseActivity()).secureNewPhotos(getAdapterView().getCheckedItemPositions(), (Cursor) getAdapter().getItem(0));

    }

    public static GalleryFragment build() {
        return GalleryFragment_.builder().build();
    }
}
