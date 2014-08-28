package com.sckftr.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.sckftr.android.securephoto.AppConst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Storage {

    private static final String TAG = Storage.class.getSimpleName();

    public static final String NOMEDIA = ".nomedia";

    private static File createDir(File dir, String path, boolean includeNoMedia) {

        return createIfNotExists(new File(dir, path), includeNoMedia);

    }

    private static File createIfNotExists(File file, boolean includeNoMedia) {

        if (!file.exists()) {

            if (!file.mkdirs()) {

                AppConst.Log.e(TAG, "Directory not created");

            } else if (includeNoMedia) {

                new File(file, NOMEDIA).mkdir();

            }
        }

        return file;
    }

    public static void deleteFileIfPublic(Uri uri) {

        Uri secureUri = Images.getPrivateUri(uri);//todo storage images

        AppConst.Log.d(TAG, "orig_uri: %s, sec_uri: %s", uri, secureUri);

        if (!secureUri.equals(uri)) deleteFileSync(uri);

    }

    public static void deleteFileSync(Uri uri) {

        if (uri != null) {

            File f = new File(uri.getPath());

            if (f.exists()) f.delete();
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
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(uri));
    }
}
