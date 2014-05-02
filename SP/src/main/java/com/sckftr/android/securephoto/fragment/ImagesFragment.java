package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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

import by.deniotokiari.core.utils.ContractUtils;

@EFragment(R.layout.images)
public class ImagesFragment extends SickAdapterViewFragment<GridView, ImagesGridCursorAdapter> implements LoaderManager.LoaderCallbacks<Cursor> {


    @Override protected int layoutId() {
        return R.layout.images;
    }

    @Override protected ImagesGridCursorAdapter createAdapter() {
        return new ImagesGridCursorAdapter(getActivity());
    }

    @AfterViews void init() {

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
}
