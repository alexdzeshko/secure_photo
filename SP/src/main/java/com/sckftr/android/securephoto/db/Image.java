package com.sckftr.android.securephoto.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import com.sckftr.android.securephoto.contract.Contracts;

public class Image implements Cryptonite {

    private String key, uri;

    public Image(String key, String uri) {
        this.key = key;
        this.uri = uri;
    }

    public Image(String key, Uri uri) {
        this(key, uri.toString());
    }

    public Image(Cursor cursor){
        key = cursor.getString(cursor.getColumnIndex(Contracts.ImageContract.KEY));
        uri = cursor.getString(cursor.getColumnIndex(Contracts.ImageContract.URI));
    }


    public String getKey() {
        return key;
    }

    public String getUri() {
        return uri;
    }

    public Uri getURI() {
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
