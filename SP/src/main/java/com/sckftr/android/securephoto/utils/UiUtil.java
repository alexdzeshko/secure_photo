package com.sckftr.android.securephoto.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.WindowManager;

import java.util.regex.Pattern;


/**
 * Class for converting px to the dp values, ellipsize text and else.
 * 
 * @author Uladzimir_Klyshevich
 *
 */
public class UiUtil {

	static int sDisplayWidth = -1;
	static int sDisplayHeight = -1;
	
	public static int getDisplayHeight(Context ctx) {
		if (sDisplayHeight == -1) {
			initDisplayDimensions(ctx);
		}
		return sDisplayHeight;
	}

	public static int getDisplayWidth(Context ctx) {
		if (sDisplayWidth == -1) {
			initDisplayDimensions(ctx);
		}
		return sDisplayWidth;
	}

    public static boolean isWiFi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return  mWifi.isConnected();
    }

	@SuppressLint("NewApi")
	private static void initDisplayDimensions(Context ctx) {
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			sDisplayWidth = size.x;
			sDisplayHeight = size.y;
		} else {
			sDisplayWidth = display.getWidth();
			sDisplayHeight = display.getHeight();
		}
	}
	
    /**
     * Default constructor.
     */
    private UiUtil() {
        // nothing here
    }

    /**
     * Convert px to dp.
     * @param context context
     * @param px value in px
     * @return dp value
     */
    public static Float getDp(final Context context, final Float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px * scale + 0.5f;
    }

    /**
     * Convert px to dp.
     * @param context context
     * @param px value in px
     * @return dp value
     */
    public static int getDp(final Context context, final int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Float.valueOf(px * scale + 0.5f).intValue();
    }

    /**
     * Gets fonts value for different resolutions.
     * @param context context
     * @param px value in px
     * @return sp value
     */
    public static int getFontSize(final Context context, final int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        int result = Float.valueOf(px * scale + 0.5f).intValue();
        if (result < 7) {
            result = result + 3;
        }
        return result;
    }

    /**
     * Convert dp value to the px value.
     * @param context context
     * @param dp value in dp
     * @return px value
     */
    public static int getPx(final Context context, final Float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round((dp - 0.5f) / scale);
    }

    /**
     * Check device orientation.
     * @param context context
     * @return ture if prortrait else false
     */
    public static boolean isPortrait(final Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    /**
     * Constant contain small characters.
     */
    private final static String NON_THIN = "[^iIl1\\.,']";
    
    /**
     * Gets text width.
     * @param str string
     * @return value in px
     */
    private static int textWidth(String str) {
	    return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
	}
    public static final Pattern SIZE_PATTERN = Pattern.compile("_\\w\\.\\w{3}$", Pattern.CASE_INSENSITIVE);


	/**
	 * Ellipsize text for lines.
	 * @param text text
	 * @param max max lines
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
	        return text.substring(0, max-3) + "...";

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

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasICS() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBeanMR1() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static int getUnitInPixels(Context ctx) {

        return getPx(ctx, 8f);
    }
}
