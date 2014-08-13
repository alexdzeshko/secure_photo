package com.sckftr.android.app.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.AQuery;
import com.sckftr.android.utils.Platform;
import com.sckftr.android.utils.UI;

import java.util.ArrayList;

public class BaseActivity extends Activity implements AppConst {

    private static final String KEY_PARAMS = "KEY_PARAMS";
    protected AQuery aq;

    private ArrayList<PopupWindow> popups;
    private Bundle mParams;

    // ----------------------
    // Life-time
    // ----------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int resId = Platform.getResourceIdFor(this, Platform.RESOURCE_TYPE_LAYOUT);
        if (resId != 0) {
            setContentView(resId);
        }

        if (savedInstanceState != null) {
            mParams = savedInstanceState.getBundle(KEY_PARAMS);
        }

        aq = new AQuery(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBundle(KEY_PARAMS, mParams);
    }

    @Override
    protected void onDestroy() {

        aq = null;

        // dismiss all active popups
        dismissAllPopups();

        super.onDestroy();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        return MenuUtils.apply(this, menu) && super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {

        Intent intent = new Intent();

        if (mParams != null) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtras(mParams);

            setResult(RESULT_OK, intent);
        }

        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == RESULT_OK) {
//            Bundle params = data.getExtras();
//            if (params != null) {
//                onActivityResultParams(requestCode, params);
//            }
//        }
    }

    protected void onActivityResultParams(int requestCode, Bundle params) {
        // NOOP
    }
    // ----------------------
    // Fragments
    // ----------------------

    public int addFragment(int id, Fragment fragment, String key) {

        return UI.addFragment(getFragmentManager(), id, fragment, key);
    }

    public void addFragment(Fragment fragment) {

        addFragment(0, fragment, UI.NO_BACK_STACK);
    }

    // ----------------------
    // Params
    // ----------------------

    public Bundle getParams() {
        if (mParams == null) {
            mParams = new Bundle();
        }
        return mParams;
    }

    public Object getParamObject(Class<?> clazz, boolean removeImmediately) {
        Bundle activityParams = getParams();
        String s = activityParams.getString(clazz.getName());

        if (s == null) {

            return null;
        }

        if (removeImmediately) {

            activityParams.remove(clazz.getName());
        }

        return API.gson().fromJson(s, clazz);
    }

    public Object putParamObject(Object obj) {
        getParams().putString(obj.getClass().getName(), API.gson().toJson(obj));
        return obj;
    }

    // ----------------------
    // Pop-ups
    // ----------------------

    private void unregisterPopupWindow(PopupWindow popUp) {

        if (popups != null) {
            popups.remove(popUp);
        }
    }

    private void registerPopupWindow(PopupWindow popUp) {

        if (popups == null) {
            popups = new ArrayList<PopupWindow>();
        }

        popups.add(popUp);
    }

    public void dismissAllPopups() {

        if (popups != null) {
            for (PopupWindow p : popups) {
                p.dismiss();
            }
        }

        popups = null;
    }

    public void popupText(View view, final Spanned s) {

        view.setOnClickListener(new View.OnClickListener() {

            private PopupWindow popUp;

            @Override
            public void onClick(View v) {

                if (popUp == null) {
                    popUp = UI.showPopupText(v, s);

                    registerPopupWindow(popUp);

                } else {

                    popUp.dismiss();
                    unregisterPopupWindow(popUp);
                    popUp = null;
                }
            }
        });
    }
}
