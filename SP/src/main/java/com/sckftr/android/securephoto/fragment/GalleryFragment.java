package com.sckftr.android.securephoto.fragment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
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
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.GalleryAdapter;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.image.FileBitmapSourceLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

import by.grsu.mcreader.mcrimageloader.imageloader.listener.PauseScrollListener;

/**
 * Created by Dzianis_Roi on 11.08.2014.
 */
@EFragment
public class GalleryFragment extends SickAdapterViewFragment<GridView, GalleryAdapter> implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {

    private List<Integer> actionList;

    private PauseScrollListener mPauseScrollListener;

    @Override
    protected int layoutId() {
        return R.layout.images;
    }

    @Override
    protected GalleryAdapter createAdapter() {
        return new GalleryAdapter(getActivity());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPauseScrollListener = new PauseScrollListener(API.images());
    }

    @AfterViews
    void init() {

        aq.id(R.id.camera).gone();

        getAdapterView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getAdapterView().setMultiChoiceModeListener(this);

        getLoaderManager().initLoader(123, null, this);

        API.images().setBitmapSourceLoader(new FileBitmapSourceLoader());
    }

    @Override
    public void onPause() {
        super.onPause();

        API.images().setPauseWork(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return API.data().getGalleryImagesCursorLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        getAdapter().swapCursor(data);

        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        getBaseActivity().loadFragment(ImagePagerFragment.build(position, true), true, MainActivity.DETAIL_IMAGE_FRAGMENT_TAG);

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

        mPauseScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);

        mPauseScrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        mode.setSubtitle(API.qstring(R.plurals.selected_items, getAdapterView().getCheckedItemCount()));

        actionList.add(position);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.setTitle(API.string(R.string.cab_title_select_items));

        actionList = new ArrayList<Integer>();

        MenuInflater inflater = mode.getMenuInflater();

        if (inflater == null) return false;

        inflater.inflate(R.menu.gallery_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:

                ((MainActivity) getBaseActivity()).secureNewPhotos((ArrayList<Integer>) actionList, (Cursor) getAdapter().getItem(0));

                return true;

            default:
                return false;
        }
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

        actionList = null;
    }

    @Override
    public void populateInsets(Rect insets) {

        super.populateInsets(insets);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.dim_small);

        getAdapterView().setPadding(insets.left + spacing, insets.top + spacing, insets.right + spacing, insets.bottom + spacing);
    }

    public static GalleryFragment build() {
        return GalleryFragment_.builder().build();
    }
}
