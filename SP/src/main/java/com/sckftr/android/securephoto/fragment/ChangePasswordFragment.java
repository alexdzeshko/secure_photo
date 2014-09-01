package com.sckftr.android.securephoto.fragment;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.activity.SettingsActivity;
import com.sckftr.android.securephoto.data.DataApi;
import com.sckftr.android.securephoto.fragment.base.BaseSettingsFragment;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Strings;

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

    private boolean oldPasswordValid, newPasswordValid, confirmNewPasswordValid;

    @ViewById
    Button done;

    @AfterViews
    void onAfterViews() {
        setTitle(R.string.change_password);
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

            UserHelper.changePassword("userName", newPassword.getText().toString());

            SettingsActivity activity = (SettingsActivity) getBaseActivity();

            Toast.makeText(activity, activity.getString(R.string.password_successfully_changed), Toast.LENGTH_LONG).show();

            activity.back();

        }
    }

    private void validate(EditText editText) {

        if (oldPasswordValid && newPasswordValid && confirmNewPasswordValid) {

            if (done.getAlpha() != 1) showDoneButton(true);

            hideSoftKeyboard(editText);

        } else {

            if (done.getAlpha() != 0) showDoneButton(false);

        }
    }

    private boolean strictValidate() {

        boolean validate = oldPasswordValid && newPasswordValid && confirmNewPasswordValid;

        boolean authenticate = UserHelper.authenticate("userName", oldPassword.getText().toString());
        boolean confirm = newPassword.getText().toString().equals(confirmNewPassword.getText().toString());

        if (!authenticate) {

            oldPassword.setText(Strings.EMPTY);

            // TODO password error
            oldPassword.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));

            return false;

        } else {

            oldPassword.setHintTextColor(getResources().getColor(R.color.text_hint));

        }

        if (!confirm) {

            newPassword.setText(Strings.EMPTY);
            confirmNewPassword.setText(Strings.EMPTY);

            newPassword.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
            confirmNewPassword.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));

        } else {

            newPassword.setHintTextColor(getResources().getColor(R.color.text_hint));
            confirmNewPassword.setHintTextColor(getResources().getColor(R.color.text_hint));

        }

        boolean strictValidate = authenticate && confirm;

        return validate && strictValidate;

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
