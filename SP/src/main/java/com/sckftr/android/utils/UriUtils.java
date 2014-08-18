package com.sckftr.android.utils;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class UriUtils {

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


    public static Set<String> getQueryParameters(Uri uri) {
        if (uri.isOpaque()) {
            return Collections.emptySet();
        }
        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());
        return Collections.unmodifiableSet(names);
    }

    public static boolean isHasKey(Uri uri, String key) {
        return uri.getQuery() != null && uri.getQuery().contains(key);
    }

    public static boolean isParamEqualsTo(Uri uri, String key, String param) {
        return uri.getQueryParameter(key).equals(param);
    }

    public static String negotiateMimeTypeFromUri(Uri uri) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

}
