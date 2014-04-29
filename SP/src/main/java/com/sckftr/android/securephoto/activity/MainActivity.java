package com.sckftr.android.securephoto.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.ImageFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.UI;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import by.deniotokiari.core.utils.ContractUtils;

@EActivity(R.layout.main)
public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MENU_CAM = R.id.menu_item_camera;
    private static final int MENU_SHARE = R.id.menu_item_share;
    private static final int MENU_ADD = R.id.menu_add_items;

    @ViewById GridView grid;

    private ImagesGridCursorAdapter mAdapter;

    @Override protected void onResume() {
        super.onResume();
//        if(!UserHelper.isLogged(this)) StartActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case MENU_CAM:
                if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    TakePhotoHelper.takePhotoFromCamera(this);
                }
                return true;
            case MENU_SHARE:
                // share
                return false;
            case MENU_ADD:
                TakePhotoHelper.takePhotoFromGallery(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if (requestCode == TakePhotoHelper.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uri = TakePhotoHelper.getImageUri(requestCode, resultCode);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.content,PrepareFragment.build(data.getExtras()))
//                    .addToBackStack("prepare").commit();

        } else if (requestCode == TakePhotoHelper.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
        }
        if (uri != null) {
//            PrepareActivity.start(this, Uri.parse(TakePhotoHelper.getPath(uri, this)));
            PrepareActivity.start(this, uri);
        }

    }


    private void showImageFragment(int pos) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ImageFragment.KEY_ARG_POS, pos);
        fragment.setArguments(bundle);
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showImageFragment(position);
    }

    @Override
    protected void onUserLeaveHint() {
        UserHelper.setIsLogged(this, false);
        super.onUserLeaveHint();
    }

    public static void start(Context context) {
        MainActivity_.intent(context).start();
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContractUtils.getUri(Contracts.ImageContract.class), null, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @AfterViews void init() {

        //todo REFACTOR
        grid.setOnItemClickListener(this);
        mAdapter = new ImagesGridCursorAdapter(this);
        grid.setAdapter(mAdapter);
        grid.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                UI.showAlert(MainActivity.this, null, "Unlock file?", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = (Cursor) mAdapter
                                .getItem(position);
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
}
