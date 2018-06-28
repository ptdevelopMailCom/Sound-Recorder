package com.example.ptdev.soundrecordercopy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.ptdev.soundrecordercopy.R;
import com.example.ptdev.soundrecordercopy.RecordItem;
import com.example.ptdev.soundrecordercopy.listener.onDatabaseChangedListener;

import static com.example.ptdev.soundrecordercopy.database.RecordingDbHelper.RecordingEntry.COLUMN_NAME_RECORDING_NAME;
import static com.example.ptdev.soundrecordercopy.database.RecordingDbHelper.RecordingEntry.TABLE_NAME;

public class RecordingDbHelper extends SQLiteOpenHelper {
    public static final String TAG = "RecordingDbHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Recordings.db";

    public static class RecordingEntry implements BaseColumns{
        public static final String TABLE_NAME = "save_recordings";
        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordingEntry.TABLE_NAME + "(" +
                    RecordingEntry._ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME_RECORDING_NAME + " TEXT, " +
                    RecordingEntry.COLUMN_NAME_RECORDING_FILE_PATH + " TEXT, " +
                    RecordingEntry.COLUMN_NAME_RECORDING_LENGTH + " INTEGER, " +
                    RecordingEntry.COLUMN_NAME_TIME_ADDED + " INTEGER) ";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecordingEntry.TABLE_NAME;


    private static onDatabaseChangedListener mOnDatabaseChangedListener;

    public void setOnDatabaseChangedListener(onDatabaseChangedListener listener){
        mOnDatabaseChangedListener = listener;
        Log.i("debugUsage", "running in db helper");
    }

    public RecordingDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public int getCount(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {RecordingEntry._ID};
        Cursor c = db.query(RecordingEntry.TABLE_NAME, projection, null, null, null, null, null);

        int count = c.getCount();
        c.close();

        return count;
    }

    public long add(String recordingName, String path, long length){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RecordingEntry.COLUMN_NAME_RECORDING_NAME, recordingName);
        values.put(RecordingEntry.COLUMN_NAME_RECORDING_FILE_PATH, path);
        values.put(RecordingEntry.COLUMN_NAME_RECORDING_LENGTH, length);
        values.put(RecordingEntry.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());

        long rowId = db.insert(RecordingEntry.TABLE_NAME, null, values);


        if (mOnDatabaseChangedListener != null){
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }

    public void rename(RecordItem recordItem, String recordingName, String path){
        ContentValues values = new ContentValues();
        values.put(RecordingEntry.COLUMN_NAME_RECORDING_NAME, recordingName);
        values.put(RecordingEntry.COLUMN_NAME_RECORDING_FILE_PATH, path);

        SQLiteDatabase db = this.getWritableDatabase();
        //db.update(RecordingEntry.TABLE_NAME, values, RecordingEntry._ID + "=" + recordItem.getId(), null);
        db.update(RecordingEntry.TABLE_NAME, values,
                RecordingEntry._ID + "=" + recordItem.getId(), null);

        if (mOnDatabaseChangedListener != null){
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
        }

    }

    public void delete(RecordItem recordItem){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(RecordingEntry.TABLE_NAME, RecordingEntry._ID + "=" + recordItem.getId(), null);

        if (mOnDatabaseChangedListener != null){
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
            Log.i(TAG, "it runs");
        }
    }

    public RecordItem getItemAt(int position){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                RecordingEntry._ID,
                RecordingEntry.COLUMN_NAME_RECORDING_NAME,
                RecordingEntry.COLUMN_NAME_RECORDING_FILE_PATH,
                RecordingEntry.COLUMN_NAME_RECORDING_LENGTH,
                RecordingEntry.COLUMN_NAME_TIME_ADDED
        };
        Cursor c = db.query(RecordingEntry.TABLE_NAME, projection,null, null, null, null, null, null);

        if (c != null && c.moveToPosition(position)){
            RecordItem recordItem = new RecordItem();
            recordItem.setId(c.getInt(c.getColumnIndex(RecordingEntry._ID)));
            recordItem.setFileName(c.getString(c.getColumnIndex(RecordingEntry.COLUMN_NAME_RECORDING_NAME)));
            recordItem.setFilePath(c.getString(c.getColumnIndex(RecordingEntry.COLUMN_NAME_RECORDING_FILE_PATH)));
            recordItem.setLength(c.getInt(c.getColumnIndex(RecordingEntry.COLUMN_NAME_RECORDING_LENGTH)));
            recordItem.setDate(c.getLong(c.getColumnIndex(RecordingEntry.COLUMN_NAME_TIME_ADDED)));

            c.close();
            return recordItem;
        }
        return null;
    }


}
