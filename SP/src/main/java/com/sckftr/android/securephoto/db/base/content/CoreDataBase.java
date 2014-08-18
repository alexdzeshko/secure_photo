package com.sckftr.android.securephoto.db.base.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sckftr.android.securephoto.db.base.helpers.DbHelper;
import com.sckftr.android.utils.ContractUtils;

public class CoreDataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "core.store.db";
    private static final int DB_VERSION = 1;
    private static final Object DB_OBJECT_LOCK = new Object();

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private boolean isInTransaction = false;
    private Class<?> mContract;

    public CoreDataBase(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        syncTransaction();

        try {

            setInTransaction(true);

            db.beginTransaction();

            ContractUtils.checkContractClass(mContract);

            db.execSQL(DbHelper.getCreateTableString(mContract));

            db.setTransactionSuccessful();

        } finally {

            db.endTransaction();

            setInTransaction(false);

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < newVersion) {

            deleteDataBase();

            db.setVersion(newVersion);

        }

        onCreate(mDatabase);
    }

    private void createTableIfNotExist(String table) {

        if (!DbHelper.isTableExists(mDatabase, table)) {

            onCreate(mDatabase);

        }
    }

    protected void deleteDataBase() {

        syncTransaction();

        try {

            setInTransaction(true);

            mContext.deleteDatabase(DB_NAME);

        } finally {

            setInTransaction(false);

        }
    }

    private void syncTransaction() {

        while (isInTransaction) {

            waitWhileTransaction();

        }
    }

    private void setInTransaction(boolean flag) {

        synchronized (DB_OBJECT_LOCK) {

            isInTransaction = flag;

            if (!isInTransaction) {

                DB_OBJECT_LOCK.notifyAll();

            }
        }
    }

    private static void waitWhileTransaction() {

        synchronized (DB_OBJECT_LOCK) {

            try {

                DB_OBJECT_LOCK.wait();

            } catch (InterruptedException ignored) {
                // ignore
            }
        }
    }

    protected long addItem(Class<?> contract, ContentValues value) throws SQLException {

        syncTransaction();

        mContract = contract;

        mDatabase = getWritableDatabase();

        String table = ContractUtils.getTableName(contract);

        createTableIfNotExist(table);

        long added;

        try {

            setInTransaction(true);

            mDatabase.beginTransaction();

            added = mDatabase.insertWithOnConflict(table, null, value, SQLiteDatabase.CONFLICT_REPLACE);

            if (added <= 0) throw new SQLException("Failed to insert row into " + table);

            mDatabase.setTransactionSuccessful();

        } finally {

            mDatabase.endTransaction();

            setInTransaction(false);

        }

        return added;
    }

    protected int addItems(Class<?> contract, ContentValues[] values) throws SQLException {

        syncTransaction();

        mContract = contract;

        mDatabase = getWritableDatabase();

        String table = ContractUtils.getTableName(contract);

        createTableIfNotExist(table);

        long added;

        int inserted = 0;

        try {

            setInTransaction(true);

            mDatabase.beginTransaction();

            for (ContentValues value : values) {

                added = mDatabase.insertWithOnConflict(table, null, value, SQLiteDatabase.CONFLICT_REPLACE);

                if (added <= 0) {

                    throw new SQLException("Failed to insert row into " + table);

                } else {
                    inserted++;
                }
            }

            mDatabase.setTransactionSuccessful();

        } finally {

            mDatabase.endTransaction();

            setInTransaction(false);

        }

        return inserted;
    }

    protected Cursor getItems(Class<?> contract, String selection, String[] selectionArgs, String orderBy) {

        syncTransaction();

        mContract = contract;

        mDatabase = getWritableDatabase();

        String table = ContractUtils.getTableName(contract);

        createTableIfNotExist(table);

        Cursor cursor = null;

        try {

            setInTransaction(true);

            mDatabase.beginTransaction();

            cursor = mDatabase.query(table, null, selection, selectionArgs, null, null, orderBy);

            if (cursor == null) {

                throw new SQLException("Failed to query row from " + table);

            }
        } finally {

            mDatabase.endTransaction();

            setInTransaction(false);

        }

        return cursor;
    }

    protected Cursor rawQuery(Class<?> contract, String sql, String[] selectionArgs) {
        syncTransaction();
        mContract = contract;
        mDatabase = getWritableDatabase();
        String table = ContractUtils.getTableName(contract);
        createTableIfNotExist(table);
        Cursor cursor = null;
        try {
            setInTransaction(true);
            mDatabase.beginTransaction();
            cursor = mDatabase.rawQuery(sql, selectionArgs);
            if (cursor == null) {
                throw new SQLException("Failed to query: " + sql);
            }
        } finally {
            mDatabase.endTransaction();
            setInTransaction(false);
        }
        return cursor;
    }

    protected int deleteItems(Class<?> contract, String where, String[] whereArgs) {
        if (mDatabase == null) {
            return 0;
        }
        syncTransaction();
        mContract = contract;
        mDatabase = getWritableDatabase();
        String table = ContractUtils.getTableName(contract);
        int result = 0;
        try {
            setInTransaction(true);
            mDatabase.beginTransaction();
            if (DbHelper.isTableExists(mDatabase, table)) {
                result = mDatabase.delete(table, where, whereArgs);
            }
            mDatabase.setTransactionSuccessful();
            return result;
        } finally {
            mDatabase.endTransaction();
            setInTransaction(false);
        }
    }

}
