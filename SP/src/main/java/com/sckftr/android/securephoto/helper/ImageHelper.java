package com.sckftr.android.securephoto.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.IO;

import java.io.FileInputStream;

/**
 * Created by Aliaksei_Dziashko on 12/18/13.
 */
public class ImageHelper implements AppConst {

    public static final String TAG = ImageHelper.class.getSimpleName();

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public static void loadEncryptedFile(String key, String uri, ImageView imageView) {

        FileInputStream stream = null;
        try {
            //todo big refactor
            stream = new FileInputStream(uri);
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            byte[] decr = Cryptograph.decrypt(buffer, key);

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(decr, 0, decr.length, options);

            options.inJustDecodeBounds = false;

            options.inSampleSize = ImageHelper.calculateInSampleSize(options, imageView.getWidth(), imageView.getHeight());

            options.inPurgeable = true;
            options.inMutable = true;

            Log.d(TAG, "inSampleSize=%s", options.inSampleSize);

            Bitmap bitmap = BitmapFactory.decodeByteArray(decr, 0, decr.length, options);

            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {

            Log.e(TAG, "encrypted file load error", e);

        } finally {

            IO.close(stream);

        }
    }
}
