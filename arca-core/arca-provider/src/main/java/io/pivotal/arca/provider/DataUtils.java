/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.pivotal.arca.utils.Logger;

public class DataUtils {

    public static ContentValues[] getContentValues(final Cursor cursor, final Class<?> klass) {
        final ContentValues[] values = new ContentValues[cursor.getCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = getContentValues(cursor, klass, i);
        }
        return values;
    }

    public static ContentValues getContentValues(final Cursor cursor, final Class<?> klass, final int position) {
        final ContentValues values = new ContentValues();
        try {
            if (cursor.moveToPosition(position)) {
                getColumns(cursor, klass, values);
            }
        } catch (final Exception e) {
            Logger.ex(e);
        }
        return values;
    }

    private static void getColumns(final Cursor cursor, final Class<?> klass, final ContentValues values) throws Exception {
        final Field[] fields = klass.getFields();
        for (final Field field : fields) {
            getField(cursor, field, values);
        }

        final Class<?>[] klasses = klass.getDeclaredClasses();
        for (int i = 0; i < klasses.length; i++) {
            getColumns(cursor, klasses[i], values);
        }
    }

    private static void getField(final Cursor cursor, final Field field, final ContentValues values) throws Exception {
        final Column columnType = field.getAnnotation(Column.class);
        if (columnType != null) {

            final String columnName = (String) field.get(null);
            final int columnIndex = cursor.getColumnIndex(columnName);

            if (columnType.value() == Column.Type.TEXT) {
                values.put(columnName, cursor.getString(columnIndex));

            } else if (columnType.value() == Column.Type.BLOB) {
                values.put(columnName, cursor.getBlob(columnIndex));

            } else if (columnType.value() == Column.Type.INTEGER) {
                values.put(columnName, cursor.getLong(columnIndex));

            } else if (columnType.value() == Column.Type.REAL) {
                values.put(columnName, cursor.getFloat(columnIndex));
            }
        }
    }


    // ================================================


    public static ContentValues[] getContentValues(final List<?> list) {
        return getContentValues(list, null);
    }

    public static ContentValues[] getContentValues(final List<?> list, final ContentValues custom) {
        final ContentValues[] values = new ContentValues[list.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = getContentValues(list.get(i));
            if (custom != null) {
                values[i].putAll(custom);
            }
        }
        return values;
    }

    public static ContentValues getContentValues(final Object object) {
        final Class<?> klass = object.getClass();
        final ContentValues values = new ContentValues();
        try {
            getContentValuesFromFields(object, klass, values);
            getContentValuesFromMethods(object, klass, values);
        } catch (final Exception e) {
            Logger.ex(e);
        }
        return values;
    }

    private static void getContentValuesFromMethods(final Object object, final Class<?> klass, final ContentValues values) throws Exception {
        final Method[] methods = klass.getDeclaredMethods();
        for (final Method method : methods) {
            final ColumnName annotation = method.getAnnotation(ColumnName.class);
            if (annotation != null) {
                method.setAccessible(true);
                final Object value = method.invoke(object);
                addToValues(values, annotation.value(), value);
            }
        }

        final Class<?> superKlass = klass.getSuperclass();
        if (superKlass != null) {
            getContentValuesFromMethods(object, superKlass, values);
        }
    }

    private static void getContentValuesFromFields(final Object object, final Class<?> klass, final ContentValues values) throws Exception {
        final Field[] fields = klass.getDeclaredFields();
        for (final Field field : fields) {
            final ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                field.setAccessible(true);
                final Object value = field.get(object);
                addToValues(values, annotation.value(), value);
            }
        }

        final Class<?> superKlass = klass.getSuperclass();
        if (superKlass != null) {
            getContentValuesFromFields(object, superKlass, values);
        }
    }

    private static void addToValues(final ContentValues values, final String key, final Object value) throws Exception {
        if (value != null) {
            final Class<?> klass = values.getClass();
            final Method method = klass.getMethod("put", String.class, value.getClass());
            method.invoke(values, key, value);
        }
    }


    // ================================================


    public static <T> List<T> getList(final Cursor cursor, final Class<T> klass) {
        try {
            return getObjectsFromCursor(cursor, klass);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    private static <T> List<T> getObjectsFromCursor(final Cursor cursor, final Class<T> klass) throws IllegalAccessException, InstantiationException {
        final List<T> objects = new ArrayList<T>();
        while (cursor.moveToNext()) {
            final T object = getObjectFromCursor(cursor, klass);
            objects.add(object);
        }
        return objects;
    }

    private static <T> T getObjectFromCursor(final Cursor cursor, final Class<T> klass) throws IllegalAccessException, InstantiationException {
        final T object = klass.newInstance();
        final Field[] fields = klass.getDeclaredFields();
        for (final Field field : fields) {
            final ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                final int columnIndex = cursor.getColumnIndex(annotation.value());
                if (columnIndex > -1) {
                    getFieldFromCursor(cursor, object, field, columnIndex);
                }
            }
        }
        return object;
    }

    private static <T> void getFieldFromCursor(final Cursor cursor, final T object, final Field field, int columnIndex) throws IllegalAccessException {
        field.setAccessible(true);

        final Class<?> type = field.getType();
        if (type == String.class) {
            field.set(object, cursor.getString(columnIndex));

        } else if (type == byte[].class) {
            field.set(object, cursor.getBlob(columnIndex));

        } else if (type == Integer.class || type == int.class) {
            field.set(object, cursor.getInt(columnIndex));

        } else if (type == Boolean.class || type == boolean.class) {
            field.set(object, cursor.getInt(columnIndex) != 0);

        } else if (type == Float.class || type == float.class) {
            field.set(object, cursor.getFloat(columnIndex));

        } else if (type == Long.class || type == long.class) {
            field.set(object, cursor.getLong(columnIndex));

        } else if (type == Double.class || type == double.class) {
            field.set(object, cursor.getDouble(columnIndex));

        } else if (type == Short.class || type == short.class) {
            field.set(object, (short) cursor.getInt(columnIndex));

        } else if (type == Byte.class || type == byte.class) {
            field.set(object, (byte) cursor.getInt(columnIndex));

        } else if (type == Character.class || type == char.class) {
            final String string = cursor.getString(columnIndex);
            final boolean isNotEmpty = string != null && string.length() > 0;
            field.set(object, isNotEmpty ? string.charAt(0) : '\0');
        }
    }


    // ================================================


    public static <T> List<T> getList(final ContentValues[] values, final Class<T> klass) {
        final List<T> objects = new ArrayList<T>();
        for (int i = 0; i < values.length; i++) {
            final T object = getObject(values[i], klass);
            if (object != null) {
                objects.add(object);
            }
        }
        return objects;
    }

    public static <T> T getObject(final ContentValues values, final Class<T> klass) {
        try {
            return getObjectsFromValues(values, klass);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    private static <T> T getObjectsFromValues(final ContentValues values, final Class<T> klass) throws IllegalAccessException, InstantiationException {
        final T object = klass.newInstance();
        final Field[] fields = klass.getDeclaredFields();
        for (final Field field : fields) {
            final ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                getFieldFromValues(values, object, field, annotation.value());
            }
        }
        return object;
    }

    private static <T> void getFieldFromValues(final ContentValues values, final T object, final Field field, final String value) throws IllegalAccessException {
        field.setAccessible(true);

        final Class<?> type = field.getType();
        if (type == String.class) {
            field.set(object, values.getAsString(value));

        } else if (type == byte[].class) {
            field.set(object, values.getAsByteArray(value));

        } else if (type == Integer.class || type == int.class) {
            final Integer asInteger = values.getAsInteger(value);
            if (asInteger != null) field.set(object, asInteger);

        } else if (type == Boolean.class || type == boolean.class) {
            final Boolean asBoolean = values.getAsBoolean(value);
            if (asBoolean != null) field.set(object, asBoolean);

        } else if (type == Float.class || type == float.class) {
            final Float asFloat = values.getAsFloat(value);
            if (asFloat != null) field.set(object, asFloat);

        } else if (type == Long.class || type == long.class) {
            final Long asLong = values.getAsLong(value);
            if (asLong != null) field.set(object, asLong);

        } else if (type == Double.class || type == double.class) {
            final Double asDouble = values.getAsDouble(value);
            if (asDouble != null) field.set(object, asDouble);

        } else if (type == Short.class || type == short.class) {
            final Short asShort = values.getAsShort(value);
            if (asShort != null) field.set(object, asShort);

        } else if (type == Byte.class || type == byte.class) {
            final Byte asByte = values.getAsByte(value);
            if (asByte != null) field.set(object, asByte);

        } else if (type == Character.class) {
            final String string = values.getAsString(value);
            final boolean isNotEmpty = string != null && string.length() > 0;
            field.set(object, isNotEmpty ? string.charAt(0) : null);

        } else if (type == char.class) {
            final String string = values.getAsString(value);
            final boolean isNotEmpty = string != null && string.length() > 0;
            field.set(object, isNotEmpty ? string.charAt(0) : '\0');
        }
    }


    // =============================================


    public static <T> MatrixCursor getCursor(final List<T> list) {
        if (list == null || list.isEmpty())
            return new MatrixCursor(new String[] { "_id" });

        final String[] titles = getFieldTitlesFromObject(list.get(0));

        final MatrixCursor cursor = new MatrixCursor(titles);

        try {
            addFieldValuesFromList(cursor, list);
        } catch (final IllegalAccessException e) {
            Logger.ex(e);
        }

        return cursor;
    }

    private static <T> String[] getFieldTitlesFromObject(final T object) {
        final List<String> titles = new ArrayList<String>();
        titles.add("_id");

        final Field[] fields = object.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);

            final ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                titles.add(annotation.value());
            } else {
                titles.add(field.getName());
            }
        }

        return titles.toArray(new String[titles.size()]);
    }

    private static <T> void addFieldValuesFromList(final MatrixCursor cursor, final List<T> list) throws IllegalAccessException {
        int position = 0;
        for (final T object : list) {
            final List<String> values = getFieldValuesFromObject(object, position++);
            cursor.addRow(values);
        }
    }

    private static <T> List<String> getFieldValuesFromObject(final T object, final int position) throws IllegalAccessException {
        final List<String> values = new ArrayList<String>();
        values.add(String.valueOf(position));

        final Field[] fields = object.getClass().getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);

            final Object value = field.get(object);

            values.add(String.valueOf(value));
        }

        return values;
    }

}
