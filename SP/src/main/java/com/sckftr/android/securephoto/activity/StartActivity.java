package com.sckftr.android.securephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.start_activity)
public class StartActivity extends BaseSPActivity {

    private static final String LOG_TAG = StartActivity.class.getSimpleName();
    public static final int VALIDATION_DELAY_MILLIS = 300;

    @ViewById
    EditText passwordInput;

    @ViewById
    TextView passwordCaption;

    @ViewById
    Button commitButton;

    private String mInput;
    private Handler mHandler;
    private Runnable mValidateRunnable;

    @AfterViews
    void init() {

        if (UserHelper.isFirstLogin()) {

            passwordCaption.setText(getString(R.string.choose_new_password));

            commitButton.setVisibility(View.VISIBLE);

        }

        mHandler = new Handler();

        mValidateRunnable = new Runnable() {
            @Override public void run() {
                if (UserHelper.authenticate("userName", mInput)) {// todo manage with user name

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            aq.id(R.id.validationProgress).animate(android.R.anim.fade_in).display(true);//todo change animation
                        }
                    });

                    passwordInput.setActivated(false);

                    UserHelper.setIsLogged(true);

                    MainActivity.start(StartActivity.this);
                }
            }
        };
    }

    @TextChange
    void passwordInput(CharSequence newText, int start, int before, int count) {
        if (UserHelper.isFirstLogin()) return;

        Log.d(LOG_TAG, "onTextChanged: " + newText);

        validate(newText);
//        if (UserHelper.authenticate("userName", newText.toString())) {// todo manage with user name
//
//            passwordInput.setActivated(false);
//
//            UserHelper.setIsLogged(true);
//
//            MainActivity.start(this);
//        }
    }

    private void validate(CharSequence newText) {
        mInput = newText.toString();
        mHandler.removeCallbacks(mValidateRunnable);
        mHandler.postDelayed(mValidateRunnable, VALIDATION_DELAY_MILLIS);
    }

    @Click(R.id.commitButton)
    void commitClicked() {

        String newPassword = passwordInput.getText().toString();

        UserHelper.logIn("userName", newPassword); // todo manage with user name

        UserHelper.setIsLogged(true);

        UserHelper.setFirstLogin(false);

        MainActivity.start(this);
    }

    public static void start(Context context) {
        StartActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
    }

}
