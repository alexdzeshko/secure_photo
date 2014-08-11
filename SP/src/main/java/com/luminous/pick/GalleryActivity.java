package com.luminous.pick;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.image.FileBitmapSourceLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import by.grsu.mcreader.mcrimageloader.imageloader.SuperImageLoader;

@EActivity
public class GalleryActivity extends BaseSPActivity implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {

    @ViewById
    GridView grid;

    GalleryAdapter adapter;

    private SuperImageLoader imageLoader;

    @AfterViews
    void onAfterViews() {

        grid.setFastScrollEnabled(true);
        grid.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        grid.setMultiChoiceModeListener(this);

        adapter = new GalleryAdapter(getApplicationContext());

        grid.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        API.images().setBitmapSourceLoader(new FileBitmapSourceLoader());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID}, null, null, MediaStore.Images.Media._ID + " DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        mode.setSubtitle(API.qstring(R.plurals.selected_items, grid.getCheckedItemCount()));
        Cursor cursor = (Cursor) adapter.getItem(position);
//        actionList.add(new Image(cursor));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.setTitle(API.string(R.string.cab_title_select_items));

//        actionList = new ArrayList<Image>();

        MenuInflater inflater = mode.getMenuInflater();

        if (inflater == null) return false;

        inflater.inflate(R.menu.cab_image_list, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_delete:
//
//                API.data().deleteFiles(actionList);
//                mode.finish();
//                return true;
//
//            case R.id.menu_unlock:
//
//                API.data().uncryptonize(actionList, null);
//                mode.finish();
//                return true;
//
//            default:
//                return false;
//        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

//        actionList = null;
    }

    public static void start(Context context) {
        GalleryActivity_.intent(context).start();
    }
}
