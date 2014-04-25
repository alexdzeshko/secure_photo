package by.deniotokiari.core.helpers;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastHelper {

	public static void show(Context context, String text, int duration) {
		Toast.makeText(context, text, duration).show();
	}

	public static void showFromThread(final Context context, final String text,
			final int duration) {
		Handler handler = new Handler(context.getMainLooper());
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				show(context, text, duration);
			}
			
		};
		handler.post(runnable);
	}

}
