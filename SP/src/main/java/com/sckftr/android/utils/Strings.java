package com.sckftr.android.utils;


import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;


public class Strings {

    public final static String EMPTY = "";
    public final static String SPACE = " ";
    public final static String SPACE_ENCODED = "%20";
    public final static String COMMA = ",";
    public final static String SEMICOLON = ";";

    public static final String[] EMPTY_ARRAY = new String[0];

    public static final Pattern NEWLINE_PATTERN = Pattern.compile("\n");
    public static final String TRUE = "true";

    /**
     * Checks if string passed is empty or null.
     *
     * @param text the string
     * @return true, if is empty
     */
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    /**
     * Null-safe check if strings passed is equals.
     *
     * @param s1 the one string
     * @param s2 the second string to compare to
     * @return true, if is equals
     */
    public static boolean isEquals(String s1, Object s2) {

        return s1 == null ? (s2 == null) : s1.equals(s2);
    }

    /**
     * Gets the not null.
     *
     * @param value the value
     * @return the not null
     */
    public static String getNotEmpty(final String value, String... defaults) {
        if (isEmpty(value)) {

            for (String s : defaults) {
                if (!isEmpty(s)) {
                    return s;
                }
            }

            return EMPTY;
        }

        return value;
    }

    /**
     * Null-safe compare two strings.
     *
     * @param s1 the one string
     * @param s2 the second string to compare to
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than {@code
     * another}; 0 if this instance has the same order as {@code
     * another}.
     */
    public static int compare(String s1, String s2) {

        return s1 == null ? (s2 == null ? 0 : -1) : s1.compareTo(s2);
    }


    public static String capitalize(String s) {
        return isEmpty(s) ? EMPTY : s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1);
    }

    /**
     * Gets string after separator.
     *
     * @param source source
     * @param sep    separator
     * @return result
     */
    public static String stringAfterOrNull(String source, String sep) {

        int p;
        return isEmpty(source) || isEmpty(sep) || (p = source.indexOf(sep)) == -1 ? null : source.substring(p + sep.length());
    }

    /**
     * Gets string after separator.
     *
     * @param source source
     * @param sep    separator
     * @return result
     */
    public static String stringBeforeOrNull(String source, String sep) {

        int p;
        return isEmpty(source) || isEmpty(sep) || (p = source.indexOf(sep)) == -1 ? null : source.substring(0, p);
    }

    public static String[] splitByComma(String s, String... seps) {

        if (isEmpty(s)) {
            return EMPTY_ARRAY;
        }

        for (String sep : seps) {
            return s.split(sep);
        }
        return s.split(COMMA);
    }


    public static boolean matchesSome(String n, String... ss) {
        if (n != null) {
            for (String s : ss) {
                if (n.equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsSome(String n, String... ss) {
        if (n != null) {
            for (String s : ss) {
                if (StringUtils.containsIgnoreCase(n, s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        } catch (IllegalArgumentException e) {
            return value;
        }

    }

    public static String restrictMaxLength(String value, int valueMaxLength) {
        if (value != null && value.length() > valueMaxLength) {
            value = value.substring(0, valueMaxLength) + "...";
        }
        return value;
    }

    /**
     * Gets the key by name by replacing upper case to underscore preceded lower
     * case.
     *
     * @param name the input name
     * @return the key by name
     */
    public static String getKeyByName(String name) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (builder.length() != 0) {
                    builder.append("_");
                }
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }

    /**
     * Checks string length minimum.
     *
     * @param x   the string value for checking
     * @param min the minimum length for string value
     * @return true, if string not null and its length not less than specified
     */
    public static boolean checkMinLength(final String x, final int min) {
        return !(x == null || x.length() < min);
    }

    /**
     * Check string for containing space.
     *
     * @param x the string value for checking
     * @return true, if string not null and not contain space
     */
    public static boolean checkContainSpace(final String x) {
        return !(x != null && x.contains(SPACE));
    }


    public static String[] split(String str, String delim) {
        return StringUtils.split(getNotEmpty(str), getNotEmpty(delim, SEMICOLON));
    }

    public static String join(Iterable iterable, String delim) {
        return iterable == null ? EMPTY : StringUtils.join(iterable, getNotEmpty(delim, SEMICOLON));
    }

    public static String joinBy(String sep, String... ss) {
        StringBuilder value = new StringBuilder();

        if (isEmpty(sep)) {
            sep = COMMA;
        }

        for (String s : ss) {
            if (!isEmpty(s)) {
                if (value.length() > 0) {
                    value.append(sep);
                }

                value.append(s);
            }
        }

        return value.toString();
    }

    public static String padL(String s, int i, String f) {
        if (s == null) {
            s = EMPTY;
        }
        return multi(i - s.length(), f).append(s).toString();
    }

    private static StringBuilder multi(int l, String f) {
        if (isEmpty(f)) {
            f = SPACE;
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < l; i++) {
            s.append(f);
        }
        return s;
    }

    public static String beforeOrSelf(String s, String s1) {
        return isEmpty(s) ? EMPTY : (s.split(getNotEmpty(s1, SPACE))[0]);
    }
}
