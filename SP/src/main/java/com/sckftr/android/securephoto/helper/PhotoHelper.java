package com.sckftr.android.securephoto.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import com.sckftr.android.app.activity.BaseActivity;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.securephoto.data.FileAsyncTask;
import com.sckftr.android.securephoto.db.Cryptonite;
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
public class PhotoHelper implements AppConst {

    public static final String EXTRA_NEW_PHOTO = "com.sckftr.android.securephoto.helper.EXTRA_NEW_PHOTO";

    private Context mContext;

    public PhotoHelper(Context context) {
        mContext = context;
    }

    public boolean takePhotoFromCamera(final BaseActivity activity) {

        if (!Platform.hasCamera(activity)) return false;

        new FileAsyncTask(new Procedure<Bundle>() {
            @Override
            public void apply(Bundle params) {

                String error = params.getString(FileAsyncTask.FILE_ERROR);

                if (Strings.isEmpty(error)) {

                    Uri uri = params.getParcelable(FileAsyncTask.FILE_URI);

                    activity.getParams().putParcelable(EXTRA_NEW_PHOTO, uri);

                    API.get().camera(activity, REQUESTS.IMAGE_CAPTURE, uri);

                } else {

                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();

                }
            }
        }).createTempFile("JPEG_" + new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) + "_", ".jpg", Storage.Images.getPrivateFolder());

        return true;
    }

    public void secureNewPhotos(SparseBooleanArray items, final Cursor cursor, final Procedure<String> onFinished) {

        if (cursor == null || items == null) {

            UI.showHint(mContext, R.string.ERR_SECURE_PHOTOS);

            return;
        }

        int size = items.size();

        final ArrayList<Image> images = new ArrayList<Image>(size);
        final ArrayList<String> contentIds = new ArrayList<String>(size);

        for (int i = 0; i < items.size(); i++) {

            int key = items.keyAt(i);

            if (items.get(key, false)) {

                if (!cursor.moveToPosition(key)) continue;

                String path = "file://" + CursorUtils.getString(MediaStore.Images.Media.DATA, cursor);

                Image image = new Image(String.valueOf(System.currentTimeMillis()), path);

                images.add(image);

                contentIds.add(CursorUtils.getString(MediaStore.Images.Media._ID, cursor));
            }
        }

        contentIds.trimToSize();

        CursorUtils.close(cursor);

        API.data().cryptonize(images, new Procedure<String>() {
            @Override
            public void apply(String dialog) {

                for (String id : contentIds)
                    API.db().delete(Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id), null, null);

                onFinished.apply(dialog);

            }
        });
    }

    public void unSecurePhotos(SparseBooleanArray items, final Cursor cursor) {

        if (cursor == null || items == null) {

            UI.showHint(mContext, R.string.ERR_UNSECURE_PHOTOS);

            return;
        }

        final ArrayList<Image> images = new ArrayList<Image>(items.size());

        for (int i = 0; i < items.size(); i++) {

            int key = items.keyAt(i);

            if (items.get(key, false)) {

                if (!cursor.moveToPosition(key)) continue;

                Image image = new Image(cursor);

                images.add(image);
            }
        }

        API.data().uncryptonize(images, null);
    }

    public void deletePhotos(SparseBooleanArray items, final Cursor cursor) {

        if (cursor == null || items == null) {

            UI.showHint(mContext, R.string.ERR_DELETE_PHOTOS);

            return;
        }

        final ArrayList<Image> images = new ArrayList<Image>(items.size());

        for (int i = 0; i < items.size(); i++) {

            int key = items.keyAt(i);

            if (items.get(key, false)) {

                if (!cursor.moveToPosition(key)) continue;

                Image image = new Image(cursor);

                images.add(image);
            }
        }

        API.data().deleteFiles(images);
    }

    public void restorePhotos(Cursor data) {
        if (data == null || isPhotosRestoring()) return;

        setPhotosRestoring(true);

        ArrayList<Cryptonite> items = new ArrayList<Cryptonite>(data.getCount());

        for (int i = 0; i < data.getCount(); i++) {

            if (data.moveToPosition(i)) items.add(new Image(data));

        }

        API.data().recryptonize(items, new Procedure<Object>() {
            @Override
            public void apply(Object dialog) {
                setPhotosRestoring(false);
            }
        });
    }

    public void setPhotosRestoring(boolean restoring) {
        API.get().putPreference(KEYS.PREF_PHOTOS_RESTORING, restoring);
    }

    public boolean isPhotosRestoring() {
        return API.get().getPreferenceBool(KEYS.PREF_PHOTOS_RESTORING, false);
    }

}
