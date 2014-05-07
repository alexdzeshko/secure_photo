package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
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
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.utils.UI;

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
        //todo REFACTOR
        getAdapterView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                UI.showAlert(getBaseActivity(), null, "Unlock file?", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = (Cursor) getAdapter().getItem(position);
                        AppConst.API.data().uncryptonize(new Image(cursor), null);
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                return true;
            }
        });

        getLoaderManager().initLoader(123, null, this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ContractUtils.getUri(Contracts.ImageContract.class), null, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().swapCursor(data);
        setListShown(true);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    private void showImageFragment(int pos) {
        Fragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ImageFragment.KEY_ARG_POS, pos);
        fragment.setArguments(bundle);
        getBaseActivity().addFragment(0, fragment, "fullscreen");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showImageFragment(position);
    }

    public static Fragment build(){
        return ImagesFragment_.builder().build();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        if(actionList==null)actionList = new ArrayList<Image>();
        Cursor cursor = (Cursor) getAdapter().getItem(position);
        actionList.add(new Image(cursor));
    }

    @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        actionList = new ArrayList<Image>();
        MenuInflater inflater = mode.getMenuInflater();
        if (inflater==null) return false;
        inflater.inflate(R.menu.cab_image_list, menu);
        return true;
    }

    @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteSelectedItems();
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.menu_unlock:
                //todo unlock file
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    private void deleteSelectedItems() {
        API.data().deleteFiles(actionList);

    }

    @Override public void onDestroyActionMode(ActionMode mode) {

        actionList = null;
    }
}
