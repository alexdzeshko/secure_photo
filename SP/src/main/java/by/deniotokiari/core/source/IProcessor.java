package by.deniotokiari.core.source;

import android.content.Context;
import by.deniotokiari.core.helpers.CoreHelper.IAppServiceKey;

public interface IProcessor<Source, Result> extends IAppServiceKey {

	public Result process(Source source);
	
	public boolean cache(Result result, Context context);
	
}
