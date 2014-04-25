package by.deniotokiari.core.helpers;

import android.database.Cursor;

public class CursorHelper {
 
	public static String get(Cursor cursor, String key) {
		return cursor.getString(cursor.getColumnIndex(key));
	}
	
}
