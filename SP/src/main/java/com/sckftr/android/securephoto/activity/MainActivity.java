package com.sckftr.android.securephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sckftr.android.app.activity.BaseActivity;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.securephoto.fragment.ImageGridFragment;
import com.sckftr.android.securephoto.helper.TakePhotoHelper;
import com.sckftr.android.securephoto.helper.UserHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.frame)
public class MainActivity extends BaseActivity {

    private static final int MENU_CAM = R.id.menu_camera;
    private static final int MENU_SHARE = R.id.menu_share;
    private static final int MENU_ADD = R.id.menu_add_items;

    @AfterViews void init() {

        addFragment(ImageGridFragment.build());
    }

    @Override protected void onResume() {
        super.onResume();
//        if(!UserHelper.isLogged(this)) StartActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
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
                return TakePhotoHelper.takePhotoFromCamera(this);

            case MENU_SHARE:

                return false;
            case MENU_ADD:
                TakePhotoHelper.takePhotoFromGallery(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if (requestCode == TakePhotoHelper.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uri = TakePhotoHelper.getImageUri(requestCode, resultCode);

        } else if (requestCode == TakePhotoHelper.REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = TakePhotoHelper.getPath(data.getData(), this);
        }
        if (uri != null) {
            Image image = new Image(String.valueOf(System.currentTimeMillis()), uri);
            API.data().cryptonize(image, null);
//            PrepareActivity.start(this, image);

        }

    }

    @Override
    protected void onUserLeaveHint() {
        UserHelper.setIsLogged(this, false);
        super.onUserLeaveHint();
    }

    public static void start(Context context) {
        MainActivity_.intent(context).start();
    }
}
