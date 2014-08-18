package com.sckftr.android.securephoto.db.base.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.sckftr.android.utils.UriUtils;

import com.sckftr.android.utils.ContractUtils;

abstract public class CoreProvider extends ContentProvider {

    public static final String IS_NO_NOTIFAED = "is_notifaed";

    private CoreDataBase mDataBase;

    protected abstract Class<?> getContract();

    @Override
    public boolean onCreate() {
        mDataBase = new CoreDataBase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return ContractUtils.getType(getContract());
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result = mDataBase.deleteItems(getContract(), selection,
                selectionArgs);
        if (!UriUtils.isHasKey(uri, IS_NO_NOTIFAED)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int inserted = mDataBase.addItems(getContract(), values);
        if (!UriUtils.isHasKey(uri, IS_NO_NOTIFAED)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return inserted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues value) {
        long id = mDataBase.addItem(getContract(), value);
        Uri itemUri = Uri.parse(uri + "/" + id);
        if (id > 0) {
            if (!UriUtils.isHasKey(uri, IS_NO_NOTIFAED)) {
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
        }
        return itemUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor items = mDataBase.getItems(getContract(), selection, selectionArgs, sortOrder);
        if (!UriUtils.isHasKey(uri, IS_NO_NOTIFAED)) {
            items.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return items;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    public Cursor rawQuery(Uri uri, String sql, String[] selectionArgs) {
        Cursor items = mDataBase.rawQuery(getContract(), sql, selectionArgs);
        if (!UriUtils.isHasKey(uri, IS_NO_NOTIFAED)) {
            items.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return items;
    }

}
