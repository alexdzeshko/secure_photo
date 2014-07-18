package by.deniotokiari.core.helpers;

import android.database.Cursor;

public class CursorHelper {

    public static String getString(Cursor cursor, String key) {

        return cursor.getString(cursor.getColumnIndex(key));

    }

    public static void close(Cursor cursor) {

        if (cursor != null && !cursor.isClosed()) cursor.close();

    }
}
