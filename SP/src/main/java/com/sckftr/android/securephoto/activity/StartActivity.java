package com.sckftr.android.securephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.UserHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.start_activity)
public class StartActivity extends BaseSPActivity implements TextWatcher {

    private static final String LOG_TAG = StartActivity.class.getSimpleName();

    @ViewById
    EditText passwordInput;

    @ViewById
    TextView passwordCaption;

    @ViewById
    Button commitButton;

    @AfterViews
    void init() {

        if (UserHelper.isFirstLogin()) {

            passwordCaption.setText(getString(R.string.choose_new_password));

        } else {

            commitButton.setVisibility(View.GONE);

            passwordCaption.setText(R.string.input_password);

            passwordInput.addTextChangedListener(this);

        }

    }

    @Override
    public void afterTextChanged(Editable arg0) {

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence newText, int start, int before, int count) {
        Log.d(LOG_TAG, "onTextChanged: " + newText);

        if (UserHelper.authenticate(this, "userName", newText.toString())) {// todo manage with user name

            passwordInput.setActivated(false);

            UserHelper.setIsLogged(true);

            MainActivity.start(this);

            finish();
        }

    }

    @Click(R.id.commitButton)
    void commitClicked() {

        String newPassword = passwordInput.getText().toString();
        UserHelper.logIn("userName", newPassword); // todo manage with user name
        UserHelper.setIsLogged(true);
        UserHelper.setFirstLogin(false);
        MainActivity.start(this);
        finish();
    }

    public static void start(Context context) {
        StartActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
    }

}
