package com.sckftr.android.securephoto.data;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sckftr.android.securephoto.AppConst;
import com.sckftr.android.securephoto.R;
import com.sckftr.android.utils.Storage;
import com.sckftr.android.utils.Strings;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dzianis_roi on 18.07.2014.
 */
public class FileThread extends Thread {

    private static final String TAG = FileThread.class.getSimpleName();

    public enum Task {
        CREATE_TEMP_FILE, CREATE, GET
    }

    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_ERROR = 2;

    public static final String CREATE_FILE_RESULT = "CREATE_FILE_RESULT";

    private final Handler mHandler;

//    private File mFile;

    private final Task mTask;

    public FileThread(Handler handler, Task task) {

        mTask = task;

        mHandler = handler;
    }

    @Override
    public void run() {
        switch (mTask) {

            case CREATE_TEMP_FILE: {

                createTempFile();

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

            default:
                break;

        }

    }

    private void createTempFile() {

        Message msg = new Message();

        String dataString;

        File tempFile = null;

        String imageFileName = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()) + "_";

        try {

            tempFile = File.createTempFile(imageFileName, ".jpg", Storage.Images.getPrivateFolder());

        } catch (IOException e) {

            msg.what = MESSAGE_ERROR;

            dataString = e.getMessage();

            AppConst.Log.e(TAG, "createImageFile", e);

        }

        if (tempFile == null || !tempFile.exists()) {

            msg.what = MESSAGE_ERROR;

            dataString = AppConst.API.string(R.string.cant_create_new_file);

        } else {

            Uri uri = Uri.fromFile(tempFile);

            dataString = Strings.getNotEmpty(uri.toString(), Strings.EMPTY);

            AppConst.Log.d("temp_file", Uri.fromFile(tempFile).toString());

            msg.what = MESSAGE_SUCCESS;

        }

        Bundle data = new Bundle();
        data.putString(CREATE_FILE_RESULT, dataString);

        msg.setData(data);

        mHandler.sendMessage(msg);
    }
}
