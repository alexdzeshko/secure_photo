package com.sckftr.android.securephoto.helper;

import android.content.Context;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.utils.Strings;

import by.deniotokiari.core.utils.HashUtils;

//todo complete refactor
public class UserHelper implements AppConst{

	private static final String PREF_FIRST_LOGGED = "user:firstLogged";
	private static final String PREF_NAME = "user";
	private static final String PREF_KEY_LOGGED = "user:logged";
	private static final String PREF_KEY_HASH = "user:hash";
	private static final int PREF_MODE = Context.MODE_PRIVATE;
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

	public static boolean authenticate(Context context, String userName,String password) {

		String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH, userName, password));

		return hash != null && hash.equals(getUserHash());
	}

	public static void clearUserAuthenticateInfo() {
        API.get().putPreference(KEYS.USER_KEY, Strings.EMPTY);
        API.get().putPreference(PREF_FIRST_LOGGED, false);
        API.get().putPreference(KEYS.USER_LOGGED, false);
	}

	public static void setFirstLogin(boolean was) {
        API.get().putPreference(PREF_FIRST_LOGGED, was);
	}

	public static boolean isFirstLogin() {
		return API.get().getPreferenceBool(PREF_FIRST_LOGGED, true);
	}

	public static String getUserHash() {
		return API.get().getPreferenceString(KEYS.USER_KEY, Strings.EMPTY);
	}

}
