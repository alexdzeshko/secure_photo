package com.sckftr.android.securephoto.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.GalleryFragment;
import com.sckftr.android.securephoto.fragment.ImageGridFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.util.ArrayList;

@EActivity
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends BaseSPActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String IMAGES_FRAGMENT_TAG = "IMAGES";
    public static final String SYSTEM_GALLERY_FRAGMENT_TAG = "SYSTEM_GALLERY";
    public static final String DETAIL_IMAGE_FRAGMENT_TAG = "DETAIL_IMAGE";

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

        saveLivingHint = TakePhotoHelper.takePhotoFromCamera(this);

    }

    @OptionsItem
    void add() {

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

    public void secureNewPhotos(ArrayList<Image> images) {
        for (Image image : images) {
            Log.d(TAG, image.getFileUri() + ", id = " + image.get_id());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        saveLivingHint = false;


        if (requestCode == REQUESTS.IMAGE_CAPTURE) {

            Uri uri = TakePhotoHelper.getImageUri(requestCode, resultCode);

            if (uri != null) {

                Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);

                // TODO remove
                ArrayList<Image> images = new ArrayList<Image>();

                images.add(image);

                API.data().cryptonize(images, null);
            }

        } else if (requestCode == REQUESTS.IMAGE_GALLERY && resultCode == RESULT_OK) {

            Object[] objects = Storage.resolveContent(data.getData());

            Uri uri = (Uri) objects[0];
            String originalContentId = (String) objects[1];

            Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);
            image.setOriginalContentId(originalContentId);

            // TODO remove
            ArrayList<Image> images = new ArrayList<Image>();
            images.add(image);

            final String contentId = originalContentId;
            final Uri contentUri = data.getData();

            API.data().cryptonize(images, new Procedure<Object>() {
                @Override
                public void apply(Object dialog) {
                    API.db().delete(contentUri, BaseColumns._ID + "=" + contentId, null);
//                    UI.showHint(MainActivity.this, "result received " + contentUri + " " + contentId);
                }
            });
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
