package com.sckftr.android.securephoto.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;

import com.sckftr.android.app.ServiceConst;
import com.sckftr.android.securephoto.Application;
import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.utils.Strings;

import java.util.ArrayList;

public class DbService extends IntentService implements ServiceConst {

    public static final String NAME = DbService.class.getName();

    private static DbService instance;

    public static DbService get() {
        if (instance == null) instance = new DbService();
        return instance;
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DbService() {
        super(NAME);
    }

    enum DbOperation {
        insert,
        delete
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DbOperation operation = (DbOperation) intent.getSerializableExtra(PARAM_IN_COMMAND_NAME);
        DbModel[] objects = dataFromIntent(intent);

        if (objects == null || objects.length == 0) return;

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
                getContentResolver().delete(uri, String.format(Contracts._ID + " IN (%s)", Strings.joinBy(Strings.COMMA, ids)), null);
                break;

        }

    }

    private DbModel[] dataFromIntent(Intent intent) {
        Parcelable[] parcelables = intent.getParcelableArrayExtra(PARAM_IN_DATA);
        if (parcelables == null || parcelables.length == 0) return null;
        DbModel[] data = new DbModel[parcelables.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = (DbModel) parcelables[i];
        }
        return data;
    }

    public void insert(DbModel... objects) {
        if (objects != null && objects.length > 0) {

            Intent intent = createIntent(DbOperation.insert, objects);
            dispatchServiceCall(intent);
        }
    }

    public void delete(DbModel... objects) {
        if (objects != null && objects.length > 0) {

            Intent intent = createIntent(DbOperation.delete, objects);
            dispatchServiceCall(intent);
        }
    }

    public void delete(ArrayList<? extends DbModel> files) {
        delete(files.toArray(new DbModel[files.size()]));
    }

    public void delete(final Uri uri, final String where, final String[] whereArgs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Application.get().getContentResolver().delete(uri, where, whereArgs);
            }
        }).start();
    }

    public Cursor query(Uri uri, String[] projection, String where, String[] whereArgs, String sortOrder) {

        return Application.get().getContentResolver().query(uri, projection, where, whereArgs, sortOrder);

    }

    public Cursor query(final Uri uri, String[] projection) {

        return query(uri, projection, null, null, null);

    }

    private Intent createIntent(DbOperation commandName, DbModel[] models) {
        Application application = Application.get();
        Intent intent = new Intent(application, DbService.class);
        intent.putExtra(PARAM_IN_COMMAND_NAME, commandName);
        intent.putExtra(PARAM_IN_DATA, models);
        return intent;
    }

    private void dispatchServiceCall(final Intent intent) {
        Application.get().startService(intent);
    }
}
