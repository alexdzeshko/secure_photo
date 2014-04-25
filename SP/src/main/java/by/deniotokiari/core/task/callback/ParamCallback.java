package by.deniotokiari.core.task.callback;

public interface ParamCallback<Argument, Result> {

	public Result onProcess(Argument... arg);
	
	public void onError(Exception e);
	
	public void onSuccess(Result result);
	
}
