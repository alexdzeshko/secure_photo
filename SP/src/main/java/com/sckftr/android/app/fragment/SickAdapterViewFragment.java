package com.sckftr.android.app.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.utils.Procedure;

public abstract class SickAdapterViewFragment<T extends AbsListView, A extends BaseAdapter> extends
        BaseFragment implements OnScrollListener, OnItemClickListener {

    private T mAdapterView;
    private A mAdapter;

    TextView mEmptyView;
    View mListContainer;
    ProgressBar mProgressBar;

    boolean mListShown;

    Rect mInsets;

    /**
     * The current activated item position. Only used on tablets.
     */
    protected int mActivatedPosition = ListView.INVALID_POSITION;

    protected CharSequence mEmptyText = null;

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mAdapterView.focusableViewAvailable(mAdapterView);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        mEmptyText = getText(R.string.empty_list_text);

        restoreSavedInstanceState(savedInstanceState);

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), null);
//        return inflater.inflate(Platform.getResourceIdFor(this, Platform.RESOURCE_TYPE_LAYOUT, R.layout.list_content), container, false);
    }

    protected abstract int layoutId();

    protected void setActivatedPosition(int position) {

        if (position == ListView.INVALID_POSITION) {

            getAdapterView().setItemChecked(mActivatedPosition, false);

        } else {

            getAdapterView().setItemChecked(position, true);

        }

        mActivatedPosition = position;
    }

    public A getAdapter() {

        if (mAdapter == null) {

            mAdapter = createAdapter();

        }

        return mAdapter;
    }

    protected abstract A createAdapter();

    @Override
    public void onSaveInstanceState(Bundle bundle) {

        super.onSaveInstanceState(bundle);

        bundle.putInt("mActivatedPosition", mActivatedPosition);
    }

    protected void restoreSavedInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            mActivatedPosition = savedInstanceState.getInt("mActivatedPosition");

        }
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {

        mHandler.removeCallbacks(mRequestFocus);

        mAdapterView = null;
        mEmptyView = null;
        mListContainer = null;

        mListShown = false;

        super.onDestroyView();
    }

    protected void onScrolledBottom() {
        // NOOP
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setAdapter(A adapter) {

        mAdapter = adapter;

        if (mAdapterView != null) {

            mAdapterView.setAdapter(adapter);

        }
    }

//    protected void setListItems(List<E> items) {
//
//        mAdapter.setItems(items);
//
//        setListShown(true);
//    }

    /**
     * Set the currently selected list item to the specified
     * position with the adapter's data
     *
     * @param position
     */
    public void setSelection(int position) {
        mAdapterView.setSelection(position);
    }

    /**
     * Get the position of the currently selected list item.
     */
    public int getSelectedItemPosition() {
        return mAdapterView.getSelectedItemPosition();
    }

    /**
     * Get the cursor row ID of the currently selected list item.
     */
    public long getSelectedItemId() {
        return mAdapterView.getSelectedItemId();
    }

    /**
     * Get the onActivityStop's list or grid view widget.
     */
    public T getAdapterView() {
        return mAdapterView;
    }

    /**
     * The default content for a ListFragment has a TextView that can
     * be display when the list is empty.  If you would like to have it
     * display, call this method to supply the text it should use.
     */
    public void setEmptyText(CharSequence text) {

        if (mEmptyView != null) {

            mEmptyView.setText(text);

        }
    }

    public void setEmptySubText(String s) {
        // TODO: implement with dynamic create views
    }

    public void setEmptyAction(final String s, final Procedure<View> procedure) {
        // TODO: implement with dynamic create viewsF
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list mItems will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give mItems the 'activated' state when touched.
        getAdapterView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    private void setListShown(boolean shown, boolean animate) {

        if (mListShown == shown) return;

        mListShown = shown;

        if (mListContainer == null || mProgressBar == null) return;

        if (shown) {

            if (animate) {
                // TODO: use object animator
                mProgressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));

            } else {

                // TODO: use object animator
                mProgressBar.clearAnimation();
                mListContainer.clearAnimation();

            }

            mListContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

        } else {

            if (animate) {

                // TODO: use object animator
                mProgressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));

            } else {

                // TODO: use object animator
                mProgressBar.clearAnimation();
                mListContainer.clearAnimation();

            }

            mProgressBar.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);

        mAdapterView.setAdapter(getAdapter());

        mAdapterView.setOnScrollListener(this);

        setActivatedPosition(mActivatedPosition);
    }

    private void initUI(View view) {

        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mProgressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        mListContainer = view.findViewById(R.id.listContainer);

        View rawListView = view.findViewById(android.R.id.list);

        if (!(rawListView instanceof AbsListView)) {
            throw new RuntimeException(
                    "Content has view with id attribute 'android.R.id.list' "
                            + "that is not a AbsListView class"
            );
        }

        mAdapterView = (T) rawListView;
        mAdapterView.setEmptyView(mEmptyView);
        mAdapterView.setOnItemClickListener(this);

        setEmptyText(mEmptyText);

        setListShown(false, false);

        aq.id(getAdapterView()).scrolledBottom(this, "onScrolledBottom").scrolled(this);

        mHandler.post(mRequestFocus);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //NOOP
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //NOOP
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //NOOP
    }

    @Override
    public void populateInsets(Rect insets) {

        mInsets = insets;

        updateInsets(insets);

    }

    private void updateInsets(Rect insets) {

        final T view = getAdapterView();

        view.setClipToPadding(false);

        view.setPadding(insets.left, insets.top, insets.right, insets.bottom);

    }
}
