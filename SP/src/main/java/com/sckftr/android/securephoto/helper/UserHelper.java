package com.sckftr.android.securephoto.helper;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.HashUtils;
import com.sckftr.android.utils.Strings;

//todo complete refactor
public class UserHelper implements AppConst {

    private static final String TEMPLATE_FOR_HASH = "%s-%s";

    public static boolean isLogged() {
        return API.get().getPreferenceBool(KEYS.USER_LOGGED, false);
    }

    public static void setIsLogged(boolean isLogged) {
        API.get().putPreference(KEYS.USER_LOGGED, isLogged);
    }

    public static void logIn(String userName, String password) {

        String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH, userName, password));

        API.get().putPreference(KEYS.USER_KEY, hash);

    }

    public static void changePassword(String userName, String password) {

        API.get().putPreference(KEYS.USER_OLD_KEY, getUserHash());

        logIn(userName, password);
    }

    public static boolean authenticate(String userName, String password) {

        String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH, userName, password));

        return hash != null && hash.equals(getUserHash());

    }

    public static void clearUserAuthenticateInfo() {
        API.get().putPreference(KEYS.USER_KEY, Strings.EMPTY);
        API.get().putPreference(KEYS.PREF_FIRST_LOGGED, false);
        API.get().putPreference(KEYS.USER_LOGGED, false);
    }

    public static void clearOldHash() {
        API.get().putPreference(KEYS.USER_OLD_KEY, Strings.EMPTY);
    }


    public static void setFirstLogin(boolean was) {
        API.get().putPreference(KEYS.PREF_FIRST_LOGGED, was);
    }

    public static boolean isFirstLogin() {
        return API.get().getPreferenceBool(KEYS.PREF_FIRST_LOGGED, true);
    }

    public static String getUserHash() {
        return API.get().getPreferenceString(KEYS.USER_KEY, Strings.EMPTY);
    }

    public static String getOldUserHash() {
        return API.get().getPreferenceString(KEYS.USER_OLD_KEY, Strings.EMPTY);
    }
}
