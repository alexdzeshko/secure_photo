package com.sckftr.android.securephoto.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.processor.Crypto;
import com.sckftr.android.utils.IO;

import java.io.FileInputStream;

/**
 * Created by Aliaksei_Dziashko on 12/18/13.
 */
public class ImageHelper implements AppConst{

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final String TAG = ImageHelper.class.getSimpleName();

    public static int getScaleFactor(Uri imageUri, int viewWidth, int viewHeight) {

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(imageUri.getPath());
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            return getScaleFactor(buffer, viewWidth, viewHeight);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } finally {
            IO.close(stream);
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
        Log.d(TAG, "width: %s, height: %s", imageWidth, imageHeight);
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

    public static void loadEncryptedFile(String key, String uri, ImageView imageView){

        FileInputStream stream = null;
        try {
            //todo big refactor
            stream = new FileInputStream(uri);
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            byte[] decr = Crypto.decrypt(buffer, key);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = ImageHelper.getScaleFactor(decr, WIDTH, HEIGHT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decr, 0, decr.length,options);
            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            Log.e(TAG, "encrypted file load error", e);
        } finally {
            IO.close(stream);
        }
    }
}
