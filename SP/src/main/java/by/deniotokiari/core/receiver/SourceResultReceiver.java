package by.deniotokiari.core.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public abstract class SourceResultReceiver extends ResultReceiver {
	
	public static final String ERROR_KEY = "core:error";
	public static final String RESULT_KEY = "core:result";
	
	public static enum STATUS {
		
		ERROR, DONE, CACHED, START
		
	}
	
	public abstract void onError(Exception e);
	
	public abstract void onDone(Bundle result);
	
	public SourceResultReceiver(Handler handler) {
		super(handler);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		STATUS status = STATUS.values()[resultCode];
		switch (status) {
		case ERROR:
			onError((Exception) resultData.getSerializable(ERROR_KEY));
			break;
		case DONE:
			onDone(resultData);
			break;
		case CACHED:
			onCached(resultData);
			break;
		case START:
			onStart(resultData);
			break;
		default:
			break;
		}
		super.onReceiveResult(resultCode, resultData);
	}
	
	protected void onCached(Bundle result) {
		
	}
	
	protected void onStart(Bundle result) {
		
	}

}
