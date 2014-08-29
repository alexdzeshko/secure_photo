package com.sckftr.android.securephoto.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.base.BaseSettingsFragment;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 29.08.2014.
 */
@EFragment(R.layout.fragment_change_password)
public class ChangePasswordFragment extends BaseSettingsFragment {

    public static final String TAG = "ChangePasswordFragment";

    @Override
    protected Drawable getBackground() {
        return getResources().getDrawable(R.drawable.bg_start);
    }

    @Override
    protected String getFragmentTag() {
        return TAG;
    }

    public static ChangePasswordFragment build() {
        return ChangePasswordFragment_.builder().build();
    }
}
