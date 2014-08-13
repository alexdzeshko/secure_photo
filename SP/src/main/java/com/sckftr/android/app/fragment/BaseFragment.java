package com.sckftr.android.app.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.sckftr.android.app.BroadcastHandler;
import com.sckftr.android.app.activity.BaseActivity;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.AQuery;
import com.sckftr.android.utils.Function;
import com.sckftr.android.utils.Platform;
import com.sckftr.android.utils.UI;

public abstract class BaseFragment extends InsetAwareFragment implements AppConst {

    protected final Handler mHandler = new Handler();

    protected AQuery aq;

    private BroadcastHandler broadcastHandler;

    public static class LoaderCallbacks<E> implements LoaderManager.LoaderCallbacks<E> {

        @Override
        public Loader<E> onCreateLoader(int id, Bundle args) {
            return null;
        }

        @Override
        public void onLoadFinished(Loader<E> loader, E data) {

        }

        @Override
        public void onLoaderReset(Loader<E> loader) {

        }
    }

    /**
     * ******************************************************
     * /** Lifetime
     * /********************************************************
     */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        broadcastHandler = new BroadcastHandler(getContext());

        aq = new AQuery(view);
    }

    protected <T extends Parcelable> void registerReceiver(String action, final Function<T, Boolean> function) {

        broadcastHandler.registerReceiver(action, function);

    }

    @Override
    public void onDestroyView() {
        broadcastHandler.unregisterAll();

        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        broadcastHandler = new BroadcastHandler(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int resId = Platform.getResourceIdFor(this, Platform.RESOURCE_TYPE_LAYOUT);
        return resId == 0 ? super.onCreateView(inflater, container, savedInstanceState) : inflater.inflate(resId, container, false);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Tries to resolve menu layout resource by key {@code #getNameKey()} <br/>
     * and then applies it if exists
     */
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        MenuUtils.apply(this, menu);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public Context getContext() {
        return getActivity();
    }

    public void setTitle(int res) {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle(res);

    }

    public void setTitle(String title) {

        getActionBar().setTitle(title);

    }

    protected Bundle getActivityParams() {
        return getBaseActivity().getParams();
    }

    protected Object getActivityParamObject(Class<?> clazz, boolean removeImmediately) {
        return getBaseActivity().getParamObject(clazz, removeImmediately);
    }

    protected Object putActivityParamObject(Object obj) {
        return getBaseActivity().putParamObject(obj);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle params = data.getExtras();
            if (params != null) {
                onActivityResultParams(requestCode, params);
            }
        }

    }

    protected void onActivityResultParams(int requestCode, Bundle params) {

    }

    /**
     * ******************************************************
     * /** Routines
     * /********************************************************
     */

    public void setHomeAsUp(boolean enabled) {
        getActionBar().setDisplayHomeAsUpEnabled(enabled);
        getActionBar().setDisplayShowHomeEnabled(enabled);
    }

    protected ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public void setCustomActionBarView(View view) {

        getActionBar().setDisplayShowCustomEnabled(view != null);
        getActionBar().setCustomView(view);
    }

    protected void d(String msg) {
        Log.d("fragment", msg);
    }

    public void showSoftKeyboard(View view) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //inputManager.showSoftInput(view, 0); // do not work on samsung devices
    }

    public void hideSoftKeyboard(View view) {

        UI.getInputManager(getContext()).hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

}
