package by.deniotokiari.core.task;

import by.deniotokiari.core.task.callback.ParamCallback;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class CommonAsynkTask<Argument, Result> extends
		AsyncTask<Argument, Void, Result> {

	private ParamCallback<Argument, Result> mCallback;
	private Exception e;

	public CommonAsynkTask(ParamCallback<Argument, Result> callback) {
		super();
		mCallback = callback;
	}

	@Override
	protected Result doInBackground(Argument... params) {
		try {
			return mCallback.onProcess(params);
		} catch (Exception e) {
			this.e = e;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		if (e != null) {
			mCallback.onError(e);
		} else {
			mCallback.onSuccess(result);
		}
	}

	@SuppressLint("NewApi")
	public void start(Argument... arguments) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arguments);
		} else {
			execute(arguments);
		}
	}

}
