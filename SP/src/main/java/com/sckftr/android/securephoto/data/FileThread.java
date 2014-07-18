package com.sckftr.android.securephoto.data;

import android.os.Message;

import com.sckftr.android.utils.Procedure;

/**
 * Created by dzianis_roi on 18.07.2014.
 */
public class FileThread extends Thread {

    private final Procedure<Message> mProcedure;

    public FileThread(Procedure<Message> messageProcedure) {

        mProcedure = messageProcedure;

    }

    @Override
    public void run() {
        // TODO
    }
}
