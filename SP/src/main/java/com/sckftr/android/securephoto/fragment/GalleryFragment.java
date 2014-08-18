package com.sckftr.android.securephoto.fragment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

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

        setSwipeRefreshEnabled(false);

        setTitle(R.string.gallery);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) {

            MainActivity mainActivity = (MainActivity) getBaseActivity();

            mainActivity.toggleSourceLoader(false);

            mainActivity.showAddMenuItem(false);

            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isDetached()) {

            MainActivity mainActivity = (MainActivity) getBaseActivity();

            mainActivity.showAddMenuItem(true);

            API.images().setPauseWork(false);

            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        setRefreshing(true);

        return API.data().getGalleryImagesCursorLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setRefreshing(false);

        getAdapter().swapCursor(data);

        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setRefreshing(false);

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

        if (checked) {
            actionList.add(position);
        } else {
            actionList.remove(new Integer(position));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.setTitle(API.string(R.string.cab_title_select_items));

        actionList = new ArrayList<Integer>();

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

        if (actionList != null) {
            ((MainActivity) getBaseActivity()).secureNewPhotos((ArrayList<Integer>) actionList, (Cursor) getAdapter().getItem(0));
        }

        actionList = null;
    }

    @Override
    public void populateInsets(Rect insets) {

        super.populateInsets(insets);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.dim_small);

        getAdapterView().setPadding(insets.left + spacing, insets.top + spacing, insets.right + spacing, insets.bottom + spacing);

        final SwipeRefreshLayout layout = (SwipeRefreshLayout) aq.id(R.id.refreshContainer).getView();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.topMargin = insets.top;

        layout.setLayoutParams(layoutParams);
    }

    public static GalleryFragment build() {
        return GalleryFragment_.builder().build();
    }
}
