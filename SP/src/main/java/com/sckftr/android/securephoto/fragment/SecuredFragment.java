package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sckftr.android.app.adapter.BaseCursorAdapter;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;
import com.sckftr.android.securephoto.fragment.base.ImageGridFragment;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Strings;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 19.08.2014.
 */
@EFragment
public class SecuredFragment extends ImageGridFragment {

    public static final String TAG = "SecuredFragment";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle(R.string.secured);

        ((MainActivity) getBaseActivity()).setBackgroundDrawableWithAnimation(R.color.primary_dark_60);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) {
            getActivityParams().putString(EXTRA.CURRENT_FRAGMENT, TAG);

            API.images().setPlaceholder(R.drawable.ic_blue_lock);

            if (!Strings.isEmpty(UserHelper.getOldUserHash())) {

                BaseCursorAdapter adapter = getAdapter();

                if (adapter != null) {

                    setRefreshing(true);

                    restorePhotos(adapter.getCursor());

                    adapter.swapCursor(null);
                }
            }
        }
    }

    @Override
    protected ImagesGridCursorAdapter createAdapter() {
        return new ImagesGridCursorAdapter(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (Strings.isEmpty(UserHelper.getOldUserHash())) {

            super.onLoadFinished(loader, data);

        } else {

            setListShown(true);
            setRefreshing(true);

            restorePhotos(data);

        }
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
    public Loader<Cursor> getCursorLoader() {
        return API.data().getEncryptedImagesCursorLoader(getActivity());
    }

    @Override
    public boolean isPhotosSecured() {
        return true;
    }

    @Click
    void fab() {
        setRefreshing(true);

        ((MainActivity) getActivity()).startCamera();
    }

    private void restorePhotos(Cursor cursor) {
        MainActivity activity = (MainActivity) getBaseActivity();

        if (activity != null) activity.restorePhotos(cursor);
    }

    public static Fragment build() {
        return SecuredFragment_.builder().build();
    }
}
