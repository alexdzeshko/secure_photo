package com.sckftr.android.utils;

import android.media.MediaScannerConnection;
import android.net.Uri;

import com.sckftr.android.securephoto.AppConst;

import java.io.File;
import java.io.IOException;

import by.deniotokiari.core.context.ContextHolder;

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

        public static Uri getPublicUri(Uri uri){

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
    }

    public static void scanFile(Uri uri){
        MediaScannerConnection.scanFile(ContextHolder.getInstance().getContext(), new String[]{uri.getPath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        AppConst.Log.d(TAG, "Scanned " + path + ":");
                        AppConst.Log.d(TAG, "-> uri=" + uri);
                    }
                }
        );
    }
}
