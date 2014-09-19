package com.sckftr.android.securephoto.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.DetailFragment;
import com.sckftr.android.utils.systemui.SystemUiHelper;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by Dzianis_Roi on 20.08.2014.
 */
@EActivity(R.layout.frame)
public class DetailActivity extends BaseSPActivity {

    @Extra int position;

    private SystemUiHelper mSystemUiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSystemUiHelper = getSystemUiHelper();

        loadFragment(DetailFragment.build(position), false, DetailFragment.TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                finish();

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public SystemUiHelper getSystemUiHelper() {
        if (mSystemUiHelper == null) {

            mSystemUiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_LOW_PROFILE, SystemUiHelper.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES);
            mSystemUiHelper.delayHide(5000);

        }
        return mSystemUiHelper;
    }

    public static void start(Fragment fragment, int position) {
        DetailActivity_.intent(fragment).position(position).start();
    }
}
