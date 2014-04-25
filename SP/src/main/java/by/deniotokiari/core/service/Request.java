package by.deniotokiari.core.service;

import java.io.Serializable;
import java.util.ArrayList;

import by.deniotokiari.core.receiver.SourceResultReceiver;
import by.deniotokiari.core.receiver.SourceResultReceiver.STATUS;
import by.deniotokiari.core.source.IProcessor;
import by.deniotokiari.core.source.ISource;
import by.deniotokiari.core.utils.AppUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

public class Request<Query, Source, Result> {

    public static final String KEY_REQUEST = "request:request";
    public static final String KEY_QUERY = "request:query";
    public static final String KEY_SOURCE_KEY = "request:sourceKey";
    public static final String KEY_PROCESSOR_KEY = "request:processorKey";
    public static final String KEY_ENTITY = "request:entity";
    public static final String KEY_REQUEST_TYPE = "request:requestType";
    public static final String KEY_IS_NEED_CACHE = "request:isNeedCache";
    public static final String KEY_ENTITY_TYPE = "request:entityType";

    public static enum ENTITY_TYPE {

        STRING, STRING_ARRAY, PARCELABLE, PARCELABLE_ARRAY, PARCELABLE_ARRAY_LIST, SERIALIZABLE

    }

    private Bundle mBundle;

    public Request(Intent intent) {
        mBundle = intent.getParcelableExtra(KEY_REQUEST);
    }

    public Request(Source source, String processorKey, boolean isNeedCache) {
        setArgsToBundle(source, null, processorKey, isNeedCache, true);
    }

    public Request(Query query, String sourceKey, String processorKey,
                   boolean isNeedCache) {
        setArgsToBundle(query, sourceKey, processorKey, isNeedCache, false);
    }

    public void setBundleToInten(Intent intent) {
        intent.putExtra(KEY_REQUEST, mBundle);
    }

    protected <T> void setArgsToBundle(T object, String sourceKey,
                                       String processorKey, boolean isNeedCache, boolean isFromEntity) {
        mBundle = new Bundle();

        String key;
        if (isFromEntity) {
            key = KEY_ENTITY;
        } else {
            key = KEY_QUERY;
        }
        mBundle.putString(KEY_REQUEST_TYPE, key);

        setToBundle(object, key, mBundle);

        mBundle.putString(KEY_PROCESSOR_KEY, processorKey);
        mBundle.putBoolean(KEY_IS_NEED_CACHE, isNeedCache);
        mBundle.putString(KEY_SOURCE_KEY, sourceKey);
    }

    protected String getSourceKey() {
        return mBundle.getString(KEY_SOURCE_KEY);
    }

    protected String getProcessorKey() {
        return mBundle.getString(KEY_PROCESSOR_KEY);
    }

    protected boolean isNeedCache() {
        return mBundle.getBoolean(KEY_IS_NEED_CACHE);
    }

    protected String getRequestType() {
        return mBundle.getString(KEY_REQUEST_TYPE);
    }

    protected void sendStatus(SourceResultReceiver.STATUS status,
                              ResultReceiver resultReceiver, Bundle bundle) {
        if (resultReceiver != null) {
            resultReceiver.send(status.ordinal(), bundle);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void setToBundle(T object, String key, Bundle bundle) {
        if (object instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) object);
            bundle.putString(KEY_ENTITY_TYPE, ENTITY_TYPE.PARCELABLE.name());
        } else if (object instanceof Parcelable[]) {
            bundle.putParcelableArray(key, (Parcelable[]) object);
            bundle.putString(KEY_ENTITY_TYPE,
                    ENTITY_TYPE.PARCELABLE_ARRAY.name());
        } else if (object instanceof ArrayList<?>) {
            bundle.putParcelableArrayList(key,
                    (ArrayList<? extends Parcelable>) object);
            bundle.putString(KEY_ENTITY_TYPE,
                    ENTITY_TYPE.PARCELABLE_ARRAY_LIST.name());
        } else if (object instanceof String) {
            bundle.putString(key, (String) object);
            bundle.putString(KEY_ENTITY_TYPE, ENTITY_TYPE.STRING.name());
        } else if (object instanceof String[]) {
            bundle.putStringArray(key, (String[]) object);
            bundle.putString(KEY_ENTITY_TYPE, ENTITY_TYPE.STRING_ARRAY.name());
        } else if (object instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) object);
            bundle.putString(KEY_ENTITY_TYPE, ENTITY_TYPE.SERIALIZABLE.name());
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T getEntyti(Bundle bundle, String key) {
        ENTITY_TYPE type = ENTITY_TYPE.valueOf(bundle
                .getString(KEY_ENTITY_TYPE));
        switch (type) {
            case PARCELABLE:
                return (T) bundle.getParcelable(key);
            case PARCELABLE_ARRAY:
                return (T) bundle.getParcelableArray(key);
            case PARCELABLE_ARRAY_LIST:
                return (T) bundle.getParcelableArrayList(key);
            case SERIALIZABLE:
                return (T) bundle.getSerializable(key);
            case STRING:
                return (T) bundle.getString(key);
            case STRING_ARRAY:
                return (T) bundle.getStringArray(key);
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void executeRequest(Context context, ResultReceiver resultReceiver) {
        sendStatus(STATUS.START, resultReceiver, mBundle);
        try {
            Source source = null;
            if (getRequestType().equals(KEY_QUERY)) {
                ISource<Query, Source> dataSource = (ISource<Query, Source>) AppUtils
                        .get(context, getSourceKey());
                try {
                    source = dataSource.getSource((Query) getEntyti(mBundle, KEY_QUERY));
                } catch (Exception e) {
                    mBundle.putSerializable(SourceResultReceiver.ERROR_KEY, e);
                    sendStatus(STATUS.ERROR, resultReceiver, mBundle);
                }
                if (source == null) {
                    mBundle.putSerializable(SourceResultReceiver.ERROR_KEY,
                            new NullPointerException(
                                    "Result from data source is null"));
                    sendStatus(STATUS.ERROR, resultReceiver, mBundle);
                    return;
                }
            } else {
                source = getEntyti(mBundle, KEY_ENTITY);
            }
            IProcessor<Source, Result> processor = (IProcessor<Source, Result>) AppUtils
                    .get(context, getProcessorKey());
            if (processor != null) {
                Result result = processor.process(source);
                if (result == null) {
                    mBundle.putSerializable(SourceResultReceiver.ERROR_KEY,
                            new NullPointerException(
                                    "Result from data processor is null"));
                    sendStatus(STATUS.ERROR, resultReceiver, mBundle);
                    return;
                }
                if (isNeedCache()) {
                    boolean isCached;
                    isCached = processor.cache(result, context);
                    if (isCached) {
                        sendStatus(STATUS.CACHED, resultReceiver, mBundle);
                    } else {
                        mBundle.putSerializable(SourceResultReceiver.ERROR_KEY,
                                new Exception("Can't cache result"));
                        sendStatus(STATUS.ERROR, resultReceiver, mBundle);
                        return;
                    }
                } else {
                    setToBundle(result, SourceResultReceiver.RESULT_KEY,
                            mBundle);
                }
            }
            sendStatus(STATUS.DONE, resultReceiver, mBundle);
        } catch (Exception e) {
            e.printStackTrace();
            mBundle.putSerializable(SourceResultReceiver.ERROR_KEY, e);
            sendStatus(STATUS.ERROR, resultReceiver, mBundle);
        }
    }
}
