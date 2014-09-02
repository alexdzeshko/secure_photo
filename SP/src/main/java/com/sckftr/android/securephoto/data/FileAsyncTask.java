package com.sckftr.android.securephoto.data;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.utils.Procedure;
import com.sckftr.android.utils.Strings;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dzianis_roi on 18.07.2014.
 */
public class FileAsyncTask extends AsyncTask<Bundle, Void, Bundle> {

    private static final String TAG = FileAsyncTask.class.getSimpleName();

    public enum Task {
        CREATE_TEMP_FILE, CREATE, GET, DELETE
    }

    public static final String FILE_URI = "com.sckftr.android.securephoto.data.CREATED_FILE_URI_EXTRA";
    public static final String FILE_ERROR = "com.sckftr.android.securephoto.data.FILE_THREAD_ERROR_EXTRA";

    private static final String FILE_NAME_EXTRA = "com.sckftr.android.securephoto.data.FILE_NAME_EXTRA";
    private static final String FILE_SUFFIX_EXTRA = "com.sckftr.android.securephoto.data.FILE_SUFFIX_EXTRA";
    private static final String DIRECTORY_PATH_EXTRA = "com.sckftr.android.securephoto.data.DIRECTORY_PATH_EXTRA";

    private final Procedure<Bundle> mProcedure;

    private Task mTask;

    public FileAsyncTask() {
        this(null);
    }

    public FileAsyncTask(Procedure<Bundle> procedure) {
        mProcedure = procedure;
    }

    @Override
    protected Bundle doInBackground(Bundle... params) {

        if (params[0] == null) {
            // TODO
            return null;
        }

        switch (mTask) {

            case CREATE_TEMP_FILE: {

                createTemp(params[0]);

                break;
            }

            case CREATE: {
                // TODO
                break;
            }

            case GET: {
                // TODO
                break;
            }

            case DELETE: {

                delete(params[0]);

                break;
            }

            default:
                break;

        }
        return null;
    }

    public void createTempFile(String fileName, String suffix, File directory) {

        mTask = Task.CREATE_TEMP_FILE;

        Bundle params = new Bundle();

        params.putString(FILE_NAME_EXTRA, Strings.isEmpty(fileName) ? new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) : fileName);

        params.putString(FILE_SUFFIX_EXTRA, suffix);

        if (directory != null && directory.exists()) {
            params.putString(DIRECTORY_PATH_EXTRA, directory.getPath());
        }

        execute(params);
    }

    private void createTemp(Bundle params) {

        String error = null;

        File tempFile = null;

        String directoryPath = params.getString(DIRECTORY_PATH_EXTRA);

        try {

            tempFile = File.createTempFile(params.getString(FILE_NAME_EXTRA), params.getString(FILE_SUFFIX_EXTRA), Strings.isEmpty(directoryPath) ? null : new File(directoryPath));

        } catch (IOException e) {

            error = e.getMessage();

            AppConst.Log.e(TAG, "createImageFile", e);

        }

        Uri uri = null;

        if (tempFile == null || !tempFile.exists()) {

            error = AppConst.API.string(R.string.ERR_CREATE_FILE);

        } else {

            uri = Uri.fromFile(tempFile);

            AppConst.Log.d(TAG, Uri.fromFile(tempFile).toString());

        }

        Bundle data = new Bundle();

        if (uri != null) {

            data.putParcelable(FILE_URI, uri);

        } else if (!Strings.isEmpty(error)) {

            data.putString(FILE_ERROR, error);

        }

        if (mProcedure != null) mProcedure.apply(data);
    }

    public void deleteFile(Uri uri) {

        mTask = Task.DELETE;

        Bundle params = new Bundle();

        params.putParcelable(FILE_URI, uri);

        execute(params);
    }

    private void delete(Bundle params) {

        Uri uri = params.getParcelable(FILE_URI);

        if (uri != null) {

            File file = new File(uri.getPath());

            try {

                FileUtils.forceDelete(file);

            } catch (IOException e) {

                AppConst.Log.e(TAG, "delete: ", e);

            }
        }
    }

}
