package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.GalleryFragment;
import com.sckftr.android.securephoto.fragment.SecuredFragment;
import com.sckftr.android.securephoto.helper.PhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Procedure;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.util.ArrayList;

@EActivity
public class MainActivity extends BaseSPActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bean
    PhotoHelper photoHelper;

    private boolean saveLivingHint;

    /**
     * ******************************************************
     * /** Lifetime
     * /********************************************************
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fragmentTag = getParams().getString(EXTRA.CURRENT_FRAGMENT, SecuredFragment.TAG);

        loadFragment(fragmentTag.equals(SecuredFragment.TAG) ? SecuredFragment.build() : GalleryFragment.build(), false, fragmentTag);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!UserHelper.isLogged()) StartActivity.start(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {

                back();

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        saveLivingHint = false;

        if (requestCode == REQUESTS.IMAGE_CAPTURE) {

            final Uri uri = getParams().getParcelable(PhotoHelper.EXTRA_NEW_PHOTO);

            if (uri != null) {

                if (resultCode != Activity.RESULT_OK) {

                    SecuredFragment fragment = getSecuredFragment();

                    if (fragment != null && !fragment.isDetached())
                        fragment.setRefreshing(false);

                    new FileAsyncTask().deleteFile(uri);

                    return;
                }

                Image image = new Image(String.valueOf(System.currentTimeMillis()), uri.toString());

                ArrayList<Image> images = new ArrayList<Image>(1);

                images.add(image);

                API.data().cryptonize(images, null);
            }

            getParams().remove(PhotoHelper.EXTRA_NEW_PHOTO);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (!saveLivingHint) UserHelper.setIsLogged(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isFinishing()) UserHelper.setIsLogged(false);
    }

    /**
     * ******************************************************
     * /** Click listeners
     * /********************************************************
     */

    public void startCamera() {
        saveLivingHint = photoHelper.takePhotoFromCamera(this);
    }

    /**
     * ******************************************************
     * /** Options
     * /********************************************************
     */

    @OptionsItem
    void add() {

        Fragment fragment = findFragmentByTag(GalleryFragment.TAG);

        loadFragment(fragment != null ? fragment : GalleryFragment.build(), true, GalleryFragment.TAG);

    }

    @OptionsItem
    void settings() {
        setSaveLivingHint(true);

        startActivity(new Intent(this, SettingsActivity.class));
    }

    void back() {

        Fragment fragment = getSecuredFragment();

        loadFragment(fragment != null ? fragment : SecuredFragment.build(), false, SecuredFragment.TAG);

    }

    /**
     * ******************************************************
     * /** Fragment
     * /********************************************************
     */

    SecuredFragment getSecuredFragment() {
        return (SecuredFragment) findFragmentByTag(SecuredFragment.TAG);
    }

    /**
     * ******************************************************
     * /** Actions
     * /********************************************************
     */

    public void secureNewPhotos(SparseBooleanArray items, Cursor cursor) {
        photoHelper.secureNewPhotos(items, cursor, new Procedure<String>() {
            @Override
            public void apply(String dialog) {

                SecuredFragment securedFragment = getSecuredFragment();

                if (securedFragment != null && !securedFragment.isDetached())
                    getSecuredFragment().setRefreshing(false);

            }
        });

        back();

        getSecuredFragment().setRefreshing(true);
    }

    public void unSecurePhotos(SparseBooleanArray items, Cursor cursor) {
        photoHelper.unSecurePhotos(items, cursor);
    }

    public void deletePhotos(SparseBooleanArray items, Cursor cursor) {
        photoHelper.deletePhotos(items, cursor);
    }

    public void setSaveLivingHint(boolean saveLivingHint) {
        this.saveLivingHint = saveLivingHint;
    }

    /**
     * ******************************************************
     * /** Activity start
     * /********************************************************
     */

    public static void start(Context context) {
        MainActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
    }
}
