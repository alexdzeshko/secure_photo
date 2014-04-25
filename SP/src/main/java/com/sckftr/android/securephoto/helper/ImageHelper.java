package com.sckftr.android.securephoto.helper;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.sckftr.android.securephoto.AppConst;

import java.io.FileInputStream;

import by.deniotokiari.core.utils.IOUtils;

/**
 * Created by Aliaksei_Dziashko on 12/18/13.
 */
public class ImageHelper {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    public static int getScaleFactor(Uri imageUri, int viewWidth, int viewHeight) {

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(imageUri.getPath());
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            return getScaleFactor(buffer, viewWidth, viewHeight);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(ImageHelper.class.getSimpleName(), e.toString());
        } finally {
            IOUtils.closeStream(stream);
        }

        return 1;
    }

    public static int getScaleFactor(byte[] buffer, int viewWidth, int viewHeight){
        int ratio = 1;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(buffer, 0, buffer.length, opts);
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        AppConst.Log.d("image", "width: %s, height: %s", imageWidth, imageHeight);
        if (imageHeight > viewHeight || imageWidth > viewWidth) {
            int hRatio = Math.round((float) imageHeight / (float) viewHeight);
            int wRatio = Math.round((float) imageWidth / (float) viewWidth);
            ratio = hRatio > wRatio ? hRatio : wRatio;
        }
        return ratio > 1 ? ratio : 1;
    }
    public static int getScaleFactor(Uri imageUri) {
        return getScaleFactor(imageUri, WIDTH, HEIGHT);
    }

    public static void setBitmapByUri(Uri uri, ImageView view) {
        // big todo
//        InputStream inputStream = null;
//        try {
//            inputStream = ContextHolder.getInstance().getContext()
//                    .getContentResolver().openInputStream(uri);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            int viewWidth = view.getWidth();
//            int viewHeight = view.getHeight();
//            int scale = ImageHelper.getScaleFactor(uri, viewWidth > 0 ? viewWidth : WIDTH, viewHeight > 0 ? viewHeight : HEIGHT);
//            options.inSampleSize = scale;
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
//                    options);
//            view.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            Log.e(ImageHelper.class.getSimpleName(), e.toString());
//        } finally {
//            IOUtils.closeStream(inputStream);
//        }
        AppConst.API.images().load(uri).into(view);
    }
}
