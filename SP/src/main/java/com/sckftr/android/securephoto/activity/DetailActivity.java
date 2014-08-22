package com.sckftr.android.securephoto.activity;

import android.app.Fragment;
import android.os.Bundle;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.ImagePagerFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by Dzianis_Roi on 20.08.2014.
 */
@EActivity(R.layout.frame)
public class DetailActivity extends BaseSPActivity {

    @Extra
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();

        getWindow().setBackgroundDrawable(null);

        loadFragment(ImagePagerFragment.build(position, false), false, "detail");
    }

    public static void start(Fragment fragment, int position) {
        DetailActivity_.intent(fragment).position(position).start();
    }
}
