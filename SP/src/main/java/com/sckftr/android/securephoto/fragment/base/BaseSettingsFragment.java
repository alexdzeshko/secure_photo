package com.sckftr.android.securephoto.fragment.base;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import com.sckftr.android.app.fragment.BaseFragment;

/**
 * Created by Dzianis_Roi on 29.08.2014.
 */
public abstract class BaseSettingsFragment extends BaseFragment {

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Activity activity = getActivity();

            if (activity != null)
                getActivity().getWindow().setBackgroundDrawable(getBackground());
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHomeAsUp(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDetached()) {
            getActivityParams().putString(EXTRA.CURRENT_FRAGMENT, getFragmentTag());

            if (!isDetached()) {
                handler.removeCallbacks(runnable);

                Window window = getActivity().getWindow();

                TransitionDrawable td = new TransitionDrawable(new Drawable[]{window.getDecorView().getBackground(), getBackground()});

                window.setBackgroundDrawable(td);

                td.startTransition(300);

                handler.postDelayed(runnable, 300);
            }
        }
    }

    protected abstract Drawable getBackground();

    protected abstract String getFragmentTag();

}
