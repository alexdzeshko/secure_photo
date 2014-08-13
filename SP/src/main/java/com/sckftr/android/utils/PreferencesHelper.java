package com.sckftr.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesHelper {

	public static void put(Context context, String name, int mode, String key,
			Object value) {
		Editor editor = context.getSharedPreferences(name, mode).edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		}
		editor.commit();
	}

	public static boolean getBoolean(Context context, String name, int mode,
			String key, boolean defValue) {
		SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getBoolean(key, defValue);
	}

	public static float getFloat(Context context, String name, int mode,
			String key, float defValue) {
		SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getFloat(key, defValue);
	}

	public static int getInt(Context context, String name, int mode,
			String key, int defValue) {
		SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getInt(key, defValue);
	}

	public static long getLong(Context context, String name, int mode,
			String key, long defValue) {
		SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getLong(key, defValue);
	}

	public static String getString(Context context, String name, int mode,
			String key, String defValue) {
		SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.getString(key, defValue);
	}
	
	public static boolean isContains(Context context, String name, int mode, String key) {
		SharedPreferences preferences = context
				.getSharedPreferences(name, mode);
		return preferences.contains(key);
	}
	
	public static void remove(Context context, String name, int mode, String key) {
		Editor editor = context.getSharedPreferences(name, mode).edit();
		editor.remove(key);
		editor.commit();
	}

	public static void clear(Context context, String name, int mode) {
		Editor editor = context.getSharedPreferences(name, mode).edit();
		editor.clear();
		editor.commit();
	}

}
