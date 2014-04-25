package by.deniotokiari.core.utils;

import android.content.Context;

public class AndroidUtils {

	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int getStatusBarHeight(Context context) {
		return get(context, "status_bar_height");
	}

	public static int getNavigationBarHeight(Context context) {
		return get(context, "navigation_bar_height");
	}

	private static int get(Context context, String res) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(res, "dimen",
				"android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
