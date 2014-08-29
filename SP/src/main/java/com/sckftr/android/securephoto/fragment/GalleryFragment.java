package com.sckftr.android.securephoto.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.GalleryAdapter;
import com.sckftr.android.securephoto.fragment.base.ImageGridFragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 19.08.2014.
 */
@EFragment
public class GalleryFragment extends ImageGridFragment {

    public static final String TAG = "GalleryFragment";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle(R.string.gallery);

        aq.id(R.id.fab_icon).image(R.drawable.add_button_icon_unchecked);

        ((MainActivity) getBaseActivity()).setBackgroundDrawableWithAnimation(R.color.primary_oppozit_sibling);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) {

            getActivityParams().putString(EXTRA.CURRENT_FRAGMENT, TAG);

            getActionBar().setDisplayHomeAsUpEnabled(true);

            API.images().setPlaceholder(R.drawable.placeholder_image_no_sec);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isDetached())
            getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.add).setVisible(false);
    }

    @Override
    protected GalleryAdapter createAdapter() {
        return new GalleryAdapter(getActivity());
    }

    @Override
    public Loader<Cursor> getCursorLoader() {
        return API.data().getGalleryImagesCursorLoader(getActivity());
    }

    @Override
    public boolean isPhotosSecured() {
        return false;
    }

    @Click
    void fab() {

        if (!isDetached())
            ((MainActivity) getBaseActivity()).secureNewPhotos(getAdapterView().getCheckedItemPositions(), (Cursor) getAdapter().getItem(0));

    }

    public static GalleryFragment build() {
        return GalleryFragment_.builder().build();
    }
}
