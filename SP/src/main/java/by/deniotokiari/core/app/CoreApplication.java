package by.deniotokiari.core.app;

import by.deniotokiari.core.context.ContextHolder;
import by.deniotokiari.core.helpers.CoreHelper;
import by.deniotokiari.core.helpers.CoreHelper.IAppServiceKey;
import android.app.Application;

public abstract class CoreApplication extends Application {

	private CoreHelper mCoreHelper;

	public abstract void register();
	
	public static final class PLUGIN {
		
		public static final String UNIVERSAL_IMAGE_LOADER = "plugin:UniversalImageLoader";
		
	}

	@Override
	public void onCreate() {
		mCoreHelper = new CoreHelper();
		ContextHolder.getInstance().setContext(this);
		register();
		super.onCreate();
	}

	@Override
	public Object getSystemService(String name) {
		Object service = null;
        try {
            service = mCoreHelper.getSystemService(name);
        }catch (Exception ignored){

        }
		if (service != null) {
			return service;
		} else {
			return super.getSystemService(name);
		}
	}

	public void registerService(IAppServiceKey service) {
		mCoreHelper.registerAppService(service);
	}

	public void registerPlugin(PluginWrapper wrapper) {
		registerService(wrapper);
	}
	
}
