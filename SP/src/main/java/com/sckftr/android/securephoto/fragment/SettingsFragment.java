package com.sckftr.android.securephoto.fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.base.BaseSettingsFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 29.08.2014.
 */
@EFragment
public class SettingsFragment extends BaseSettingsFragment {

    public static final String TAG = "SettingsFragment";

    @AfterViews
    void onAfterViews() {
        setTitle(R.string.label_settings);
    }

    @Click
    void changePassword() {
        getBaseActivity().loadFragment(ChangePasswordFragment.build(), true, ChangePasswordFragment.TAG);
    }

    @Override
    protected Drawable getBackground() {
        return new ColorDrawable(getResources().getColor(android.R.color.white));
    }

    @Override
    protected String getFragmentTag() {
        return TAG;
    }

    public static SettingsFragment build() {
        return SettingsFragment_.builder().build();
    }

}
