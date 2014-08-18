package com.sckftr.android.securephoto.db.base.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sckftr.android.securephoto.db.base.annotations.db.DBAutoincrement;
import com.sckftr.android.securephoto.db.base.annotations.db.DBPrimaryKey;
import com.sckftr.android.securephoto.db.base.annotations.db.DBUnique;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBBoolean;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBInteger;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBLong;
import com.sckftr.android.securephoto.db.base.annotations.db.types.DBVarchar;
import com.sckftr.android.utils.ContractUtils;

import org.apache.commons.lang3.StringUtils;

public class DbHelper {

    private static final String BAD_CONTRACT_FIELD_VALUE = "Bad contract field value";
    private static final String FIELD_UNMARKED = "The contract field: %s in %s unmarked with database types annotations";

    private static enum FIELDS {
        INTEGER, VARCHAR, BOOLEAN, BIGINT, AUTOINCREMENT
    }

    private static final String TEMPLATE_SELECT_DISTINCT_TABLE = "SELECT DISTINCT tbl_name from sqlite_master where tbl_name = '%s'";
    private static final String TEMPLATE_CREATE_TABLE = "CREATE TABLE %s (%s)";
    private static final String TEMPLATE_PRIMARY_KEY = "%s %s PRIMARY KEY";
    private static final String TEMPLATE_FIELD = "%s %s";
    private static final String TEMPLATE_UNIQUE = "UNIQUE (%s)";

    public static String getCreateTableString(Class<?> contract) {

        List<String> tableFieldsString = new ArrayList<String>();

        String tableName = ContractUtils.getTableName(contract);

        List<Field> fields = getListFields(contract.getFields());

        tableFieldsString.add(getPrimaryKey(fields));

        for (Field field : fields) {
            tableFieldsString.add(getTableFieldString(field));
        }
        String uniqueFileds = getUniqueFields(fields);
        if (uniqueFileds != null) {
            tableFieldsString.add(uniqueFileds);
        }
        return String.format(TEMPLATE_CREATE_TABLE, tableName, StringUtils.join(tableFieldsString, ", "));
    }

    private static String getPrimaryKey(List<Field> fields) {
        int i = 0;
        String result = null;
        for (Field field : fields) {
            if (isPrimaryKeyField(field)) {
                result = getTableFieldString(field);
                break;
            }
            i++;
        }
        fields.remove(i);
        return result;
    }

    private static String getTableFieldString(Field field) {
        Annotation[] annotations = field.getAnnotations();
        if (annotations.length == 0) {
            throw new IllegalArgumentException(String.format(FIELD_UNMARKED,
                    field.getName(), field.getClass().getSimpleName()));
        }
        String fieldValue = getFieldValue(field);
        String template = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof DBPrimaryKey) {
                template = TEMPLATE_PRIMARY_KEY;
                break;
            }
        }
        if (template == null) {
            template = TEMPLATE_FIELD;
        }
        String result = String.format(template, fieldValue, "%s");
        for (Annotation annotation : annotations) {
            if (annotation instanceof DBBoolean) {
                result = String.format(result, FIELDS.BOOLEAN);
            } else if (annotation instanceof DBInteger) {
                result = String.format(result, FIELDS.INTEGER);
            } else if (annotation instanceof DBLong) {
                result = String.format(result, FIELDS.BIGINT);
            } else if (annotation instanceof DBVarchar) {
                result = String.format(result, FIELDS.VARCHAR);
            }

            if (annotation instanceof DBAutoincrement) {
                result += " " + FIELDS.AUTOINCREMENT;
            }
        }
        return result;
    }

    private static String getUniqueFields(List<Field> fields) {
        List<String> items = new ArrayList<String>();
        for (Field field : fields) {
            if (field.getAnnotation(DBUnique.class) != null) {
                items.add(getFieldValue(field));
            }
        }
        if (items.size() == 0) {
            return null;
        }
        return String.format(TEMPLATE_UNIQUE, StringUtils.join(items, ", "));
    }

    private static boolean isPrimaryKeyField(Field field) {
        Annotation annotation = field.getAnnotation(DBPrimaryKey.class);
        return annotation != null;

    }

    private static List<Field> getListFields(Field[] fields) {
        List<Field> list = new ArrayList<Field>();
        Collections.addAll(list, fields);
        return list;
    }

    private static String getFieldValue(Field field) {
        String value = null;
        try {
            value = (String) field.get(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(BAD_CONTRACT_FIELD_VALUE);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(BAD_CONTRACT_FIELD_VALUE);
        }
        return value;
    }

    public static boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (db == null || tableName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            db.beginTransaction();
            cursor = db.rawQuery(
                    String.format(TEMPLATE_SELECT_DISTINCT_TABLE, tableName),
                    null);
            if (cursor.getCount() > 0) {
                db.setTransactionSuccessful();
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
    }

}
