package com.sckftr.android.utils;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

public final class CursorUtils {

	public static String getString(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return (columnIndex == -1) ? "["+columnName+"]" : cursor.getString(columnIndex);
	}
	
	public static Integer getInteger(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return (columnIndex == -1) ? -1 : cursor.getInt(columnIndex);
	}
	
	public static byte getByte(String columnName, Cursor cursor) {
		return getInteger(columnName, cursor).byteValue();
	}
	
	public static Double getDouble(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return (columnIndex == -1) ? -1 : cursor.getDouble(columnIndex);
	}
	
	public static Float getFloat(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return (columnIndex == -1) ? -1 : cursor.getFloat(columnIndex);
	}
	
	public static Long getLong(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return (columnIndex == -1) ? -1 : cursor.getLong(columnIndex);
	}
	
	public static Short getShort(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getShort(columnIndex);
	}
	
	public static byte[] getBlob(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getBlob(columnIndex);
	}

	public static boolean isEmpty(Cursor cursor) {
		return cursor == null || cursor.getCount() == 0;
	}

	public static void close(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

    public static boolean isClosed(Cursor cursor) {
        return cursor == null || cursor.isClosed();
    }

    public static boolean getBoolean(String columnName, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex == -1) {
            return false;
        }
        return cursor.getInt(columnIndex) == 1;
    }

    public static void convertToContentValues(Cursor cursor, List<ContentValues> list) {
        if (isEmpty(cursor)) {
            return;
        }
        cursor.moveToFirst();
        do {
            ContentValues contentValues = new ContentValues();
            //converter.convert(cursor, contentValues);
            list.add(contentValues);
        } while (cursor.moveToNext());
    }
}
