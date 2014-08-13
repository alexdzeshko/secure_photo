package com.sckftr.android.app.listener;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

import com.sckftr.android.utils.UiUtil;

/**
 * Created by Dzianis_Roi on 13.08.2014.
 */
public class HideViewScrollListener implements AbsListView.OnScrollListener {

    private AbsListView.OnScrollListener mExternalListener;

    private boolean hidden;

    private int mPrevFirstVisibleItem;

    ObjectAnimator show, hide;

    public HideViewScrollListener(Context context, View target, AbsListView.OnScrollListener externalListener) {

        final int displayHeight = UiUtil.getDisplayHeight(context);

        hide = ObjectAnimator.ofFloat(target, "translationY", 0, displayHeight).setDuration(300);
        show = ObjectAnimator.ofFloat(target, "translationY", displayHeight, 0).setDuration(300);

        mExternalListener = externalListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mExternalListener != null)
            mExternalListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (firstVisibleItem > mPrevFirstVisibleItem && !hidden) {

            if (show.isRunning()) show.cancel();

            hide.start();

            hidden = true;

        } else if (firstVisibleItem < mPrevFirstVisibleItem && hidden) {

            if (hide.isRunning()) hide.cancel();

            show.start();

            hidden = false;

        }

        mPrevFirstVisibleItem = firstVisibleItem;

        if (mExternalListener != null)
            mExternalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

    }
}
