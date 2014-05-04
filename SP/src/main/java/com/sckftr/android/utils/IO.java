package com.sckftr.android.utils;

import android.net.Uri;
import android.os.Environment;

import com.sckftr.android.securephoto.AppConst;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class IO {

    public static final float MIN_ACCEPTABLE_SPACE = 50 * 1024 * 1024;
    private static final String TAG = IO.class.getSimpleName();

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                //can be ignored
            }
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getStorage(boolean externalIfExists) {

        if (externalIfExists && isExternalStorageWritable() &&
                Environment.getExternalStorageDirectory().getFreeSpace() >= MIN_ACCEPTABLE_SPACE) {

            return Environment.getExternalStorageDirectory();

        } else {

            return Environment.getDataDirectory();
        }

    }

    public static File getExternalDir(){
        return Environment.getExternalStorageDirectory();
//        return ContextHolder.getInstance().getContext().getExternalFilesDir(null);
    }

    public static File getExternalDirPublic() {
        File publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        publicDirectory.mkdirs();
        return publicDirectory;
    }

    public static void delete(Uri uri) {
        File file = new File(uri.getPath());
        if (file.exists()) {
            AppConst.Log.d(TAG, "file: %s, deleted: %s ", uri, file.delete());

        } else {
            AppConst.Log.w(TAG, "file does not exist: " + uri);
        }
    }
}
