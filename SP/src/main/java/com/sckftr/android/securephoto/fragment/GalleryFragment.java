package com.sckftr.android.securephoto.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.GalleryAdapter;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 19.08.2014.
 */
@EFragment
public class GalleryFragment extends ImageGridFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aq.id(R.id.camera).gone();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) {

            MainActivity mainActivity = (MainActivity) getBaseActivity();

            mainActivity.toggleSourceLoader(false);

            mainActivity.showAddMenuItem(false);

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isDetached()) {

            MainActivity mainActivity = (MainActivity) getBaseActivity();

            mainActivity.showAddMenuItem(true);

            API.images().setPauseWork(false);

            getActionBar().setDisplayHomeAsUpEnabled(false);
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
    public void onDestroyActionMode(ActionMode mode) {

        if (actionList != null) {
            ((MainActivity) getBaseActivity()).secureNewPhotos(actionList, (Cursor) getAdapter().getItem(0));
        }

        super.onDestroyActionMode(mode);
    }

    public static GalleryFragment build() {
        return GalleryFragment_.builder().build();
    }
}
