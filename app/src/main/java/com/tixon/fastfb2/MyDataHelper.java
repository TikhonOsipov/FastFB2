package com.tixon.fastfb2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "logs_myDataHelper";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "fast_fb2_database";

    private static final String UID = "_id";
    private static final String BOOK_NAME = "book_name";
    private static final String AUTHOR_FIRST_NAME = "author_first_name";
    private static final String AUTHOR_MIDDLE_NAME = "author_middle_name";
    private static final String AUTHOR_LAST_NAME = "author_last_name";
    private static final String CHAPTER_INDEX = "last_chapter_index";
    private static final String SUBTITLE_INDEX = "last_subtitle_index";
    private static final String WORD_INDEX = "last_word_index";
    private static final String DATE = "date";
    private static final String TIME = "time";

    private static final String TABLE_MAIN_NAME = "main_table";

    private static final String CREATE_TABLE_MAIN = "create table " + TABLE_MAIN_NAME + " (" +
            UID + " integer primary key autoincrement, " +
            BOOK_NAME + " text, " + AUTHOR_FIRST_NAME + " text, " +
            AUTHOR_MIDDLE_NAME + " text, " + AUTHOR_LAST_NAME + " text, " +
            DATE + " text, " + TIME + " text, " +
            CHAPTER_INDEX + " integer, " + SUBTITLE_INDEX + " integer, " +
            WORD_INDEX + " integer" + ");";

    private static final String DELETE_TABLE_MAIN = "drop table if exists " + TABLE_MAIN_NAME;
    private static final String DELETE_ID_TABLE_MAIN = "delete sqlite_sequence where name = '" + TABLE_MAIN_NAME + "'";

    public MyDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_MAIN);
        db.execSQL(DELETE_ID_TABLE_MAIN);
        onCreate(db);
    }

    public String[] getLastBookInfo(SQLiteDatabase db, String bookName, String authorFirstName,
                             String authorMiddleName, String authorLastName) {
        String[] result = new String[5];
        Cursor c = db.rawQuery("select * from " + TABLE_MAIN_NAME + " where " +
                BOOK_NAME + " = ? AND " + AUTHOR_FIRST_NAME + " = ? AND " + AUTHOR_MIDDLE_NAME +
                " = ? AND " + AUTHOR_LAST_NAME + " = ?",
                new String[] {bookName, authorFirstName, authorMiddleName, authorLastName});

        if(c.moveToFirst()) {
            int chapterIndexCI = c.getColumnIndex(CHAPTER_INDEX);
            int subtitleIndexCI = c.getColumnIndex(SUBTITLE_INDEX);
            int wordIndexCI = c.getColumnIndex(WORD_INDEX);
            int dateCI = c.getColumnIndex(DATE);
            int timeCI = c.getColumnIndex(TIME);

            do {
                result[0] = String.valueOf(c.getInt(chapterIndexCI));
                result[1] = String.valueOf(c.getInt(subtitleIndexCI));
                result[2] = String.valueOf(c.getInt(wordIndexCI));
                result[3] = c.getString(dateCI);
                result[4] = c.getString(timeCI);
            } while(c.moveToNext());
            Log.d(LOG_TAG, "chapter = " + result[0] + ", subtitle = " + result[1] +
                    ", word = " + result[2] + ", date = " + result[3] + ", time = " + result[4]);
        } else {
            Log.d(LOG_TAG, "0 rows in " + TABLE_MAIN_NAME);
        }
        c.close();
        return result;
    }

    public long writeBookInfo(SQLiteDatabase db, String bookName, String authorFirstName,
                             String authorMiddleName, String authorLastName,
                             int chapter, int subtitle, int word, String date, String time) {
        long id;
        ContentValues cv = new ContentValues();
        cv.put(BOOK_NAME, bookName);
        cv.put(AUTHOR_FIRST_NAME, authorFirstName);
        cv.put(AUTHOR_MIDDLE_NAME, authorMiddleName);
        cv.put(AUTHOR_LAST_NAME, authorLastName);
        cv.put(CHAPTER_INDEX, chapter);
        cv.put(SUBTITLE_INDEX, subtitle);
        cv.put(WORD_INDEX, word);
        cv.put(DATE, date);
        cv.put(TIME, time);
        id = db.insert(TABLE_MAIN_NAME, null, cv);
        Log.d(LOG_TAG, "insert in " + TABLE_MAIN_NAME + ": chapter = " + chapter + ", subtitle = " +
        subtitle + ", word = " + word + ", date = " + date + ", time = " + time + ": id = " + id);
        return id;
    }
}
