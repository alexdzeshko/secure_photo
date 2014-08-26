package com.sckftr.android.securephoto.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

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

    private boolean mStatusBarVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);


        showNavigation(getWindow().getDecorView(), false);

        loadFragment(ImagePagerFragment.build(position, false), false, "detail");
    }

    private void showNavigation(View decorView, boolean show) {

        int uiOptions = show ? 0 : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);
    }

    public void toggleNavigation() {

        View decorView = getWindow().getDecorView();

        showNavigation(decorView, (decorView.getSystemUiVisibility() & (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN)) != 0);

    }

    public static void start(Fragment fragment, int position) {
        DetailActivity_.intent(fragment).position(position).start();
    }
}
