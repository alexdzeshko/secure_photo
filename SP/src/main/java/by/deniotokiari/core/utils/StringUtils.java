package by.deniotokiari.core.utils;

import java.util.HashMap;
import java.util.List;

public class StringUtils {

	public static <T> String join(List<T> items, String joiner) {
		String result = "";
        for (T item : items) {
            if (item == null) {
                continue;
            }
            result += String.valueOf(item) + joiner;
        }
		return substring(result, joiner.length());
	}

	public static String join(HashMap<?, ?> items, String joiner) {
		String result = "";
		String[] k = new String[items.size()];
		items.keySet().toArray(k);
        for (String aK : k) {
            result += aK + joiner;
        }
		return substring(result, joiner.length());
	}

	public static <T> String join(T[] items, String joiner) {
		String result = "";
        for (T item : items) {
            if (item == null) {
                continue;
            }
            result += String.valueOf(item) + joiner;
        }
		return substring(result, joiner.length());
	}
	
	private static String substring(String str, int n) {
		return str.substring(0, str.length() - n);
	}

}
