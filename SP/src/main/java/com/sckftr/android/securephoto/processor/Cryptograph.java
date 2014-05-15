package com.sckftr.android.securephoto.processor;

import android.content.Context;
import android.net.Uri;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.IO;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import androidkeystore.android.security.KeyStoreManager;
import by.deniotokiari.core.context.ContextHolder;

public class Cryptograph {

    public static final String TAG = Cryptograph.class.getSimpleName();

    //MAIN XOR METHOD
    private static byte[] encrypt(byte[] arr, String keyWord) {

        keyWord += UserHelper.getUserHash();

        byte[] keyarr = keyWord.getBytes();
        byte[] result = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (byte) (arr[i] ^ keyarr[i % keyarr.length]);
        }
        return result;
    }

    //MAIN XOR METHOD
    public static byte[] decrypt(byte[] text, String keyWord) {

        keyWord += UserHelper.getUserHash();

        byte[] result = new byte[text.length];
        byte[] keyarr = keyWord.getBytes();
        for (int i = 0; i < text.length; i++) {
            result[i] = (byte) (text[i] ^ keyarr[i % keyarr.length]);
        }
        return result;
    }


    public static boolean encrypt(Uri fileUri, String key) {

        boolean result;

        Context context = ContextHolder.getInstance().getContext();

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(fileUri);

            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            byte[] encryptedData = encrypt(buffer, key);

            Uri secureUri = Storage.Images.getPrivateUri(fileUri);//todo storage images

            File file = new File(secureUri.getPath());

            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(encryptedData);
            result = true;

        } catch (Exception e) {
            AppConst.Log.e(TAG, "when encrypt", e);
            result = false;
        } finally {
            IO.close(inputStream);
            IO.close(fileOutputStream);
        }

        return result;
    }

    public void storeKey(final String alias, final String key) {


        KeyStoreManager.put(alias, key, new Procedure<String>() {
            @Override public void apply(String result) {
                if (result != null && result.equals(KeyStoreManager.ERROR_LOCKED)) {

                    AppConst.API.get().putPreference(alias, key);

                }
            }
        });

    }

    public void getKey(final String alias, final Procedure<String> resultCallback) {

        KeyStoreManager.get(alias, new Procedure<String>() {
            @Override public void apply(String result) {

                if (result != null && result.equals(KeyStoreManager.ERROR_LOCKED)) {

                    resultCallback.apply(AppConst.API.get().getPreferenceString(alias, null));
                    return;

                }

                resultCallback.apply(result);

            }
        });

    }

    public void deleteKey(String alias) {

        KeyStoreManager.deleteEntries();

        AppConst.API.get().putPreference(alias, null);
    }


//    public static boolean encrypt(Bitmap bitmap, Uri fileUri, String key) {
//
//        if (bitmap == null || key == null || fileUri == null) {
//            AppConst.Log.w(TAG, "encrypt with null: bitmap-%s, uri-%s, key-%s", bitmap == null, fileUri == null, key == null);
//            return false;
//        }
//
//        boolean result;
//        Context context = ContextHolder.getInstance().getContext();
//
//        ByteArrayOutputStream baos = null;
//        FileOutputStream fileOutputStream = null;
//        try {
//            baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
//            bitmap.recycle();
//            byte[] b = baos.toByteArray();
//
//            byte[] encryptedData = encrypt(b, key);
//
//            File file = new File(fileUri.getPath());
//            fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(encryptedData);
//            result = true;
//        } catch (Exception e) {
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
//            result = false;
//        } finally {
//            IO.close(baos);
//            IO.close(fileOutputStream);
//        }
//
//        return result;
//    }
}