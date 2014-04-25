package com.sckftr.android.securephoto.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.fragment.ImageFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.securephoto.processor.Crypto;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import by.deniotokiari.core.utils.ContractUtils;
import by.deniotokiari.core.utils.IOUtils;

@EActivity(R.layout.main)
public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MENU_CAM = R.id.menu_item_camera;
    private static final int MENU_SHARE = R.id.menu_item_share;
    private static final int MENU_ADD = R.id.menu_add_items;

    @ViewById StaggeredGridView grid;

    private ImagesGridCursorAdapter mAdapter;

    private boolean mConfigChanged;

    @AfterViews void init() {

        grid.setOnItemClickListener(this);
        mAdapter = new ImagesGridCursorAdapter(this);
        grid.setAdapter(mAdapter);
        grid.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this);
                builder.setTitle("Choose action");
                builder.setMessage("What do you want to do?");
                builder.setCancelable(true);
                builder.setPositiveButton("Decrypt",
                        new DialogInterface.OnClickListener() { // ������ ��
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Cursor cursor = (Cursor) mAdapter
                                                .getItem(position);
                                        String uri = cursor.getString(cursor
                                                .getColumnIndex(Contracts.ImageContract.URI));
                                        String key = cursor.getString(cursor
                                                .getColumnIndex(Contracts.ImageContract.KEY));
                                        FileInputStream stream = null;
                                        FileOutputStream fileOutputStream = null;
                                        try {
                                            stream = new FileInputStream(uri);
                                            byte[] buffer = new byte[stream
                                                    .available()];
                                            stream.read(buffer);
                                            byte[] decrypted = Crypto.decrypt(buffer, key);
                                            fileOutputStream = new FileOutputStream(
                                                    uri);
                                            fileOutputStream.write(decrypted);
                                        } catch (FileNotFoundException e) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        } catch (IOException e) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        } finally {
                                            IOUtils.closeStream(stream);
                                            IOUtils.closeStream(fileOutputStream);
                                        }
                                        getContentResolver()
                                                .delete(ContractUtils
                                                                .getUri(Contracts.ImageContract.class),
                                                        Contracts.ImageContract.URI
                                                                + " = '" + uri
                                                                + "'", null
                                                );
                                    }
                                }).start();
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                );
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }
                );
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        getLoaderManager().initLoader(123, null, this);
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
        mConfigChanged = true;
        switch (item.getItemId()) {
            case MENU_CAM:
                TakePhotoHelper.takePhotoFromCamera(this);
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

        if (requestCode == TakePhotoHelper.KEY_CAMERA_REQUEST) {
            Uri uri = TakePhotoHelper.getImageUri(requestCode, resultCode);
            if (uri != null) {
                mAdapter.notifyDataSetChanged();
                Intent intent = new Intent(this, PrepareActivity.class);
                intent.setData(uri);
                mConfigChanged = true;
                startActivity(intent);
            }
        } else if (requestCode == TakePhotoHelper.KEY_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
//            Intent intent = new Intent(this, PrepareActivity.class);
//            intent.setData(Uri.parse(TakePhotoHelper.getPath(uri, getBaseContext())));
            mConfigChanged = true;
            PrepareActivity.start(this, Uri.parse(TakePhotoHelper.getPath(uri, this)));
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
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        showImageFragment(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConfigChanged = true;
    }

    @Override
    protected void onUserLeaveHint() {
        UserHelper.setIsLogged(this, false);
        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
        Log.e("onStop", "onStop");
        if (!mConfigChanged) {
            Log.e("onStop", "log out");
            UserHelper.setIsLogged(this, false);
        }
        mConfigChanged = false;
        super.onDestroy();
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

}
