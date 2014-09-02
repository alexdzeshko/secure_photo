package com.sckftr.android.securephoto.processor;

import android.content.Context;
import android.net.Uri;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.helper.UserHelper;
import com.sckftr.android.utils.Storage;
import com.sckftr.android.utils.Strings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cryptograph {

    private static final String TAG = Cryptograph.class.getSimpleName();

    public static boolean encrypt(Context ctx, Uri uri, String key) {

        if (ctx == null || uri == null)
            throw new IllegalArgumentException(TAG + ": Encryption is impossible. Bad source!!");

        if (Strings.isEmpty(key))
            throw new IllegalArgumentException(TAG + ": Encryption is impossible: Illegal key!!");

        InputStream is = null;

        try {

            is = ctx.getContentResolver().openInputStream(uri);

            Uri secureUri = Storage.Images.getPrivateUri(uri);//todo storage images

            return encrypt(ctx, secureUri, IOUtils.toByteArray(is), key);

        } catch (IOException e) {
            AppConst.Log.e(TAG, "Encrypt", e);
            return false;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static boolean encrypt(Context ctx, Uri securedUri, byte[] source, String key) {

        if (ctx == null || source == null)
            throw new IllegalArgumentException(TAG + ": Encryption is impossible. Bad source!!");

        if (Strings.isEmpty(key))
            throw new IllegalArgumentException(TAG + ": Encryption is impossible: Illegal key!!");

        key += UserHelper.getUserHash();

        try {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(key));

            File file = new File(securedUri.getPath());

            FileUtils.writeByteArrayToFile(file, cipher.doFinal(source));

        } catch (IOException e) {
            AppConst.Log.e(TAG, "Encrypt", e);
            return false;
        } catch (IllegalBlockSizeException e) {
            AppConst.Log.e(TAG, "Encrypt: ", e);
            return false;
        } catch (InvalidKeyException e) {
            AppConst.Log.e(TAG, "Encrypt: ", e);
            return false;
        } catch (BadPaddingException e) {
            AppConst.Log.e(TAG, "Encrypt: ", e);
            return false;
        } catch (NoSuchAlgorithmException e) {
            AppConst.Log.e(TAG, "Encrypt: ", e);
            return false;
        } catch (NoSuchPaddingException e) {
            AppConst.Log.e(TAG, "Encrypt: ", e);
            return false;
        } catch (NoSuchProviderException e) {
            AppConst.Log.e(TAG, "Encrypt: ", e);
            return false;
        }

        return true;
    }

    public static byte[] decrypt(byte[] encodedBytes, String key) {

        return decrypt(encodedBytes, key, UserHelper.getUserHash());

    }

    public static byte[] decrypt(byte[] encodedBytes, String key, String hash) {

        if (encodedBytes == null || encodedBytes.length <= 0)
            throw new IllegalArgumentException(TAG + ": Decryption is impossible: Illegal source");

        if (Strings.isEmpty(key))
            throw new IllegalArgumentException(TAG + ": Decryption is impossible: Illegal key");

        key += hash;

        try {

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(key));

            return cipher.doFinal(encodedBytes);

        } catch (IllegalBlockSizeException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        } catch (InvalidKeyException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        } catch (BadPaddingException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        } catch (NoSuchAlgorithmException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        } catch (NoSuchPaddingException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        } catch (NoSuchProviderException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        } catch (UnsupportedEncodingException e) {
            AppConst.Log.e(TAG, "Decryption: ", e);
        }

        return null;
    }

    private static SecretKeySpec getSecretKeySpec(String key) throws NoSuchProviderException, NoSuchAlgorithmException, UnsupportedEncodingException {

        byte[] keyStart = key.getBytes("UTF-8");

        KeyGenerator generator = KeyGenerator.getInstance("AES");

        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");

        sr.setSeed(keyStart);

        generator.init(128, sr);

        SecretKey skey = generator.generateKey();

        return new SecretKeySpec(skey.getEncoded(), "AES");

    }
}