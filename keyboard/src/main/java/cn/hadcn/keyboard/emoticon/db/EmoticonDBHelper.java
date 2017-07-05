package cn.hadcn.keyboard.emoticon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;

/**
 * @author chris
 */
public class EmoticonDBHelper {
    private final static String TAG = "EmoticonDBHelper";
    private static final int VERSION = 5;
    private static final String DATABASE_NAME = "chatkeyboard.db";
    private static final String TABLE_NAME_EMOTICON = "emoticons";
    private static final String TABLE_NAME_EMOTICON_SET = "emoticonset";
    private DBOpenHelper mOpenDbHelper;

    public EmoticonDBHelper(Context context) {
        mOpenDbHelper = new DBOpenHelper(context);
    }

    private ContentValues createEmoticonSetContentValues(EmoticonBean bean, String beanSetName) {
        if (bean == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonItem.EVENT_TYPE, bean.getEventType());
        values.put(TableColumns.EmoticonItem.TAG, bean.getTag());
        values.put(TableColumns.EmoticonItem.NAME, bean.getName());
        values.put(TableColumns.EmoticonItem.ICON_URI, bean.getIconUri());
        values.put(TableColumns.EmoticonItem.MSG_URI, bean.getMsgUri());
        values.put(TableColumns.EmoticonItem.EMOTICON_SET_NAME, beanSetName);
        return values;
    }

    public synchronized long insertEmoticonBean(EmoticonBean bean, String beanSetName) {
        SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        long result = -1;
        if (bean == null || db == null) {
            return result;
        }
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonItem.EVENT_TYPE, bean.getEventType());
        values.put(TableColumns.EmoticonItem.TAG, bean.getTag());
        values.put(TableColumns.EmoticonItem.NAME, bean.getName());
        values.put(TableColumns.EmoticonItem.ICON_URI, bean.getIconUri());
        values.put(TableColumns.EmoticonItem.MSG_URI, bean.getMsgUri());
        values.put(TableColumns.EmoticonItem.EMOTICON_SET_NAME, beanSetName);
        try {
            result = db.insert(TABLE_NAME_EMOTICON, null, values);
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "insert failed", e);
        }
        return result;
    }

    private synchronized long insertEmoticonBeans(ContentValues[] values) {
        SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        db.beginTransaction();
        int insertSuccessCount = values.length;
        try {
            for (ContentValues cv : values) {
                if (db.insert(TABLE_NAME_EMOTICON, null, cv) < 0) {
                    insertSuccessCount--;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Keyboard", "insert error", e);
        } finally {
            db.endTransaction();
        }
        return insertSuccessCount;
    }

    public synchronized long insertEmoticonSet(EmoticonSetBean bean) {
        SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        long result = -1;
        if (bean == null || db == null || TextUtils.isEmpty(bean.getName())) {
            return result;
        }

        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSet.NAME, bean.getName());
        values.put(TableColumns.EmoticonSet.LINE, bean.getLine());
        values.put(TableColumns.EmoticonSet.ROW, bean.getRow());
        values.put(TableColumns.EmoticonSet.ICON_URI, bean.getIconUri());
        values.put(TableColumns.EmoticonSet.IS_SHOW_NAME, bean.isShownName());
        values.put(TableColumns.EmoticonSet.IS_SHOW_DEL_BTN, bean.isShowDelBtn() ? 1 : 0);
        values.put(TableColumns.EmoticonSet.ITEM_PADDING, bean.getItemPadding());
        values.put(TableColumns.EmoticonSet.HORIZONTAL_SPACING, bean.getHorizontalSpacing());
        values.put(TableColumns.EmoticonSet.VERTICAL_SPACING, bean.getVerticalSpacing());
        result = db.insert(TABLE_NAME_EMOTICON_SET, null, values);

        List<EmoticonBean> emoticonList = bean.getEmoticonList();
        if (emoticonList != null) {
            String emoticonSetName = bean.getName();
            ContentValues[] contentValues = new ContentValues[emoticonList.size()];
            for (int i = 0; i < emoticonList.size(); i++) {
                contentValues[i] = createEmoticonSetContentValues(emoticonList.get(i),
                        emoticonSetName);
            }
            insertEmoticonBeans(contentValues);
        }
        return result;
    }

    public synchronized EmoticonBean queryEmoticonBean(String contentStr) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME_EMOTICON + " where " + TableColumns
                .EmoticonItem.TAG + " = '" + contentStr + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long eventType = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonItem
                    .EVENT_TYPE));
            String tag = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem.TAG));
            String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                    .ICON_URI));
            String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                    .NAME));
            return new EmoticonBean(eventType, iconUri, tag, name);
        }
        cursor.close();
        return null;
    }

    public synchronized String getUriByTag(String tag) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME_EMOTICON + " where " + TableColumns
                .EmoticonItem.TAG + " = '" + tag + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        //first check msg uri
        String msgUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                .MSG_URI));

        if (msgUri != null) {
            cursor.close();
            return msgUri;
        }
        // if msgUri is null, use icon uri
        String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                .ICON_URI));
        cursor.close();
        return iconUri;
    }

    private synchronized List<EmoticonBean> queryEmoticonBeanList(String sql) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int count = cursor.getCount();
        ArrayList<EmoticonBean> beanList = new ArrayList<>();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                long eventType = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonItem
                        .EVENT_TYPE));
                String tag = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                        .TAG));
                String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                        .NAME));
                String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonItem
                        .ICON_URI));
                EmoticonBean bean = new EmoticonBean(eventType, iconUri, tag, name);
                beanList.add(bean);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return beanList;
    }

    public synchronized List<EmoticonBean> queryAllEmoticonBeans() {
        String sql = "select * from " + TABLE_NAME_EMOTICON;
        return queryEmoticonBeanList(sql);
    }

    public synchronized List<EmoticonSetBean> queryEmoticonSet(String... setNames) {
        if (setNames == null || setNames.length == 0) {
            return new ArrayList<>();
        }
        String sql = "select * from " + TABLE_NAME_EMOTICON_SET + " where ";
        for (int i = 0; i < setNames.length; i++) {
            if (i != 0) {
                sql += " or ";
            }
            sql = sql + TableColumns.EmoticonSet.NAME + " = '" + setNames[i] + "' ";
        }
        return queryEmoticonSet(sql);
    }

    public synchronized List<EmoticonSetBean> queryEmoticonSet(List<String> setNameList) {
        if (setNameList == null || setNameList.isEmpty()) {
            return new ArrayList<>();
        }
        String sql = "select * from " + TABLE_NAME_EMOTICON_SET + " where ";
        int i = 0;
        for (String name : setNameList) {
            if (i != 0) {
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSet.NAME + " = '" + name + "' ";
            i++;
        }
        return queryEmoticonSet(sql);
    }

    public synchronized List<EmoticonSetBean> queryAllEmoticonSet() {
        String sql = "select * from " + TABLE_NAME_EMOTICON_SET;
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            List<EmoticonSetBean> beanList = new ArrayList<>();

            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSet
                            .NAME));
                    int line = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSet.LINE));
                    int row = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSet
                            .ROW));
                    String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns
                            .EmoticonSet.ICON_URI));
                    boolean isshowdelbtn = cursor.getInt(cursor.getColumnIndex(TableColumns
                            .EmoticonSet.IS_SHOW_DEL_BTN)) == 1;
                    int itempadding = cursor.getInt(cursor.getColumnIndex(TableColumns
                            .EmoticonSet.ITEM_PADDING));
                    int horizontalspacing = cursor.getInt(cursor.getColumnIndex(TableColumns
                            .EmoticonSet.HORIZONTAL_SPACING));
                    int verticalSpacing = cursor.getInt(cursor.getColumnIndex(TableColumns
                            .EmoticonSet.VERTICAL_SPACING));
                    boolean isShownName = cursor.getInt(cursor.getColumnIndex(TableColumns
                            .EmoticonSet.IS_SHOW_NAME)) == 1;

                    List<EmoticonBean> emoticonList = null;
                    if (!TextUtils.isEmpty(name)) {
                        String sqlGetEmoticonBean = "select * from " + TABLE_NAME_EMOTICON + " " +
                                "where " + TableColumns.EmoticonItem.EMOTICON_SET_NAME + " = " +
                                "'" + name + "'";
                        emoticonList = queryEmoticonBeanList(sqlGetEmoticonBean);
                    }

                    EmoticonSetBean bean = new EmoticonSetBean(name, line, row, iconUri,
                            isshowdelbtn, isShownName, itempadding, horizontalspacing,
                            verticalSpacing, emoticonList);
                    beanList.add(bean);
                    cursor.moveToNext();
                }
                return beanList;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "query failed", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return new ArrayList<>();
    }

    public synchronized void cleanup() {
        mOpenDbHelper.close();
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        private void createEmoticonsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICON + " ( " +
                    TableColumns.EmoticonItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonItem.EVENT_TYPE + " INTEGER, " +
                    TableColumns.EmoticonItem.TAG + " TEXT NOT NULL UNIQUE, " +
                    TableColumns.EmoticonItem.NAME + " TEXT, " +
                    TableColumns.EmoticonItem.ICON_URI + " TEXT NOT NULL, " +
                    TableColumns.EmoticonItem.MSG_URI + " TEXT, " +
                    TableColumns.EmoticonItem.EMOTICON_SET_NAME + " TEXT NOT NULL);");

            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICON_SET + " ( " +
                    TableColumns.EmoticonSet._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonSet.NAME + " TEXT NOT NULL UNIQUE, " +
                    TableColumns.EmoticonSet.LINE + " INTEGER, " +
                    TableColumns.EmoticonSet.ROW + " INTEGER, " +
                    TableColumns.EmoticonSet.ICON_URI + " TEXT, " +
                    TableColumns.EmoticonSet.IS_SHOW_DEL_BTN + " BOOLEAN, " +
                    TableColumns.EmoticonSet.IS_SHOW_NAME + " BOOLEAN, " +
                    TableColumns.EmoticonSet.ITEM_PADDING + " INTEGER, " +
                    TableColumns.EmoticonSet.HORIZONTAL_SPACING + " INTEGER, " +
                    TableColumns.EmoticonSet.VERTICAL_SPACING + " TEXT);");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createEmoticonsTable(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int currentVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EMOTICON);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EMOTICON_SET);
            onCreate(sqLiteDatabase);
        }
    }
}