package com.sckftr.android.securephoto;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.sckftr.android.securephoto.data.DataApi;
import com.sckftr.android.securephoto.db.DbService;
import com.sckftr.android.utils.Strings;

import by.grsu.mcreader.mcrimageloader.imageloader.SuperImageLoader;

public interface AppConst {

    String EMPTY = Strings.EMPTY;

    // Logging prefix.
    String LOG_TAG = "SecurePhotoApp";

    // Application package id.
    String PACKAGE = "com.sckftr.android.securephoto";


    // Common extras keys.
    public static interface EXTRA {

        String VALUES = PACKAGE + ".VALUES";
        String RESULT = PACKAGE + ".RESULT";
        String IMAGE = PACKAGE + ".IMAGE";

    }

    // Preferences keys.
    public interface KEYS {
        String USER_KEY = PACKAGE + ".USER_KEY";
        String USER_LOGGED = PACKAGE + ".USER_LOGGED";
    }

    public static class REQUESTS {
        public static final int IMAGE_CAPTURE = 0x01;
        public static final int IMAGE_GALLERY = 0x02;
    }

    public enum ERROR {
        UNKNOWN,
        NO_NETWORK,
        NOT_SPECIFIED
    }

    /**
     * Common API accessors.
     */
    public static class API {

        private static AppApi INSTANCE;

        public static AppApi get() {

            return INSTANCE;
        }

        private Gson gson;

        public static Gson gson() {
            return get().gson();
        }

        public static SuperImageLoader images() {

            return get().images();
        }

        public static DataApi data() {
            return DataApi.instance();
        }

        public static DbService db() {
            return DbService.get();
        }

        // invoked only at Application.onCreate()
        static void init(Context context) {

            INSTANCE = new AppApi(context);
        }

        public static String string(int resId, Object... args) {

            return get().string(resId, args);
        }

        public static String qstring(int resId, int i) {

            return get().qstring(resId, i);

        }

        public static int color(int resId) {

            return get().color(resId);

        }

        public static Drawable drawable(int resId) {
            return get().drawable(resId);
        }
    }

    /**
     * Logging API.
     */
    public static class Log {
        /**
         * Checks if app is in debug mode.
         */
        public static final boolean ENABLED = BuildConfig.DEBUG;

        /**
         * Logs strings.
         *
         * @param tag
         * @param template
         * @param args
         */
        public static void d(String tag, String template, Object... args) {
            if (ENABLED) {
                if (args.length == 0) {
                    android.util.Log.d(_t(tag), template);
                } else {
                    android.util.Log.d(_t(tag), String.format(template, args));
                }
            }
        }

        public static void e(String tag, String msg, Throwable error) {
            android.util.Log.e(_t(tag), msg, error);
        }

        public static void v(String tag, String template, Object... args) {
            if (ENABLED) {
                android.util.Log.v(_t(tag), String.format(template, args));
            }
        }

        public static void w(String tag, String template, Object... args) {
            if (ENABLED) {
                android.util.Log.w(_t(tag), String.format(template, args));
            }
        }

        public static void e(String tag, String string) {
            e(_t(tag), string, null);
        }

        public static void i(String tag, String template, Object... args) {
            if (ENABLED) {
                android.util.Log.i(_t(tag), String.format(template, args));
            }
        }

        private static String _t(String tag) {
            return tag == null ? LOG_TAG + ":*" : LOG_TAG + ":" + tag;
        }
    }

}
