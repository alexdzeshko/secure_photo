package com.sckftr.android.securephoto.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.DetailFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by Dzianis_Roi on 20.08.2014.
 */
@EActivity(R.layout.frame)
public class DetailActivity extends BaseSPActivity implements View.OnSystemUiVisibilityChangeListener {

    @Extra
    int position;

    Handler hideHandler = new Handler();
    Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            View decorView = getWindow().getDecorView();

            if (decorView != null) showNavigation(decorView, false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);

        loadFragment(DetailFragment.build(position), false, DetailFragment.TAG);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(200);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {

            delayedHide(5000);

        } else {

            hideHandler.removeCallbacks(hideRunnable);

        }
    }

    public void delayedHide(int delay) {
        hideHandler.postDelayed(hideRunnable, delay);
    }

    public void resetDelay() {
        hideHandler.removeCallbacks(hideRunnable);

        delayedHide(5000);
    }

    private void showNavigation(View decorView, boolean show) {

        int uiOptions = show ? 0 : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);
    }

    public void toggleNavigation() {

        View decorView = getWindow().getDecorView();

        showNavigation(decorView, (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0);

    }

    public static void start(Fragment fragment, int position) {
        DetailActivity_.intent(fragment).position(position).start();
    }
}
