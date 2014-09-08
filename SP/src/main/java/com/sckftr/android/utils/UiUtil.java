package com.sckftr.android.utils;


import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

//todo clear or delete. see DisplayMetricsUtil
/**
 * Class for converting px to the dp values, ellipsize text and else.
 *
 * @author Uladzimir_Klyshevich
 */
public class UiUtil {

    /**
     * Constant contain small characters.
     */
    private final static String NON_THIN = "[^iIl1\\.,']";

    /**
     * Gets text width.
     *
     * @param str string
     * @return value in px
     */
    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    /**
     * Ellipsize text for lines.
     *
     * @param text text
     * @param max  max lines
     * @return new text
     */
    public static String ellipsize(String text, int max) {
        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max - 3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }
}
