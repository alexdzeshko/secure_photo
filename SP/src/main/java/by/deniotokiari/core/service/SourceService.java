package by.deniotokiari.core.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

public class SourceService extends IntentService {

    public static final String SERVICE_NAME = SourceService.class
            .getSimpleName();
    public static final String KEY_RESULT_RECEIVER = "resultReceiver";

    public SourceService() {
        super(SERVICE_NAME);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void onHandleIntent(Intent intent) {
        Request request = new Request(intent);
        ResultReceiver receiver = intent.getParcelableExtra(KEY_RESULT_RECEIVER);
        request.executeRequest(getApplicationContext(), receiver);
    }

    public static void execute(Context context, Request<?, ?, ?> request) {
        execute(context, request, null);
    }

    public static void execute(Context context, Request<?, ?, ?> request,
                               ResultReceiver receiver) {
        Intent intent = new Intent(context, SourceService.class);
        request.setBundleToInten(intent);
        intent.putExtra(KEY_RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

}
