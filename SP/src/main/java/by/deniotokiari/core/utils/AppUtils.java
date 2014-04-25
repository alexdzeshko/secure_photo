package by.deniotokiari.core.utils;

import android.content.Context;

public class AppUtils {

	public static Object get(Context context, String key) {
		Object service = context.getSystemService(key);
		if (service == null) {
			context = context.getApplicationContext();
			service = context.getSystemService(key);
		}
		return service;
	}
	
}
