package by.deniotokiari.core.utils;

import android.graphics.BitmapFactory;

public class Calculate {

	public static int getSampleScale(BitmapFactory.Options options, int width,
			int height) {
		if (options.outHeight > height || options.outWidth > width) {
			int hRatio = Math.round((float) options.outHeight / (float) height);
			int wRatio = Math.round((float) options.outWidth / (float) width);

			return hRatio < wRatio ? hRatio : wRatio;
		}
		return 1;
	}

}
