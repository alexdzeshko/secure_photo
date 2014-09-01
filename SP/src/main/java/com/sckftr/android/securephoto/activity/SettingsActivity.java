package com.sckftr.android.securephoto.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.ChangePasswordFragment;
import com.sckftr.android.securephoto.fragment.SecuredFragment;
import com.sckftr.android.securephoto.fragment.SettingsFragment;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.frame)
public class SettingsActivity extends BaseSPActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tag = getParams().getString(EXTRA.CURRENT_FRAGMENT, SettingsFragment.TAG);

        Fragment fragment = getLastOpenedFragment(tag);

        loadFragment(fragment, false, tag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {

                if (getParams().getString(EXTRA.CURRENT_FRAGMENT, SettingsFragment.TAG).equals(SettingsFragment.TAG)) {
                    finish();
                } else {
                    back();
                }

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void back() {

        Fragment fragment = getSettingsFragment();

        loadFragment(fragment != null ? fragment : SettingsFragment.build(), false, SettingsFragment.TAG);

    }

    public Fragment getLastOpenedFragment(String tag) {

        Fragment lastOpenedFragment = null;

        if (tag.equals(SettingsFragment.TAG))
            lastOpenedFragment = SettingsFragment.build();

        else if (tag.equals(ChangePasswordFragment.TAG))
            lastOpenedFragment = ChangePasswordFragment.build();

        return lastOpenedFragment;
    }

    public Fragment getSettingsFragment() {
        return findFragmentByTag(SettingsFragment.TAG);
    }

    public static void start(Context context) {
        SettingsActivity_.intent(context).start();
    }
}
