package com.sckftr.android.securephoto.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.sckftr.android.app.fragment.SickAdapterViewFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.MainActivity;
import com.sckftr.android.securephoto.adapter.ImagesGridCursorAdapter;
import com.sckftr.android.securephoto.db.Image;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import by.grsu.mcreader.mcrimageloader.imageloader.listener.PauseScrollListener;

@EFragment
public class ImageGridFragment extends SickAdapterViewFragment<GridView, ImagesGridCursorAdapter> implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {

    private ArrayList<Image> actionList;

    private PauseScrollListener mPauseScrollListener;

    @ViewById
    Button camera;

    @Override
    protected int layoutId() {
        return R.layout.images;
    }

    @Override
    protected ImagesGridCursorAdapter createAdapter() {
        return new ImagesGridCursorAdapter(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPauseScrollListener = new PauseScrollListener(API.images());
    }

    @AfterViews
    void init() {

        getAdapterView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getAdapterView().setMultiChoiceModeListener(this);

        getLoaderManager().initLoader(123, null, this);

        setHidingView(camera);

        setTitle(R.string.secured);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) ((MainActivity) getBaseActivity()).toggleSourceLoader(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        API.images().setPauseWork(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return API.data().getEncryptedImagesCursorLoader(getContext());

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

        getBaseActivity().loadFragment(ImagePagerFragment.build(position, false), true, MainActivity.DETAIL_IMAGE_FRAGMENT_TAG);

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

    public static Fragment build() {
        return ImageGridFragment_.builder().build();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        mode.setSubtitle(API.qstring(R.plurals.selected_items, getAdapterView().getCheckedItemCount()));
        Cursor cursor = (Cursor) getAdapter().getItem(position);
        actionList.add(new Image(cursor));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.setTitle(API.string(R.string.cab_title_select_items));

        actionList = new ArrayList<Image>();

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

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionList = null;
    }

    @Click
    void camera() {
        ((MainActivity) getActivity()).startCamera();
    }

    @Override
    public void populateInsets(Rect insets) {
        super.populateInsets(insets);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.dim_small);
        final int margin = getResources().getDimensionPixelSize(R.dimen.unit3);

        getAdapterView().setPadding(insets.left + spacing, insets.top + spacing, insets.right + spacing, insets.bottom + spacing);

        final Button button = aq.id(R.id.camera).getButton();
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button.getLayoutParams();
        layoutParams.bottomMargin = insets.bottom + margin;
        layoutParams.rightMargin = insets.right + margin;
        button.setLayoutParams(layoutParams);
    }
}
