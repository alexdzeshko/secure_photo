package by.deniotokiari.core.helpers;

import android.net.Uri;

public class UriHelper {

	public static final String TEMPLATE_QUERY_EMPTY = "?%s=%s";
	public static final String TEMPLATE_QUERY_NOT_EMPTY = "&%s=%s";

	public static Uri getUriWithParams(Uri uri, String[] keys, String[] params) {
		String str = "";
		int j = 0;
		if (uri.getQuery() == null) {
			str = String.format(TEMPLATE_QUERY_EMPTY, keys[0], params[0]);
			j = 1;
		}
		String result = uri.toString() + str;
		for (int i = j; i < keys.length; i++) {
			result += String.format(TEMPLATE_QUERY_NOT_EMPTY, keys[i],
					params[i]);
		}
		return Uri.parse(result);
	}

	public static Uri getUriWithParam(Uri uri, String key, String param) {
		String template = null;
		if (uri.getQuery() == null) {
			template = TEMPLATE_QUERY_EMPTY;
		} else {
			template = TEMPLATE_QUERY_NOT_EMPTY;
		}
		return Uri.parse(uri + String.format(template, key, param));
	}

	public static Uri getUriWithKey(Uri uri, String key) {
		if (uri.getQuery() == null) {
			return Uri.parse(uri + "?" + key);
		} else {
			return Uri.parse(uri + "&" + key);
		}
	}

	public static boolean isHasKey(Uri uri, String key) {
		return uri.getQuery() != null && uri.getQuery().contains(key);
	}

	public static boolean isParamEqualsTo(Uri uri, String key, String param) {
		return uri.getQueryParameter(key).equals(param);
	}

}
