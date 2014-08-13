package com.sckftr.android.securephoto.helper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.utils.Platform;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;
import com.sckftr.android.utils.Strings;

import org.androidannotations.annotations.EBean;

import java.text.SimpleDateFormat;
import java.util.Date;

@EBean
public class TakePhotoHelper {

    private static final String TAG = TakePhotoHelper.class.getSimpleName();

    private Uri mCapturedPhotoUri;

    public boolean takePhotoFromCamera(final Activity activity) {

        if (!Platform.hasCamera(activity)) return false;

        new FileAsyncTask(new Procedure<Bundle>() {
            @Override
            public void apply(Bundle params) {

                String error = params.getString(FileAsyncTask.FILE_ERROR);

                if (Strings.isEmpty(error)) {

                    Uri uri = params.getParcelable(FileAsyncTask.FILE_URI);

                    mCapturedPhotoUri = uri;

                    AppConst.API.get().camera(activity, AppConst.REQUESTS.IMAGE_CAPTURE, uri);

                } else {

                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

                }
            }
        }).createTempFile(new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) + "_", ".jpg", Storage.Images.getPrivateFolder());

        return true;
    }

    public Uri getCapturedPhotoUri() {
        return mCapturedPhotoUri;
    }
}
