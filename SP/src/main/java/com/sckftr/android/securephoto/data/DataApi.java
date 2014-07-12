package com.sckftr.android.securephoto.data;

import android.app.IntentService;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.sckftr.android.app.ServiceConst;
import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.Application_;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.securephoto.db.BaseModel;
import com.sckftr.android.securephoto.db.Cryptonite;
import com.sckftr.android.securephoto.db.DbModel;
import com.sckftr.android.securephoto.processor.Cryptograph;
import com.sckftr.android.utils.IO;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import by.deniotokiari.core.utils.ContractUtils;
import by.deniotokiari.core.utils.IOUtils;

public class DataApi implements AppConst {

    private static DataApi instance;

    private void unlockFiles(ArrayList<Cryptonite> files) {

        for (Cryptonite file : files) {
            unlockFile(file);
        }
    }

    private void unlockFile(Cryptonite item) {
        FileInputStream stream = null;
        FileOutputStream fileOutputStream = null;
        try {

            stream = new FileInputStream(item.getFileUri().toString());
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            byte[] decrypted = Cryptograph.decrypt(buffer, item.getKey());

            File publicFile = Storage.Images.getPublicFile(item.getFileUri());
            fileOutputStream = new FileOutputStream(publicFile);
            fileOutputStream.write(decrypted);

            Storage.scanFile(Uri.fromFile(publicFile));

        } catch (FileNotFoundException e) {
            Log.e("unlock", item.getFileUri().toString(), e);
        } catch (IOException e) {
            Log.e("unlock", item.getFileUri().toString(), e);
        } finally {
            IOUtils.closeStream(stream);
            IOUtils.closeStream(fileOutputStream);
        }

        API.db().delete(ContractUtils.getUri(Contracts.ImageContract.class),Contracts.ImageContract.URI + " = '" + item.getFileUri().toString() + "'", null);

    }

    private void lockFiles(ArrayList<Cryptonite> files) {

        for (Cryptonite file : files) {
            lockFile(file);
        }
    }

    private boolean lockFile(Cryptonite file) {

        Uri uri = file.getFileUri();
        String key = file.getKey();

        if (Cryptograph.encrypt(uri, key)) {

            Storage.deleteFileIfPublic(uri);

            if (file instanceof BaseModel) {
                BaseModel model = (BaseModel) file;
                API.db().insert(model);
            }
            return true;

        } else
            return false;


    }

    public static DataApi instance() {
        if (instance == null) instance = new DataApi();
        return instance;
    }

    public void deleteFiles(ArrayList<? extends Cryptonite> files) {
        ArrayList<DbModel> dbList = null;
        for (Cryptonite image : files) {
            IO.delete(image.getFileUri());
            if (image instanceof DbModel) {
                if (dbList == null) dbList = new ArrayList<DbModel>();
                dbList.add((DbModel) image);
            }
        }

        API.db().delete(dbList);
    }

    public CursorLoader getImagesLoader(Context context) {
        return new CursorLoader(context, ContractUtils.getUri(Contracts.ImageContract.class), null, null, null, Contracts.ImageContract._ID+" DESC");
    }

    private enum CommandName {
        unlockFile,
        lockFile,
        deleteFiles
    }

    public static class DataAsyncEnforcerService extends IntentService implements ServiceConst {

        public DataAsyncEnforcerService() {
            super("DataAsyncEnforcerService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            final CommandName commandName = (CommandName) intent.getSerializableExtra(PARAM_IN_COMMAND_NAME);
            final ResultReceiver receiver = intent.getParcelableExtra(PARAM_IN_CALLBACK);
            Bundle resultingBundle = null;

            switch (commandName) {
                case unlockFile:
                    ArrayList<Cryptonite> files = intent.getParcelableArrayListExtra(PARAM_IN_DATA);
                    API.data().unlockFiles(files);
                    break;
                case lockFile:
                    files = intent.getParcelableArrayListExtra(PARAM_IN_DATA);
                    API.data().lockFiles(files);
                    resultingBundle = createSingleEntryBundle("ok");
                    break;
                case deleteFiles:
                    files = intent.getParcelableArrayListExtra(PARAM_IN_DATA);
                    API.data().deleteFiles(files);
                default:
                    break;

            }

            //send results if we have receiver
            if (receiver != null) {
                receiver.send(0, resultingBundle);
            }
        }

        private Bundle createSingleEntryBundle(Serializable value) {
            Bundle resultingBundle = new Bundle(1);
            resultingBundle.putSerializable(PARAM_OUT_MSG, value);
            return resultingBundle;
        }

        private Bundle createSingleEntryBundle(Parcelable value) {
            Bundle resultingBundle = new Bundle(1);
            resultingBundle.putParcelable(PARAM_OUT_MSG, value);
            return resultingBundle;
        }

    }

    public void uncryptonize(ArrayList<? extends Cryptonite> cryptonite, Procedure<? extends Object> callback) {
        final Intent intent = createBaseIntentForAsyncEnforcer(CommandName.unlockFile, createResultReceiver(callback));
        intent.putParcelableArrayListExtra(DataAsyncEnforcerService.PARAM_IN_DATA, cryptonite);
        dispatchServiceCall(intent);
    }

    public void cryptonize(ArrayList<? extends Cryptonite> cryptonite, Procedure<? extends Object> callback) {
        final Intent intent = createBaseIntentForAsyncEnforcer(CommandName.lockFile, createResultReceiver(callback));
        intent.putParcelableArrayListExtra(DataAsyncEnforcerService.PARAM_IN_DATA, cryptonite);
        dispatchServiceCall(intent);
    }

    public void delete(ArrayList<? extends Cryptonite> cryptonite, Procedure<Integer> callback) {
        final Intent intent = createBaseIntentForAsyncEnforcer(CommandName.deleteFiles, createResultReceiver(callback));
        intent.putParcelableArrayListExtra(DataAsyncEnforcerService.PARAM_IN_DATA, cryptonite);
        dispatchServiceCall(intent);
    }

    private <T extends Serializable> Intent createBaseIntentForAsyncEnforcer(final CommandName commandName, final ResultReceiver resultReceiver) {
        Application app = Application_.getInstance();
        Intent msgIntent = new Intent(app, DataAsyncEnforcerService.class);
        msgIntent.putExtra(DataAsyncEnforcerService.PARAM_IN_COMMAND_NAME, commandName);
        if (resultReceiver != null) {
            msgIntent.putExtra(DataAsyncEnforcerService.PARAM_IN_CALLBACK, resultReceiver);
        }
        return msgIntent;
    }

    private void dispatchServiceCall(final Intent intent) {
        Application_.getInstance().startService(intent);
    }


    private <T> ResultReceiver createResultReceiver(final Procedure<T> callback) {
        if (callback == null) {
            return null;
        }

        return new ResultReceiver(null) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle msg) {
                Object result;
                if (isOutputParcelable(callback)) {
                    result = msg.getParcelable(DataAsyncEnforcerService.PARAM_OUT_MSG);
                } else {
                    result = msg.getSerializable(DataAsyncEnforcerService.PARAM_OUT_MSG);
                }
                callback.apply((T) result);
            }
        };
    }

    private <T> boolean isOutputParcelable(final Procedure<T> callback) {
        Type[] types = callback.getClass().getGenericInterfaces();
        if (types.length == 0 || !(types[0] instanceof ParameterizedType)) {
            return false;
        }
        Type[] argumentsTypes = ((ParameterizedType) types[0]).getActualTypeArguments();
        return !(argumentsTypes.length == 0 || !(argumentsTypes[0] instanceof Class)) && Parcelable.class.isAssignableFrom((Class<?>) argumentsTypes[0]);
    }
}
