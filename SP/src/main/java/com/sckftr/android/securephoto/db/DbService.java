package com.sckftr.android.securephoto.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.sckftr.android.app.ServiceConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.contract.Contracts;

import java.util.ArrayList;
import java.util.Arrays;

public class DbService extends IntentService implements ServiceConst {

    public static final String NAME = DbService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DbService(String name) {
        super(NAME);
    }

    public static void insert(DbModel... objects) {
        if (objects != null && objects.length > 0) {

            createIntent(DbOperation.insert, objects);
        }
    }


    enum DbOperation {
        insert,
        delete;
    }
    @Override protected void onHandleIntent(Intent intent) {

        DbOperation operation = (DbOperation) intent.getSerializableExtra(PARAM_IN_COMMAND_NAME);
        DbModel[] objects = (DbModel[]) intent.getParcelableArrayExtra(PARAM_IN_DATA);
        switch (operation) {
            case insert:
                Uri uri = objects[0].getContentUri();
                ContentValues[] values = new ContentValues[objects.length];
                for (int i = 0; i < objects.length; i++) {
                   values[i] = objects[i].getContentValues();
                }
                getContentResolver().bulkInsert(uri, values);
                break;
            case delete:
                uri = objects[0].getContentUri();
                String[] ids = new String[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    ids[i] = objects[i].get_id();
                }
                getContentResolver().delete(uri, Contracts._ID + "=?", ids);
                break;

        }

    }

    public static void delete(DbModel... objects) {
        if (objects != null && objects.length > 0) {

            createIntent(DbOperation.delete, objects);
        }
    }

    public static void delete(ArrayList<? extends DbModel> files) {
        delete(files.toArray(new DbModel[files.size()]));
    }

    private static Intent createIntent(DbOperation commandName, DbModel[] models) {
        Application application = Application.get();
        Intent intent = new Intent(application, DbService.class);
        intent.putExtra(PARAM_IN_COMMAND_NAME, commandName);
        intent.putParcelableArrayListExtra(PARAM_IN_DATA, new ArrayList<DbModel>(Arrays.asList(models)));
        return intent;
    }

}
