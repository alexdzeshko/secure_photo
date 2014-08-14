package com.sckftr.android.utils;

import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by dzianis_roi on 14.08.2014.
 */
public class ExifUtil {

    private static final String TAG = ExifUtil.class.getSimpleName();

    private ExifUtil() {
    }

    public static ExifInterface copy(String targetFilePath, ExifInterface source) throws IOException {

        if (TextUtils.isEmpty(targetFilePath) || source == null) return null;

        // copy paste exif information from original file to new file
        ExifInterface newExif = new ExifInterface(targetFilePath);

        // From API 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAttribute(source, ExifInterface.TAG_APERTURE, source.getAttribute(ExifInterface.TAG_APERTURE));
            setAttribute(source, ExifInterface.TAG_EXPOSURE_TIME, source.getAttribute(ExifInterface.TAG_EXPOSURE_TIME));
            setAttribute(source, ExifInterface.TAG_ISO, source.getAttribute(ExifInterface.TAG_ISO));
        }
        // From API 9
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setAttribute(source, ExifInterface.TAG_GPS_ALTITUDE, source.getAttribute(ExifInterface.TAG_GPS_ALTITUDE));
            setAttribute(source, ExifInterface.TAG_GPS_ALTITUDE_REF, source.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF));
        }
        // From API 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            setAttribute(source, ExifInterface.TAG_FOCAL_LENGTH, source.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
            setAttribute(source, ExifInterface.TAG_GPS_DATESTAMP, source.getAttribute(ExifInterface.TAG_GPS_DATESTAMP));
            setAttribute(source, ExifInterface.TAG_GPS_PROCESSING_METHOD, source.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD));
            setAttribute(source, ExifInterface.TAG_GPS_TIMESTAMP, source.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP));
        }

        setAttribute(source, ExifInterface.TAG_DATETIME, source.getAttribute(ExifInterface.TAG_DATETIME));
        setAttribute(source, ExifInterface.TAG_FLASH, source.getAttribute(ExifInterface.TAG_FLASH));
        setAttribute(source, ExifInterface.TAG_GPS_LATITUDE, source.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
        setAttribute(source, ExifInterface.TAG_GPS_LATITUDE_REF, source.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
        setAttribute(source, ExifInterface.TAG_GPS_LONGITUDE, source.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
        setAttribute(source, ExifInterface.TAG_GPS_LONGITUDE_REF, source.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));

        // You need to update this with your new height width
        setAttribute(source, ExifInterface.TAG_IMAGE_LENGTH, source.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
        setAttribute(source, ExifInterface.TAG_IMAGE_WIDTH, source.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));

        setAttribute(source, ExifInterface.TAG_MAKE, source.getAttribute(ExifInterface.TAG_MAKE));
        setAttribute(source, ExifInterface.TAG_MODEL, source.getAttribute(ExifInterface.TAG_MODEL));
        setAttribute(source, ExifInterface.TAG_ORIENTATION, source.getAttribute(ExifInterface.TAG_ORIENTATION));
        setAttribute(source, ExifInterface.TAG_WHITE_BALANCE, source.getAttribute(ExifInterface.TAG_WHITE_BALANCE));

        newExif.saveAttributes();

        return newExif;
    }


    public static String getAttributeValue(String path, String tag) throws IOException {

        return TextUtils.isEmpty(path) || TextUtils.isEmpty(tag) ? null : new ExifInterface(path).getAttribute(tag);

    }

    public static int convertMetaOrientationToDegrees(String orientation) {

        int degree = 0;

        if (orientation.equalsIgnoreCase("6")) {
            degree = 90;
        } else if (orientation.equalsIgnoreCase("8")) {
            degree = 270;
        } else if (orientation.equalsIgnoreCase("3")) {
            degree = 180;
        }

        return degree;
    }

    private static void setAttribute(ExifInterface newExif, String attrName, String value) {
        if (newExif != null && !TextUtils.isEmpty(attrName) && !TextUtils.isEmpty(value))
            newExif.setAttribute(attrName, value);
    }
}
