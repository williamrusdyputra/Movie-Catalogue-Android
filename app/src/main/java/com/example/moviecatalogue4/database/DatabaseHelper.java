package com.example.moviecatalogue4.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "db_movie_app";

    private static final int DATABASE_VERSION = 3;

    private static final String SQL_CREATE_TABLE_MOVIE = String.format("CREATE TABLE %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "%s BLOB NOT NULL UNIQUE, " +
            "%s TEXT NOT NULL, " +
            "%s TEXT NOT NULL, " +
            "%s TEXT NOT NULL, " +
            "%s TEXT NOT NULL)",
            DatabaseContract.TABLE_NOTE,
            DatabaseContract.MovieColumns._ID,
            DatabaseContract.MovieColumns.IMAGE,
            DatabaseContract.MovieColumns.TITLE,
            DatabaseContract.MovieColumns.RATE,
            DatabaseContract.MovieColumns.RELEASE_DATE,
            DatabaseContract.MovieColumns.OVERVIEW);

    private static final String SQL_CREATE_TABLE_SHOW = String.format("CREATE TABLE %s " +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL UNIQUE, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL)",
            DatabaseContract.TABLE_NOTE_2,
            DatabaseContract.ShowColumns._ID,
            DatabaseContract.ShowColumns.IMAGE,
            DatabaseContract.ShowColumns.TITLE,
            DatabaseContract.ShowColumns.AIR_DATE,
            DatabaseContract.ShowColumns.AVERAGE_VOTE,
            DatabaseContract.ShowColumns.OVERVIEW);

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MOVIE);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SHOW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NOTE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NOTE_2);
        onCreate(sqLiteDatabase);
    }
}
