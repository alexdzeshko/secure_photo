package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sckftr.android.app.fragment.SickAdapterViewFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.db.Image;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;

import by.deniotokiari.core.utils.ContractUtils;

@EFragment(R.layout.images)
public class ImageGridFragment extends SickAdapterViewFragment<GridView, ImagesGridCursorAdapter> implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {

    private ArrayList<Image> actionList;

    @Override protected int layoutId() {
        return R.layout.images;
    }

    @Override protected ImagesGridCursorAdapter createAdapter() {
        return new ImagesGridCursorAdapter(getActivity());
    }

    @AfterViews void init() {

        getAdapterView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getAdapterView().setMultiChoiceModeListener(this);

        getLoaderManager().initLoader(123, null, this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ContractUtils.getUri(Contracts.ImageContract.class), null, null, null, Contracts.ImageContract._ID+" DESC");
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().swapCursor(data);
        setListShown(true);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    private void showImageFragment(String[] value) {
        Fragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ImageFragment.KEY_ARG_VALUE, value);
        fragment.setArguments(bundle);
        getBaseActivity().addFragment(0, fragment, "fullscreen");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//        final String[] strings = {CursorHelper.get(cursor, Contracts.ImageContract.URI), CursorHelper.get(cursor, Contracts.ImageContract.KEY)};
//        showImageFragment(strings);
        Fragment fragment = new ImagePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ImagePagerFragment.KEY_POSITION, position);
        fragment.setArguments(bundle);
        getBaseActivity().addFragment(0, fragment, "gallery");
    }

    public static Fragment build() {
        return ImageGridFragment_.builder().build();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        mode.setSubtitle(API.qstring(R.plurals.selected_items, getAdapterView().getCheckedItemCount()));
        Cursor cursor = (Cursor) getAdapter().getItem(position);
        actionList.add(new Image(cursor));
    }

    @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.setTitle(API.string(R.string.cab_title_select_items));

        actionList = new ArrayList<Image>();

        MenuInflater inflater = mode.getMenuInflater();

        if (inflater == null) return false;

        inflater.inflate(R.menu.cab_image_list, menu);

        return true;
    }

    @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:

                API.data().deleteFiles(actionList);
                mode.finish();
                return true;

            case R.id.menu_unlock:

                API.data().uncryptonize(actionList, null);
                mode.finish();
                return true;

            default:
                return false;
        }
    }

    @Override public void onDestroyActionMode(ActionMode mode) {

        actionList = null;
    }

    @Override
    public void populateInsets(Rect insets) {
        super.populateInsets(insets);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.dim_small);
        getAdapterView().setPadding(
                insets.left + spacing,
                insets.top + spacing,
                insets.right + spacing,
                insets.bottom + spacing);
    }
}
