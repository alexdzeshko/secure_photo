package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.GalleryFragment;
import com.sckftr.android.securephoto.fragment.ImageGridFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

@EActivity
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends BaseSPActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String IMAGES_FRAGMENT_TAG = "IMAGES";
    public static final String SYSTEM_GALLERY_FRAGMENT_TAG = "SYSTEM_GALLERY";
    public static final String DETAIL_IMAGE_FRAGMENT_TAG = "DETAIL_IMAGE";

    @Bean
    TakePhotoHelper photoHelper;

    private boolean saveLivingHint;

    @AfterViews
    void init() {

        loadFragment(ImageGridFragment.build(), false, IMAGES_FRAGMENT_TAG);

        getActionBar().setBackgroundDrawable(null);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!UserHelper.isLogged()) {

            StartActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        setIntent(intent);

    }

    @OptionsItem
    void camera() {

        saveLivingHint = photoHelper.takePhotoFromCamera(this);

    }

    @OptionsItem
    void add() {

//        photoHelper.takePhotoFromGallery(this);

        if (getFragmentManager().findFragmentByTag(SYSTEM_GALLERY_FRAGMENT_TAG) == null) {

            loadFragment(GalleryFragment.build(), true, SYSTEM_GALLERY_FRAGMENT_TAG);

        } else {

            loadFragment(ImageGridFragment.build(), false, IMAGES_FRAGMENT_TAG);

        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack, String name) {

        FragmentManager fragmentManager = getFragmentManager();

        // workaround that clear all stack
        if (!addToBackStack) {

            while (fragmentManager.getBackStackEntryCount() > 0) {

                fragmentManager.popBackStackImmediate();

            }
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(R.id.frame, fragment, name);

        if (addToBackStack) ft.addToBackStack(name);

        ft.commit();
    }

    public void secureNewPhotos(ArrayList<Integer> positions, Cursor cursor) {

        if (cursor == null || positions == null) {
            // TODO
            return;
        }

        final ArrayList<Image> images = new ArrayList<Image>(positions.size());

        for (int position : positions) {

            if (!cursor.moveToPosition(position)) continue;

            String path = "file://" + CursorUtils.getString(MediaStore.Images.Media.DATA, cursor);

            Image image = new Image(String.valueOf(System.currentTimeMillis()),
                    path,
                    CursorUtils.getString(BaseColumns._ID, cursor));

            images.add(image);

        }

        CursorUtils.close(cursor);

        loadFragment(ImageGridFragment.build(), false, IMAGES_FRAGMENT_TAG);

        API.data().cryptonize(images, new Procedure<Object>() {
            @Override
            public void apply(Object dialog) {

                for (Image image : images)
                    API.db().delete(Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + image.getOriginalContentId()), null, null);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        saveLivingHint = false;

        if (requestCode == REQUESTS.IMAGE_CAPTURE) {

            Uri uri = photoHelper.getCapturedPhotoUri();

            if (uri != null) {

                if (resultCode != Activity.RESULT_OK) {

                    new FileAsyncTask().deleteFile(uri);

                    return;
                }

                Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);

                ArrayList<Image> images = new ArrayList<Image>(1);

                images.add(image);

                API.data().cryptonize(images, null);
            }
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

    public static void start(Context context) {
        MainActivity_.intent(context).start();
    }
}
