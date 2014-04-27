package com.sckftr.android.securephoto.helper;

import android.content.Context;
import by.deniotokiari.core.helpers.PreferencesHelper;
import by.deniotokiari.core.utils.HashUtils;

//todo complete refactor
public class UserHelper {

	private static final String PREF_FIRST_LOGGED = "user:firstLogged";
	private static final String PREF_NAME = "user";
	private static final String PREF_KEY_LOGGED = "user:logged";
	private static final String PREF_KEY_HASH = "user:hash";
	private static final int PREF_MODE = Context.MODE_PRIVATE;
	private static final String TEMPLATE_FOR_HASH = "%s-%s";

	public static boolean isLogged(Context context) {
		return PreferencesHelper.getBoolean(context, PREF_NAME, PREF_MODE,
				PREF_KEY_LOGGED, false);
	}

	public static void setIsLogged(Context context, boolean isLogged) {
		PreferencesHelper.put(context, PREF_NAME, PREF_MODE, PREF_KEY_LOGGED,
				isLogged);
	}

	public static void logIn(Context context, String userName, String password) {
		String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH,
				userName, password));
		PreferencesHelper.put(context, PREF_NAME, PREF_MODE, PREF_KEY_HASH,
				hash);
	}

	public static boolean authenticate(Context context, String userName,
			String password) {
		String hash = HashUtils.stringToMD5(String.format(TEMPLATE_FOR_HASH,
				userName, password));
		return hash != null && hash.equals(getUserHash(context));
	}

	public static void clearUserAuthentificateInfo(Context context) {
		PreferencesHelper.clear(context, PREF_NAME, PREF_MODE);
	}

	public static void setFirstLogin(Context context, boolean was) {
		PreferencesHelper.put(context, PREF_NAME, PREF_MODE, PREF_FIRST_LOGGED,
				was);
	}

	public static boolean isFirstLogin(Context context) {
		return PreferencesHelper.getBoolean(context, PREF_NAME, PREF_MODE,
				PREF_FIRST_LOGGED, true);
	}

	private static String getUserHash(Context context) {
		return PreferencesHelper.getString(context, PREF_NAME, PREF_MODE,
				PREF_KEY_HASH, "");
	}

}
