package com.sckftr.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.processor.Cryptograph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import by.deniotokiari.core.helpers.CursorHelper;

public class Storage {

    private static final String TAG = Storage.class.getSimpleName();


    public static final String NOMEDIA = ".nomedia";

    private static File createDir(File dir, String path, boolean includeNomedia) {
        File file = new File(dir, path);
        return createIfNotExists(file, includeNomedia);
    }

    private static File createIfNotExists(File file, boolean includeNomedia) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                AppConst.Log.e("STORAGE", "Directory not created");
            } else if (includeNomedia) {
                File nomedia = new File(file, NOMEDIA);
                nomedia.mkdir();
            }
        }
        return file;
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


    public static void deleteFileIfPublic(Uri uri) {
        Uri secureUri = Images.getPrivateUri(uri);//todo storage images
        AppConst.Log.d(Cryptograph.TAG, "orig_uri: %s, sec_uri: %s", uri, secureUri);

        if (!secureUri.equals(uri)) {

            IO.delete(uri);

//            scanFile(uri);

        }
    }

    public static class Images {

        private static final String DIR_IMAGES_SECURE = ".secure_cam";
        private static final String DIR_IMAGES = "Images";


        public static File getPrivateFolder() {

            return createDir(IO.getExternalDir(), DIR_IMAGES_SECURE, true);

        }

        public static File getPublicFolder() {
            return createDir(IO.getExternalDirPublic(), DIR_IMAGES, false);
        }

        public static Uri getPrivateUri(Uri unprivateUri) {

            Uri secureUri = Uri.parse(Uri.fromFile(getPrivateFolder()).toString() + File.separator + unprivateUri.getLastPathSegment());

            AppConst.Log.d(TAG, "original_uri: %s", unprivateUri);
            AppConst.Log.d(TAG, "private_uri: %s", secureUri);

            return secureUri;
        }

        public static Uri getPublicUri(Uri uri) {

            Uri publicUri = Uri.parse(Uri.fromFile(getPublicFolder()).toString() + File.separator + uri.getLastPathSegment());

            AppConst.Log.d(TAG, "original_uri: %s", uri);
            AppConst.Log.d(TAG, "public_uri: %s", publicUri);

            return publicUri;
        }

        public static File getPublicFile(Uri uri) throws IOException {
            File file = new File(getPublicFolder(), uri.getLastPathSegment());
            file.createNewFile();
            return file;
        }

        public static ArrayList<Uri> getAllImages() {
            // todo get both internal and external
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIR_IMAGES_SECURE);

            File[] files = storageDir.listFiles();

            ArrayList<Uri> uris = new ArrayList<Uri>(files.length);

            for (File file : files) {

                uris.add(Uri.fromFile(file));

            }

            return uris;
        }
    }

    public static void scanFile(Context context, Uri uri) {
        MediaScannerConnection.scanFile(context, new String[]{uri.getPath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        AppConst.Log.d(TAG, "Scanned " + path + ":");
                        AppConst.Log.d(TAG, "-> uri=" + uri);
                    }
                }
        );
    }
}
