package com.sckftr.android.securephoto.fragment;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.fragment.base.BaseSettingsFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Dzianis_Roi on 29.08.2014.
 */
@EFragment(R.layout.fragment_change_password)
public class ChangePasswordFragment extends BaseSettingsFragment {

    public static final String TAG = "ChangePasswordFragment";

    @ViewById
    EditText oldPassword, newPassword, confirmNewPassword;

    private boolean oldPasswordValid, newPasswordValid, confirmNewPasswordValid, newPasswordConfirmed;

    @ViewById
    Button done;

    @AfterViews
    void onAfterViews() {

    }

    @TextChange
    void oldPassword(CharSequence newText, int start, int before, int count) {
        oldPasswordValid = count > 0;

        validate(oldPassword);
    }

    @TextChange
    void newPassword(CharSequence newText, int start, int before, int count) {
        newPasswordValid = count > 0;

        validate(newPassword);
    }

    @TextChange
    void confirmNewPassword(CharSequence newText, int start, int before, int count) {
        confirmNewPasswordValid = count > 0;

        validate(confirmNewPassword);
    }

    @Click
    void done() {

        if (strictValidate()) {
            // TODO change password
        }
    }

    private void validate(EditText editText) {
        Log.d("Alpha", done.getAlpha() + "");

        if (oldPasswordValid && newPasswordValid && confirmNewPasswordValid) {

            if (done.getAlpha() != 1) showDoneButton(true);

            hideSoftKeyboard(editText);

        } else {

            if (done.getAlpha() != 0) showDoneButton(false);

        }
    }

    private boolean strictValidate() {
        // TODO

        return false;
    }

    private void showDoneButton(boolean show) {
        done.setEnabled(show);

        ObjectAnimator.ofFloat(done, View.ALPHA, show ? 0 : 1, show ? 1 : 0).setDuration(400).start();
    }

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
