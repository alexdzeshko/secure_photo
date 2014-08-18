package com.sckftr.android.securephoto.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;

import com.sckftr.android.securephoto.contract.Contracts;
import com.sckftr.android.utils.CursorUtils;
import com.sckftr.android.utils.ExifUtil;
import com.sckftr.android.utils.Storage;

import java.io.IOException;

import com.sckftr.android.utils.ContractUtils;

public class Image extends BaseModel implements Cryptonite {

    private String _id, key, uri;

    private int orientation;

    public Image(String key, String uri) {
        this.key = key;
        this.uri = uri;
    }


    public Image(Cursor cursor) {
        _id = CursorUtils.getString(Contracts._ID, cursor);
        key = CursorUtils.getString(Contracts.ImageContract.KEY, cursor);
        uri = CursorUtils.getString(Contracts.ImageContract.URI, cursor);
        orientation = CursorUtils.getInteger(Contracts.ImageContract.ORIENTATION, cursor);
    }

    public Image(Parcel in) {
        _id = in.readString();
        key = in.readString();
        uri = in.readString();
        orientation = in.readInt();
    }

    public Uri getFileUri() {
        return Uri.parse(uri);
    }

    public String getKey() {
        return key;
    }

    public int getOrientation() {
        return orientation;
    }

    public void appendImageMeta() throws IOException {

        // TODO append more metadata

        Uri path = Uri.parse(uri);

        String value = ExifUtil.getAttributeValue(path.getPath(), ExifInterface.TAG_ORIENTATION);

        orientation = ExifUtil.convertMetaOrientationToDegrees(value);
    }

    @Override
    public String get_id() {
        return _id;
    }

    @Override
    public Uri getContentUri() {
        return ContractUtils.getUri(Contracts.ImageContract.class);
    }

    @Override
    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(Contracts.ImageContract.KEY, key);
        contentValues.put(Contracts.ImageContract.URI, Storage.Images.getPrivateUri(getFileUri()).getPath());
        contentValues.put(Contracts.ImageContract.ORIENTATION, orientation);

        return contentValues;
    }

    /**
     * ******************************************************
     * <p/> Parcelable
     * ******************************************************
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(key);
        dest.writeString(uri);
        dest.writeInt(orientation);
    }

    public static final Creator CREATOR = new Creator() {

        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[0];
        }
    };
}
