package com.sckftr.android.app.activity;

import android.graphics.Rect;
import android.os.Bundle;

import com.sckftr.android.app.view.InsetFrameLayout;
import com.sckftr.android.securephoto.R;

import java.util.HashSet;

public abstract class BaseSPActivity extends BaseActivity implements InsetFrameLayout.OnInsetsCallback {

    private HashSet<OnActivityInsetsCallback> mInsetCallbacks;

    private Rect mInsets;

    private InsetFrameLayout mInsetFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInsetFrameLayout = (InsetFrameLayout) findViewById(R.id.frameInset);

        if (mInsetFrameLayout != null) mInsetFrameLayout.setOnInsetsCallback(this);
    }

    public void addInsetChangedCallback(OnActivityInsetsCallback callback) {
        if (mInsetCallbacks == null) mInsetCallbacks = new HashSet<OnActivityInsetsCallback>();

        mInsetCallbacks.add(callback);

        if (mInsets != null) callback.onInsetsChanged(mInsets);
    }

    public void removeInsetChangedCallback(OnActivityInsetsCallback callback) {

        if (mInsetCallbacks != null) mInsetCallbacks.remove(callback);

    }

    @Override
    public void onInsetsChanged(Rect insets) {

        mInsets = insets;

        if (mInsetCallbacks != null && !mInsetCallbacks.isEmpty()) {

            for (OnActivityInsetsCallback callback : mInsetCallbacks)
                callback.onInsetsChanged(insets);

        }
    }

    public void setInsetTopAlpha(float alpha) {

        if (mInsetFrameLayout != null)
            mInsetFrameLayout.setTopInsetAlpha(Math.max(Math.min(Math.round(alpha * 255), 0), 255));

    }

    public void setInsetBackgroundWithAnimation(int colorRes) {
        mInsetFrameLayout.setBackgroundDrawableWithAnimation(colorRes);
    }

    public void resetInsets() {
        setInsetTopAlpha(255);
    }

    public static interface OnActivityInsetsCallback {
        public void onInsetsChanged(Rect insets);
    }
}
