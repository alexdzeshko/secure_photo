package by.deniotokiari.core.app;

import by.deniotokiari.core.helpers.CoreHelper.IAppServiceKey;

public class PluginWrapper implements IAppServiceKey {

	private String key;
	private Object plugin;
	
	public PluginWrapper(String key, Object plugin) {
		this.key = key;
		this.plugin = plugin;
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	public Object getPlugin() {
		return plugin; 
	}

}
