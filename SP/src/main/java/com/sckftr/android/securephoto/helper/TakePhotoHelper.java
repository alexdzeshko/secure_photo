package com.sckftr.android.securephoto.helper;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.IO;
import com.sckftr.android.utils.Platform;
import com.sckftr.android.utils.Storage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import by.deniotokiari.core.helpers.CursorHelper;

public class TakePhotoHelper {

    private static final String DIR_IMAGES = ".secure_cam";
    public static final int REQUEST_IMAGE_CAPTURE = 124;
    public static final int REQUEST_IMAGE_GALLERY = 123;
    private static final String TAG = TakePhotoHelper.class.getSimpleName();

    private static Uri sCurrentUri;

    public static boolean takePhotoFromCamera(Activity activity) {
        if (!Platform.hasCamera(activity)) return false;

        Uri imageUri = Uri.fromFile(createImageFile());

        sCurrentUri = imageUri;

        AppConst.API.get().camera(activity, REQUEST_IMAGE_CAPTURE, imageUri);

        return true;
    }

    public static void takePhotoFromGallery(Activity activity) {
        AppConst.API.get().gallery(activity, REQUEST_IMAGE_GALLERY);
    }

    public static Uri getImageUri(int requestCode, int resultCode) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            return sCurrentUri;
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            IO.delete(sCurrentUri);
            return null;
        }
        return null;
    }

    public static List<Uri> getAllImages() {
        // todo get both internal and external
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                DIR_IMAGES);
        File[] files = storageDir.listFiles();
        List<Uri> uris = new ArrayList<Uri>(files.length);
        for (File file : files) {
            uris.add(Uri.fromFile(file));
        }
        return uris;
    }

    private static File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        try {

            File album = Storage.Images.getPrivateFolder();

            File tempFile = File.createTempFile(imageFileName, ".jpg", album);

            AppConst.Log.d("temp_file", Uri.fromFile(tempFile).toString());

            return tempFile;

        } catch (IOException e) {

            AppConst.Log.e("STORAGE", "temp_file", e);

        }
        return null;
    }

    public static Object[] resolveContent(Uri uri) {

        Object[] content = new Object[2];

        String[] proj = {MediaStore.Images.Media.DATA, BaseColumns._ID};

        Cursor cursor = AppConst.API.db().query(uri, proj);

        if (cursor == null) return null;

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        final String path = cursor.getString(columnIndex);

        content[0] = Uri.parse("file://" + path);

        AppConst.Log.d(TAG, "gallery img uri: %s, path: %s", uri, content[0]);

        content[1] = CursorUtils.getString(BaseColumns._ID, cursor);

        CursorHelper.close(cursor);

        return content;
    }


}
