package com.sckftr.android.securephoto.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.IO;

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

    private static Uri sCurrentUri;

    public static void takePhotoFromCamera(Activity activity) {

//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(createImageFile());
        AppConst.Log.d("temp_file", imageUri.toString());
        sCurrentUri = imageUri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public static void takePhotoFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    public static Uri getImageUri(int requestCode, int resultCode) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            return sCurrentUri;
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            deleteImage(sCurrentUri);
            return null;
        }
        return null;
    }

    public static void deleteImage(Uri uri) {
        //FIXME delete as per stackoverflow
        // or may be not because it is a simply temp file
        // refactor anyway
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

    private static File getAlbum() {
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                DIR_IMAGES);

        File storageDir = new File(IO.getExternalDir(), DIR_IMAGES);
        if (!storageDir.exists()) {
            if(!storageDir.mkdirs()){
                AppConst.Log.e("STORAGE", "Directory not created");
            }
        }
        return storageDir;

    }


    private static File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp.hashCode() + "_";
        try {

            File album = getAlbum();

            return File.createTempFile(imageFileName, ".jpg", album);

        } catch (IOException e) {

            AppConst.Log.e("STORAGE", "temp file", e);

        }
        return null;
    }

    public static String getPath(Uri uri, Context context) {

        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);

        if (cursor == null) return null;

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        final String path = cursor.getString(columnIndex);

        CursorHelper.close(cursor);

        AppConst.Log.d("image_path", "uri=%s, path=%s", uri, path);

        return "file://" + path;
    }


}
