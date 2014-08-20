package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.GalleryFragment;
import com.sckftr.android.securephoto.fragment.SecuredFragment;
import com.sckftr.android.securephoto.helper.PhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.securephoto.image.CryptoBitmapLoader;
import com.sckftr.android.securephoto.image.FileBitmapLoader;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.util.ArrayList;

@EActivity
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends BaseSPActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String IMAGES_FRAGMENT_TAG = "IMAGES";
    public static final String SYSTEM_GALLERY_FRAGMENT_TAG = "SYSTEM_GALLERY";
    public static final String DETAIL_IMAGE_FRAGMENT_TAG = "DETAIL_IMAGE";

    private FileBitmapLoader mFileLoader;
    private CryptoBitmapLoader mCryptoLoader;

    @Bean
    PhotoHelper photoHelper;

    @OptionsMenuItem
    MenuItem add;

    private boolean saveLivingHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFragment(SecuredFragment.build(), false, IMAGES_FRAGMENT_TAG);

        getActionBar().setBackgroundDrawable(null);
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

    public void startCamera() {
        saveLivingHint = photoHelper.takePhotoFromCamera(this);
    }

    @OptionsItem
    void add() {

        Fragment fragment = findFragmentByTag(SYSTEM_GALLERY_FRAGMENT_TAG);

        loadFragment(fragment != null ? fragment : GalleryFragment.build(), true, SYSTEM_GALLERY_FRAGMENT_TAG);

    }

    private void back() {

        Fragment fragment = findFragmentByTag(IMAGES_FRAGMENT_TAG);

        loadFragment(fragment != null ? fragment : SecuredFragment.build(), false, IMAGES_FRAGMENT_TAG);

    }

    public void toggleSourceLoader(boolean secured) {
        if (secured) {

            mCryptoLoader = mCryptoLoader == null ? new CryptoBitmapLoader() : mCryptoLoader;

            API.images().setBitmapSourceLoader(mCryptoLoader);

        } else {

            mFileLoader = mFileLoader == null ? new FileBitmapLoader() : mFileLoader;

            API.images().setBitmapSourceLoader(mFileLoader);

        }
    }

    public void secureNewPhotos(ArrayList<Integer> positions, Cursor cursor) {
        photoHelper.secureNewPhotos(positions, cursor);

        back();
    }

    public void unSecurePhotos(ArrayList<Integer> positions, final Cursor cursor) {
        photoHelper.unSecurePhotos(positions, cursor);
    }

    public void deletePhotos(ArrayList<Integer> positions, final Cursor cursor) {
        photoHelper.deletePhotos(positions, cursor);
    }

    public void showAddMenuItem(boolean show) {
        add.setVisible(show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        saveLivingHint = false;

        if (requestCode == REQUESTS.IMAGE_CAPTURE) {

            final Uri uri = photoHelper.getCapturedPhotoUri();

            if (uri != null) {

                if (resultCode != Activity.RESULT_OK) {

                    new FileAsyncTask().deleteFile(uri);

                    return;
                }

                Image image = new Image(String.valueOf(System.currentTimeMillis()), uri.toString());

                ArrayList<Image> images = new ArrayList<Image>(1);

                images.add(image);

                API.data().cryptonize(images, null);
            }
        }
    }

    public void setSaveLivingHint(boolean saveLivingHint) {
        this.saveLivingHint = saveLivingHint;
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

    public static void start(Context context) {
        MainActivity_.intent(context).start();
    }
}
