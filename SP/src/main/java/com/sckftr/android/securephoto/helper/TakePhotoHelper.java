package com.sckftr.android.securephoto.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.sckftr.android.securephoto.AppConst;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TakePhotoHelper {

    private static final String DIR = ".secure_cam";
    public static final int KEY_CAMERA_REQUEST = 124;
    public static final int KEY_IMAGE_REQUEST = 123;

    private static Uri sCurrentUri;

    public static void takePhotoFromCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(createImageFile());
        sCurrentUri = imageUri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, KEY_CAMERA_REQUEST);
    }

    public static void takePhotoFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, KEY_IMAGE_REQUEST);
    }

    public static Uri getImageUri(int requestCode, int resultCode) {
        if (requestCode == KEY_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            return sCurrentUri;
        } else if (requestCode == KEY_CAMERA_REQUEST) {
            deleteImage(sCurrentUri);
            return null;
        }
        return null;
    }

    public static void deleteImage(Uri uri) {
        File file = new File(uri.getPath());
        if (file.exists()) {
            if (!file.delete()) {
                AppConst.Log.w("File", "file not deleted " + uri);
            }
        } else {
            AppConst.Log.w("File", "file does not exist: " + uri);
        }
    }

    public static List<Uri> getAllImages() {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                DIR);
        File[] files = storageDir.listFiles();
        List<Uri> uris = new ArrayList<Uri>(files.length);
        for (File file : files) {
            uris.add(Uri.fromFile(file));
        }
        return uris;
    }

    private static File getAlbum() {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                DIR);
        if (storageDir.exists()) {
            return storageDir;
        } else {
            storageDir.mkdirs();
            return storageDir;
        }
    }

    private static File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp.hashCode() + "_";
        try {
            File album = getAlbum();
            return File.createTempFile(imageFileName, ".jpg", album);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPath(Uri uri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver()
                .query(uri, proj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        final String path = cursor.getString(columnIndex);
        cursor.close();
        return "file://" + path;
    }


}
