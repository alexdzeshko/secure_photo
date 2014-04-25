package by.deniotokiari.core.helpers;

import java.util.HashMap;
import java.util.Map;

import by.deniotokiari.core.app.PluginWrapper;
import by.deniotokiari.core.utils.AppUtils;
import android.content.Context;

public class CoreHelper {

	public static final String SYSTEM_SERVICE_KEY = "core:coreHelper";
	
	public static interface IAppServiceKey {
		
		public String getKey();
		
	}
	
	private Map<String, IAppServiceKey> mAppServices = new HashMap<String, IAppServiceKey>();

	public static CoreHelper get(Context context) {
		return (CoreHelper) AppUtils.get(context, SYSTEM_SERVICE_KEY);
	}
	
	public Object getSystemService(String key) {
		if (key.equals(SYSTEM_SERVICE_KEY)) {
			return this;
		} else if (mAppServices.containsKey(key)) {
			Object item = mAppServices.get(key);
			if (item instanceof PluginWrapper) {
				return ((PluginWrapper) item).getPlugin();
			} else {
				return item;
			}
		} else {
			return null;
		}
	}
	
	public void registerAppService(IAppServiceKey service) {
		if (!mAppServices.containsKey(service.getKey())) {
			mAppServices.put(service.getKey(), service);
		}
	}
	
}
