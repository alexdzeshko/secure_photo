package com.sckftr.android.securephoto.helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.securephoto.db.Image;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.Platform;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;
import com.sckftr.android.utils.Strings;
import com.sckftr.android.utils.UI;

import org.androidannotations.annotations.EBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@EBean
public class PhotoHelper {

    private Uri mCapturedPhotoUri;

    private Context mContext;

    public PhotoHelper(Context context) {
        mContext = context;
    }

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
        }).createTempFile("JPEG_" + new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) + "_", ".jpg", Storage.Images.getPrivateFolder());

        return true;
    }


    // TODO: store on orientation changed
    public Uri getCapturedPhotoUri() {
        return mCapturedPhotoUri;
    }

    public void secureNewPhotos(ArrayList<Integer> positions, final Cursor cursor) {

        if (cursor == null || positions == null) {

            UI.showHint(mContext, R.string.ERR_SECURE_PHOTOS);

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

        AppConst.API.data().cryptonize(images, new Procedure<String>() {
            @Override
            public void apply(String dialog) {

                for (String id : originalContentIds)
                    AppConst.API.db().delete(Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id), null, null);

            }
        });
    }

    public void unSecurePhotos(ArrayList<Integer> positions, final Cursor cursor) {

        if (cursor == null || positions == null) {

            UI.showHint(mContext, R.string.ERR_UNSECURE_PHOTOS);

            return;
        }

        final ArrayList<Image> images = new ArrayList<Image>(positions.size());

        for (int position : positions) {

            if (!cursor.moveToPosition(position)) continue;

            Image image = new Image(cursor);

            images.add(image);

        }

        AppConst.API.data().uncryptonize(images, null);
    }

    public void deletePhotos(ArrayList<Integer> positions, final Cursor cursor) {

        if (cursor == null || positions == null) {

            UI.showHint(mContext, R.string.ERR_DELETE_PHOTOS);

            return;
        }

        final ArrayList<Image> images = new ArrayList<Image>(positions.size());

        for (int position : positions) {

            if (!cursor.moveToPosition(position)) continue;

            Image image = new Image(cursor);

            images.add(image);

        }

        AppConst.API.data().deleteFiles(images);
    }

}
