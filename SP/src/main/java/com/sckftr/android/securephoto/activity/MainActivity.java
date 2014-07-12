package com.sckftr.android.securephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.ImageGridFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Procedure;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;

@EActivity
public class MainActivity extends BaseSPActivity {

    private static final int MENU_CAM = R.id.menu_camera;
    //    private static final int MENU_SHARE = R.id.menu_share;
    private static final int MENU_ADD = R.id.menu_add_items;

    private boolean saveLivingHint;

    @AfterViews void init() {

        addFragment(ImageGridFragment.build());
    }

    @Override protected void onResume() {
        super.onResume();
        if (!UserHelper.isLogged())
            StartActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
    }

    @Override protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case MENU_CAM:

                saveLivingHint = TakePhotoHelper.takePhotoFromCamera(this);

                return saveLivingHint;

            case MENU_ADD:

                saveLivingHint = true;

                TakePhotoHelper.takePhotoFromGallery(this);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        saveLivingHint = false;

        Uri uri = null;
        if (requestCode == TakePhotoHelper.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            uri = TakePhotoHelper.getImageUri(requestCode, resultCode);
            if (uri != null) {
                Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);
                ArrayList<Image> images = new ArrayList<Image>();
                images.add(image);
                API.data().cryptonize(images, null);
            }

        } else if (requestCode == TakePhotoHelper.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {

            String originalContentId = null;

            Object[] objects = TakePhotoHelper.resolveContent(data.getData());
            uri = (Uri) objects[0];
            originalContentId = (String) objects[1];

            Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);
            image.setOriginalContentId(originalContentId);
            ArrayList<Image> images = new ArrayList<Image>();
            images.add(image);

            final String contentId = originalContentId;
            final Uri contentUri = data.getData();

            API.data().cryptonize(images, new Procedure<Object>() {
                @Override public void apply(Object dialog) {
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

    @Override public void onBackPressed() {

        super.onBackPressed();

        if (isFinishing()) UserHelper.setIsLogged(false);

    }

    public static void start(Context context) {
        MainActivity_.intent(context).start();
    }
}
