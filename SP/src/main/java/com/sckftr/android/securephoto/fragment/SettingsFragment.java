package com.sckftr.android.securephoto.fragment;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.sckftr.android.app.fragment.BaseFragment;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.base.BaseSettingsFragment;
import com.sckftr.android.securephoto.helper.UserHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Dzianis_Roi on 29.08.2014.
 */
@EFragment
public class SettingsFragment extends BaseSettingsFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";

    @AfterViews
    void onAfterViews() {

        setTitle(R.string.label_settings);

        UserHelper.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (UserHelper.isPhotosRestoring()) aq.id(R.id.changePassword).enabled(false);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean restored = sharedPreferences.getBoolean(KEYS.PREF_PHOTOS_RESTORING, false);

        Log.d(TAG, restored + "");

        aq.id(R.id.changePassword).enabled(!restored);
    }
}
