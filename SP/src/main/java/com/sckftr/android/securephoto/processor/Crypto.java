package com.sckftr.android.securephoto.processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.sckftr.android.securephoto.AppConst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import by.deniotokiari.core.context.ContextHolder;
import by.deniotokiari.core.utils.IOUtils;

public class Crypto {

    public static final String TAG = Crypto.class.getSimpleName();

    //MAIN XOR METHOD
	public static byte[] encrypt(byte[] arr, String keyWord) {
		byte[] keyarr = keyWord.getBytes();
		byte[] result = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i] = (byte) (arr[i] ^ keyarr[i % keyarr.length]);
		}
		return result;
	}

    //MAIN XOR METHOD
    public static byte[] decrypt(byte[] text, String keyWord) {
        byte[] result = new byte[text.length];
        byte[] keyarr = keyWord.getBytes();
        for (int i = 0; i < text.length; i++) {
            result[i] = (byte) (text[i] ^ keyarr[i % keyarr.length]);
        }
        return result;
    }


    public static boolean encrypt(Bitmap bitmap, Uri fileUri, String key){
        if (bitmap == null || key == null || fileUri == null) {
            AppConst.Log.w(TAG, "encrypt with null: bitmap-%s, uri-%s, key-%s", bitmap==null, fileUri==null, key==null);
            return false;
        }

        boolean result;
        Context context = ContextHolder.getInstance().getContext();

        ByteArrayOutputStream baos = null;
        FileOutputStream fileOutputStream = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            bitmap.recycle();

            byte[] b = baos.toByteArray();
            byte[] encryptedData = encrypt(b, key);
            File file = new File(fileUri.getPath());
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(encryptedData);
            result = true;
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            result = false;
        } finally {
            IOUtils.closeStream(baos);
            IOUtils.closeStream(fileOutputStream);
        }

        return result;
    }

}
