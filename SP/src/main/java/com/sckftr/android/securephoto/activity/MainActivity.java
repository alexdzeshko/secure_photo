package com.sckftr.android.securephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseSPActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.ImageGridFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;
import com.sckftr.android.utils.Strings;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.util.ArrayList;

@EActivity
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends BaseSPActivity {

    private static final int MENU_CAM = R.id.camera;
    //private static final int MENU_SHARE = R.id.menu_share;
    private static final int MENU_ADD = R.id.add;

    private boolean saveLivingHint;

    @AfterViews
    void init() {
        addFragment(ImageGridFragment.build());
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

        setIntent(intent);

    }

    @OptionsItem
    void camera() {

        saveLivingHint = TakePhotoHelper.takePhotoFromCamera(this);

    }

    @OptionsItem
    void add() {

        saveLivingHint = true;

        TakePhotoHelper.takePhotoFromGallery(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        saveLivingHint = false;


        if (requestCode == REQUEST.IMAGE_CAPTURE) {

            Uri uri = TakePhotoHelper.getImageUri(requestCode, resultCode);

            if (uri != null) {

                Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);

                // TODO remove
                ArrayList<Image> images = new ArrayList<Image>();

                images.add(image);

                API.data().cryptonize(images, null);
            }

        } else if (requestCode == REQUEST.IMAGE_GALLERY && resultCode == RESULT_OK) {

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
