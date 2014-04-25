package by.deniotokiari.core.source;

import by.deniotokiari.core.helpers.CoreHelper.IAppServiceKey;

public interface ISource<Query, Source> extends IAppServiceKey {

	Source getSource(Query query) throws Exception;
	
}
