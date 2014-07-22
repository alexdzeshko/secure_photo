package com.sckftr.android.securephoto.helper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.data.FileThread;
import com.sckftr.android.utils.IO;
import com.sckftr.android.utils.Platform;

public class TakePhotoHelper {

    private static final String TAG = TakePhotoHelper.class.getSimpleName();

    private static Uri sCurrentUri;

    public static boolean takePhotoFromCamera(final Activity activity) {

        if (!Platform.hasCamera(activity)) return false;

        new FileThread(new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == FileThread.MESSAGE_SUCCESS) {

                    Bundle data = msg.getData();

                    String s = data.getString(FileThread.CREATE_FILE_RESULT);

                    Uri uri = Uri.parse(s);

                    sCurrentUri = uri;

                    AppConst.API.get().camera(activity, AppConst.REQUESTS.IMAGE_CAPTURE, uri);

                } else {

                    Toast.makeText(activity, "Can't create new photo!!!", Toast.LENGTH_LONG).show();

                }
            }
        }, FileThread.Task.CREATE_TEMP_FILE).start();

        return true;
    }

    public static void takePhotoFromGallery(Activity activity) {

        AppConst.API.get().gallery(activity, AppConst.REQUESTS.IMAGE_GALLERY);

    }

    public static Uri getImageUri(int requestCode, int resultCode) {

        if (resultCode != Activity.RESULT_OK) {

            IO.delete(sCurrentUri);

            return null;
        }

        return requestCode == AppConst.REQUESTS.IMAGE_CAPTURE ? sCurrentUri : null;
    }
}
