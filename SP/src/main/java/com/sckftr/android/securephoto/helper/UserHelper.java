package com.sckftr.android.securephoto.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.utils.HashUtils;
import com.sckftr.android.utils.Strings;

import org.androidannotations.api.sharedpreferences.EditorHelper;

import java.util.prefs.Preferences;

//todo complete refactor
public class UserHelper implements AppConst {

    private static final String TEMPLATE_FOR_HASH = "%s-%s";

    public static boolean isLogged() {
        return getBoolean(KEYS.PREF_USER_LOGGED, false);
    }

    public static boolean setIsLogged(boolean isLogged) {
        return putBoolean(KEYS.PREF_USER_LOGGED, isLogged);
    }

    public static boolean logIn(String userName, String password) {

        String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH, userName, password));

        return putString(KEYS.PREF_USER_KEY, hash);
    }

    public static boolean changePassword(String userName, String password) {

        putString(KEYS.PREF_USER_OLD_KEY, getUserHash());

        API.data().recryptonize(null);

        return logIn(userName, password);
    }

    public static boolean authenticate(String userName, String password) {

        String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH, userName, password));

        return hash != null && hash.equals(getUserHash());

    }

    public static boolean setFirstLogin(boolean was) {
        return putBoolean(KEYS.PREF_FIRST_LOGGED, was);
    }

    public static boolean isFirstLogin() {
        return getBoolean(KEYS.PREF_FIRST_LOGGED, true);
    }

    public static String getUserHash() {
        return getString(KEYS.PREF_USER_KEY, Strings.EMPTY);
    }

    public static String getOldUserHash() {
        return getString(KEYS.PREF_USER_OLD_KEY, Strings.EMPTY);
    }

    public static void clearUserAuthenticateInfo() {

        remove(KEYS.PREF_USER_KEY);
        remove(KEYS.PREF_USER_OLD_KEY);
        remove(KEYS.PREF_FIRST_LOGGED);
        remove(KEYS.PREF_USER_LOGGED);

        clearOldHash();
    }

    public static void clearOldHash() {
        remove(KEYS.PREF_USER_OLD_KEY);
    }

    /**
     * ******************************************************
     * /** Preferences
     * /********************************************************
     */

    private static SharedPreferences getPreferences() {
        Context context = Application.get();

        return context == null ? null : context.getSharedPreferences("app_values", Context.MODE_PRIVATE);
    }

    private static boolean putString(String key, String value) {

        SharedPreferences sp = getPreferences();

        return sp == null ? false : sp.edit().putString(key, value).commit();
    }

    private static boolean putBoolean(String key, boolean value) {
        SharedPreferences sp = getPreferences();

        return sp == null ? false : sp.edit().putBoolean(key, value).commit();
    }

    private static String getString(String key, String defValue) {
        SharedPreferences sp = getPreferences();

        return sp == null ? defValue : sp.getString(key, defValue);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = getPreferences();

        return sp == null ? defValue : getPreferences().getBoolean(key, defValue);
    }

    private static boolean remove(String key) {
        SharedPreferences sp = getPreferences();

        return sp == null ? false : getPreferences().edit().remove(key).commit();
    }

    public static void setPhotosRestoring(boolean restoring) {
        if (!restoring) clearOldHash();

        API.get().putPreference(KEYS.PREF_PHOTOS_RESTORING, restoring);
    }

    public static boolean isPhotosRestoring() {
        return API.get().getPreferenceBool(KEYS.PREF_PHOTOS_RESTORING, false);
    }
}
