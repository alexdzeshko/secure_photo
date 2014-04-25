package by.deniotokiari.core.context;

import android.content.Context;

public class ContextHolder {

	private static ContextHolder instance;
	
	private Context mContext;
	
	public static ContextHolder getInstance() {
		if (instance == null) {
			instance = new ContextHolder();
		}
		return instance;
	}
	
	public void setContext(Context context) {
		this.mContext = context;
	}
	
	public Context getContext() {
		return this.mContext;
	}
	
}
