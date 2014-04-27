package com.sckftr.android.securephoto;

import android.content.Context;

import com.google.gson.Gson;
import com.sckftr.android.securephoto.data.DataApi;
import com.sckftr.android.utils.Strings;
import com.squareup.picasso.Picasso;

public interface AppConst {
    int ASCENDING = 1;
    int DESCENDING = -1;

    String EMPTY = Strings.EMPTY;

    // Logging prefix.
    String LOG_TAG = "SecurePhotoApp";

    // Application package id.
    String PACKAGE = "com.sckftr.android.securephoto";

    // Entity kind key.
    String KEY_KIND = "_kind";
    // Entity PK key.
    String KEY_PK = "_pk";
    // Entity TIMESTAMP key.
    String KEY_TIMESTAMP = "_timestamp";

    // Common extras keys.
    public static interface EXTRA {

        // Extras
        String ID = PACKAGE + ".ID";
        String TYPE = PACKAGE + ".TYPE";
        String VALUES = PACKAGE + ".VALUES";
        String CODE = PACKAGE+".CODE";
        String PARAMS = PACKAGE+".PARAMS";
        String RECEIVER = PACKAGE+".RECEIVER";
        String RESULT = PACKAGE+".RESULT";

        String ACTION = PACKAGE + ".ACTION";
        String SELECTION = PACKAGE + ".SELECTION";
        String SELECTION_ARGS = PACKAGE + ".SELECTION_ARGS";
        String PROJECTION = PACKAGE + ".PROJECTION";
        String SORT_ORDER = PACKAGE + ".SORT_ORDER";

        String IMAGE = PACKAGE + ".IMAGE";
        String NAME = PACKAGE + ".NAME_EXTRA";
        String ADDRESS = PACKAGE + ".ADDRESS";
        String LOCATION = PACKAGE + ".LOCATION";
    }

    // Common actions.
    public static interface ACTION {

        String INSERT = PACKAGE + ".INSERT";
        String UPDATE = PACKAGE + ".UPDATE";
        String DELETE = PACKAGE + ".DELETE";
        String LOCATION_SELECTED = PACKAGE + ".LOCATION_SELECTED";
        String SORT_ORDER_CHANGED = PACKAGE + ".SORT_ORDER_CHANGED";
        String FILTER_CHANGED = PACKAGE + ".FILTER_CHANGED";
        String DATES_SELECTED = PACKAGE + ".DATES_SELECTED";
        String BOOKING_ERROR = PACKAGE + ".BOOKING_ERROR";
        String CVC_READY = PACKAGE + ".CVC_READY";
    }

    // Preferences keys.
    public interface KEYS {
        String GEOLOCATION_DISABLED = PACKAGE + ".GEOLOCATION_DISABLED";
        String ARRIVAL_DATE = PACKAGE + ".ARRIVAL_DATE";
        String DEPARTURE_DATE = PACKAGE + ".DEPARTURE_DATE";
        String NIGTHS = PACKAGE + ".NIGTHS";
        String HOTEL_ID = PACKAGE + ".HOTEL_ID";

        String FIRST_MAME = PACKAGE + ".FIRST_MAME";
        String LAST_NAME = PACKAGE + ".LAST_NAME";
        String PHONE = PACKAGE + ".PHONE";
        String EMAIL = PACKAGE + ".EMAIL";
        String USER_INFO = PACKAGE+".USER_INFO";

        String CURRENCY = PACKAGE+".CURRENCY";
        String DISTANCE = PACKAGE+".DISTANCE";
        String PRIVACY_PERMISSION = PACKAGE+".PRIVACY_PERMISSION";
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

        public static Gson gson(){
            return get().gson();
        }

        public static Picasso images() {

            return get().images();
        }

        public static DataApi data() {
            return DataApi.instance();
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
                if (args.length==0){
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
            return tag==null ? LOG_TAG+":*" : LOG_TAG+":"+tag;
        }
    }

    public static class Settings {


    }
}
