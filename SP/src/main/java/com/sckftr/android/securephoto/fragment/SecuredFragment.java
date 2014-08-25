package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 19.08.2014.
 */
@EFragment
public class SecuredFragment extends ImageGridFragment {

    public static final String TAG = "SecuredFragment";

    @AfterViews
    void onAfterViews() {
        setTitle(R.string.secured);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached())
            getActivityParams().putString(EXTRA.CURRENT_FRAGMENT, TAG);

    }

    @Override
    public void onPause() {
        super.onPause();

        ((MainActivity) getBaseActivity()).showAddMenuItem(false);
    }

    @Override
    protected ImagesGridCursorAdapter createAdapter() {
        return new ImagesGridCursorAdapter(getActivity());
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        MenuInflater inflater = mode.getMenuInflater();

        if (inflater == null) return false;

        inflater.inflate(R.menu.cab_image_list, menu);

        return super.onCreateActionMode(mode, menu);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:

                setRefreshing(true);

                ((MainActivity) getActivity()).deletePhotos(getAdapterView().getCheckedItemPositions(), (Cursor) getAdapter().getItem(0));

                mode.finish();

                return true;

            case R.id.menu_unlock:

                setRefreshing(true);

                ((MainActivity) getActivity()).unSecurePhotos(getAdapterView().getCheckedItemPositions(), (Cursor) getAdapter().getItem(0));

                mode.finish();

                return true;

            default:
                return false;
        }
    }

    @Override
    Loader<Cursor> getCursorLoader() {
        return API.data().getEncryptedImagesCursorLoader(getActivity());
    }

    @Override
    boolean isPhotosSecured() {
        return true;
    }

    @Click
    void camera() {
        setRefreshing(true);

        ((MainActivity) getActivity()).startCamera();
    }

    public static Fragment build() {
        return SecuredFragment_.builder().build();
    }
}
