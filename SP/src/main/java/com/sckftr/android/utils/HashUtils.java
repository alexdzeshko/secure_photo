package com.sckftr.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static long generateId(Object... value) {
        //String value to be converted
        try {
            StringBuilder builder = new StringBuilder();
            for (Object s : value) {
                builder.append(String.valueOf(s));
            }
            MessageDigest md = MessageDigest.getInstance("sha-1");

            //convert the string value to a byte array and pass it into the hash algorithm
            md.update(builder.toString().getBytes());

            //retrieve a byte array containing the digest
            byte[] hashValBytes = md.digest();

            long hashValLong = 0;

            //instance a long value from the byte array
            for (int i = 0; i < 8; i++) {
                hashValLong |= ((long) (hashValBytes[i]) & 0x0FF) << (8 * i);
            }
            return hashValLong;
        } catch (NoSuchAlgorithmException e) {
            return 0l;
        }
    }

    public static String stringToMD5(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
