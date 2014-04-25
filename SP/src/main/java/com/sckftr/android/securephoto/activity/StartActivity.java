package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.helper.UserHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.start_activity)
public class StartActivity extends Activity implements TextWatcher {

    private static final String LOG_TAG = StartActivity.class.getSimpleName();

    @ViewById EditText passwordInput;

    @ViewById TextView passwordCaption;

    @ViewById Button commitButton;

    @AfterViews void init() {
        if (UserHelper.isFirstLogin(this)) {

            passwordCaption.setText("Choose your new password");

        } else {
            commitButton.setVisibility(View.GONE);
            passwordCaption.setText("Input password");
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

        if (UserHelper.authenticate(this, "userName", newText.toString())) {

            passwordInput.setActivated(false);

            UserHelper.setIsLogged(this, true);

            MainActivity.start(this);

            finish();
        }

    }

    @Click(R.id.commitButton) void commitClicked() {

        String newPassword = passwordInput.getText().toString();
        UserHelper.logIn(this, "userName", newPassword);
        UserHelper.setIsLogged(this, true);
        UserHelper.setFirstLogin(this, false);
        MainActivity.start(this);
        finish();
    }

}
