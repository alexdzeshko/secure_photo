package com.sckftr.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Aliaksandr_Litskevic on 3/20/14.
 */
public class CryptoUtils {

    public static final String md5(final String... ss) {
//        return new String(Hex.encodeHex(DigestUtils.md5(data)));
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            for (String s : ss) {
                digest.update(s.getBytes());
            }
            byte array[] = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return Strings.EMPTY;
    }
}
