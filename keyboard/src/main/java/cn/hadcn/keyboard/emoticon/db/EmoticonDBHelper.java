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

import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.emoticon.EmoticonSetBean;


public class EmoticonDBHelper {
    private final static String TAG = "EmoticonDBHelper";
    private static final int VERSION = 5;

    private static final String DATABASE_NAME = "xhsemoticons.db";
    private static final String TABLE_NAME_EMOTICONS = "emoticons";
    private static final String TABLE_NAME_EMOTICONSET = "emoticonset";

    private DBOpenHelper mOpenDbHelper;

    public EmoticonDBHelper(Context context) {
        mOpenDbHelper = new DBOpenHelper(context);
    }

    public ContentValues createEmoticonSetContentValues(EmoticonBean bean, String beanSetName) {
        if (bean == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonColumns.EVENT_TYPE, bean.getEventType());
        values.put(TableColumns.EmoticonColumns.TAG, bean.getTag());
        values.put(TableColumns.EmoticonColumns.NAME, bean.getName());
        values.put(TableColumns.EmoticonColumns.ICON_URI, bean.getIconUri());
        values.put(TableColumns.EmoticonColumns.MSG_URI, bean.getMsgUri());
        values.put(TableColumns.EmoticonColumns.EMOTICON_SET_NAME, beanSetName);
        return values;
    }

    public synchronized long insertEmoticonBean(EmoticonBean bean, String beanSetName) {
        SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        long result = -1;
        if (bean == null || db == null) {
            return result;
        }
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonColumns.EVENT_TYPE, bean.getEventType());
        values.put(TableColumns.EmoticonColumns.TAG, bean.getTag());
        values.put(TableColumns.EmoticonColumns.NAME, bean.getName());
        values.put(TableColumns.EmoticonColumns.ICON_URI, bean.getIconUri());
        values.put(TableColumns.EmoticonColumns.MSG_URI, bean.getMsgUri());
        values.put(TableColumns.EmoticonColumns.EMOTICON_SET_NAME, beanSetName);
        try {
            result = db.insert(TABLE_NAME_EMOTICONS, null, values);
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "insert failed", e);
        }
        return result;
    }

    public synchronized long insertEmoticonBeans(ContentValues[] values) {
        SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        db.beginTransaction();
        int insertSuccessCount = values.length;
        try {
            for (ContentValues cv : values) {
                if (db.insert(TABLE_NAME_EMOTICONS, null, cv) < 0) {
                    insertSuccessCount--;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Keyboard", "insert error", e);
        }  finally {
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
        values.put(TableColumns.EmoticonSetColumns.NAME, bean.getName());
        values.put(TableColumns.EmoticonSetColumns.LINE, bean.getLine());
        values.put(TableColumns.EmoticonSetColumns.ROW, bean.getRow());
        values.put(TableColumns.EmoticonSetColumns.ICON_URI, bean.getIconUri());
        values.put(TableColumns.EmoticonSetColumns.IS_SHOWN_NAME, bean.isShownName());
        values.put(TableColumns.EmoticonSetColumns.IS_SHOW_DEL_BTN, bean.isShowDelBtn() ? 1 : 0);
        values.put(TableColumns.EmoticonSetColumns.ITEM_PADDING, bean.getItemPadding());
        values.put(TableColumns.EmoticonSetColumns.HORIZONTAL_SPACING, bean.getHorizontalSpacing());
        values.put(TableColumns.EmoticonSetColumns.VERTICAL_SPACING, bean.getVerticalSpacing());
        result = db.insert(TABLE_NAME_EMOTICONSET, null, values);

        ArrayList<EmoticonBean> emoticonList = bean.getEmoticonList();
        if (emoticonList != null) {
            String emoticonSetName = bean.getName();
            ContentValues[] contentValues = new ContentValues[emoticonList.size()];
            for (int i = 0; i < emoticonList.size(); i++) {
                contentValues[i] = createEmoticonSetContentValues(emoticonList.get(i), emoticonSetName);
            }
            insertEmoticonBeans(contentValues);
        }
        return result;
    }

    public synchronized EmoticonBean queryEmoticonBean(String contentStr) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.TAG + " = '" + contentStr + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long eventType = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonColumns.EVENT_TYPE));
            String tag = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.TAG));
            String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICON_URI));
            String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.NAME));
            return new EmoticonBean(eventType, iconUri, tag, name);
        }
        cursor.close();
        return null;
    }

    public synchronized String getUriByTag(String tag) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.TAG + " = '" + tag + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        //first check msg uri
        String msgUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.MSG_URI));

        if (msgUri != null) {
            cursor.close();
            return msgUri;
        }
        // if msgUri is null, use icon uri
        String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICON_URI));
        cursor.close();
        return iconUri;
    }

    public synchronized ArrayList<EmoticonBean> queryEmoticonBeanList(String sql) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int count = cursor.getCount();
        ArrayList<EmoticonBean> beanList = new ArrayList<>();
        if (count > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                long eventType = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonColumns.EVENT_TYPE));
                String tag = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.TAG));
                String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.NAME));
                String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICON_URI));
                EmoticonBean bean = new EmoticonBean(eventType, iconUri, tag, name);
                beanList.add(bean);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return beanList;
    }

    public synchronized ArrayList<EmoticonBean> queryAllEmoticonBeans() {
        String sql = "select * from " + TABLE_NAME_EMOTICONS;
        return queryEmoticonBeanList(sql);
    }

    public synchronized ArrayList<EmoticonSetBean> queryEmoticonSet(String... setNames) {
        if(setNames == null || setNames.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " ;
        for(int i = 0 ;i < setNames.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.NAME + " = '" + setNames[i] + "' ";
        }
        return queryEmoticonSet(sql);
    }

    public synchronized ArrayList<EmoticonSetBean> queryEmoticonSet(ArrayList<String> setNameList) {
        if(setNameList == null || setNameList.size() == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " ;
        int i = 0 ;
        for(String name : setNameList){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.NAME + " = '" + name + "' ";
            i++;
        }
        return queryEmoticonSet(sql);
    }

    public synchronized ArrayList<EmoticonSetBean> queryAllEmoticonSet() {
        String sql = "select * from " + TABLE_NAME_EMOTICONSET;
        return queryEmoticonSet(sql);
    }

    public synchronized ArrayList<EmoticonSetBean> queryEmoticonSet(String sql) {
        SQLiteDatabase db = mOpenDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            ArrayList<EmoticonSetBean> beanList = new ArrayList<EmoticonSetBean>();
            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.NAME));
                    int line = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.LINE));
                    int row = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ROW));
                    String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICON_URI));
                    boolean isshowdelbtn = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.IS_SHOW_DEL_BTN)) == 1;
                    int itempadding = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ITEM_PADDING));
                    int horizontalspacing = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.HORIZONTAL_SPACING));
                    int verticalSpacing = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.VERTICAL_SPACING));
                    boolean isShownName = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.IS_SHOWN_NAME)) == 1;

                    ArrayList<EmoticonBean> emoticonList = null;
                    if (!TextUtils.isEmpty(name)) {
                        String sqlGetEmoticonBean = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.EMOTICON_SET_NAME + " = '" + name + "'";
                        emoticonList = queryEmoticonBeanList(sqlGetEmoticonBean);
                    }

                    int pageCount = 0;
                    if (emoticonList != null) {
                        int del = isshowdelbtn ? 1 : 0;
                        int everyPageMaxSum = row * line - del;
                        pageCount = (int) Math.ceil((double) emoticonList.size() / everyPageMaxSum);
                    }

                    EmoticonSetBean bean = new EmoticonSetBean(name, line, row, iconUri, isshowdelbtn, isShownName, itempadding, horizontalspacing, verticalSpacing, emoticonList);
                    beanList.add(bean);
                    cursor.moveToNext();
                }
                return beanList;
            }
        } catch (SQLiteException e){
            Log.e(TAG, "query failed", e);
        } finally {
            if ( cursor != null ) cursor.close();
        }
        return null;
    }


    public synchronized void cleanup() {
        mOpenDbHelper.close();
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        private void createEmoticonsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICONS + " ( " +
                    TableColumns.EmoticonColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonColumns.EVENT_TYPE + " INTEGER, " +
                    TableColumns.EmoticonColumns.TAG + " TEXT NOT NULL UNIQUE, " +
                    TableColumns.EmoticonColumns.NAME + " TEXT, " +
                    TableColumns.EmoticonColumns.ICON_URI + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.MSG_URI + " TEXT, " +
                    TableColumns.EmoticonColumns.EMOTICON_SET_NAME + " TEXT NOT NULL);");


            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICONSET + " ( " +
                    TableColumns.EmoticonSetColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonSetColumns.NAME + " TEXT NOT NULL UNIQUE, " +
                    TableColumns.EmoticonSetColumns.LINE + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ROW + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ICON_URI + " TEXT, " +
                    TableColumns.EmoticonSetColumns.IS_SHOW_DEL_BTN + " BOOLEAN, " +
                    TableColumns.EmoticonSetColumns.IS_SHOWN_NAME + " BOOLEAN, " +
                    TableColumns.EmoticonSetColumns.ITEM_PADDING + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.HORIZONTAL_SPACING + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.VERTICAL_SPACING + " TEXT);");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createEmoticonsTable(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int currentVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EMOTICONS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EMOTICONSET);
            onCreate(sqLiteDatabase);
        }
    }
}