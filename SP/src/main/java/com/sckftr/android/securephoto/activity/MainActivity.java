package com.sckftr.android.securephoto.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.GalleryFragment;
import com.sckftr.android.securephoto.fragment.ImageGridFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.securephoto.image.CryptoBitmapSourceLoader;
import com.sckftr.android.securephoto.image.FileBitmapSourceLoader;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.UI;

import org.androidannotations.annotations.Bean;
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

    private FileBitmapSourceLoader mFileSourceLoader;
    private CryptoBitmapSourceLoader mCryptoSourceLoader;

    @Bean
    TakePhotoHelper photoHelper;

    private boolean saveLivingHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    public void startCamera() {

        saveLivingHint = photoHelper.takePhotoFromCamera(this);

    }

    @OptionsItem
    void add() {

        if (getFragmentManager().findFragmentByTag(SYSTEM_GALLERY_FRAGMENT_TAG) == null) {

            loadFragment(GalleryFragment.build(), true, SYSTEM_GALLERY_FRAGMENT_TAG);

        } else {

            loadFragment(ImageGridFragment.build(), false, IMAGES_FRAGMENT_TAG);

        }
    }

    public void toggleSourceLoader(boolean secured) {

        API.images().setBitmapSourceLoader(secured ?
                mCryptoSourceLoader == null ? new CryptoBitmapSourceLoader() : mCryptoSourceLoader :
                mFileSourceLoader == null ? new FileBitmapSourceLoader() : mFileSourceLoader);

    }

    public void secureNewPhotos(ArrayList<Integer> positions, final Cursor cursor) {

        if (cursor == null || positions == null) {

            UI.showHint(this, R.string.ERR_secure_photos);

            return;
        }

        int size = positions.size(), index = 0;

        final ArrayList<Image> images = new ArrayList<Image>(size);

        final String[] originalContentIds = new String[size];

        for (int position : positions) {

            if (!cursor.moveToPosition(position)) continue;

            String path = "file://" + CursorUtils.getString(MediaStore.Images.Media.DATA, cursor);

            Image image = new Image(String.valueOf(System.currentTimeMillis()), path);

            images.add(image);

            originalContentIds[index++] = CursorUtils.getString(MediaStore.Images.Media._ID, cursor);
        }

        CursorUtils.close(cursor);

        loadFragment(ImageGridFragment.build(), false, IMAGES_FRAGMENT_TAG);

        encrypt(images, new Procedure<String>() {
            @Override
            public void apply(String dialog) {

                for (String id : originalContentIds)
                    API.db().delete(Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id), null, null);

            }
        });
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

                encrypt(images, null);
            }
        }
    }

    private void encrypt(ArrayList<Image> images, Procedure<String> p) {
        API.data().cryptonize(images, p);
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
