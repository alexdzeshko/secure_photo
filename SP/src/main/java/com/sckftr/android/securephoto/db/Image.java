package com.sckftr.android.securephoto.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.utils.CursorUtils;

import by.deniotokiari.core.utils.ContractUtils;

public class Image implements Cryptonite, DbModel {

    private String key, uri, _id;

    public Image(String key, String uri) {
        this.key = key;
        this.uri = uri;
    }

    public Image(String key, Uri uri) {
        this(key, uri.toString());
    }

    public Image(Cursor cursor){
        _id = CursorUtils.getString(Contracts._ID, cursor);
        key = CursorUtils.getString(Contracts.ImageContract.KEY, cursor);
        uri = CursorUtils.getString(Contracts.ImageContract.URI, cursor);
    }


    public String getKey() {
        return key;
    }

    @Override public String get_id() {
        return null;
    }

    @Override public Uri getContentUri() {
        return ContractUtils.getUri(Contracts.ImageContract.class);
    }

    public String getFileUriString() {
        return uri;
    }

    public Uri getFileUri() {
        return Uri.parse(uri);
    }

    public Image(Parcel in) {
        key = in.readString();
        uri = in.readString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(uri);
    }

    public static final Creator CREATOR = new Creator(){

        @Override public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override public Image[] newArray(int size) {
            return new Image[0];
        }
    };

    public ContentValues contentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.ImageContract.KEY, key);
        contentValues.put(Contracts.ImageContract.URI, uri);
        return contentValues;
    }
}
