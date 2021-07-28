package com.example.moviecatalogue4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.moviecatalogue4.model.TvShowModel;

import java.util.ArrayList;

import static com.example.moviecatalogue4.database.DatabaseContract.ShowColumns.AIR_DATE;
import static com.example.moviecatalogue4.database.DatabaseContract.ShowColumns.AVERAGE_VOTE;
import static com.example.moviecatalogue4.database.DatabaseContract.ShowColumns.IMAGE;
import static com.example.moviecatalogue4.database.DatabaseContract.ShowColumns.OVERVIEW;
import static com.example.moviecatalogue4.database.DatabaseContract.ShowColumns.TITLE;
import static com.example.moviecatalogue4.database.DatabaseContract.TABLE_NOTE_2;

public class FavoriteShowHelper {

    private static final String DATABASE_TABLE = TABLE_NOTE_2;
    private static DatabaseHelper showDatabaseHelper;
    private static FavoriteShowHelper INSTANCE;

    private static SQLiteDatabase database;

    private FavoriteShowHelper(Context context) {
        showDatabaseHelper = new DatabaseHelper(context);
    }

    public static FavoriteShowHelper getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if(INSTANCE == null) {
                    INSTANCE = new FavoriteShowHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = showDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        showDatabaseHelper.close();

        if(database.isOpen()) database.close();
    }

    public ArrayList<TvShowModel> getAllFavoriteShows() {
        ArrayList<TvShowModel> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                BaseColumns._ID + " ASC",
                null);
        cursor.moveToFirst();
        TvShowModel show;
        if (cursor.getCount() > 0) {
            do {
                show = new TvShowModel();
                show.setId(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)));
                show.setName(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                show.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)));
                show.setFirstAirDate(cursor.getString(cursor.getColumnIndexOrThrow(AIR_DATE)));
                show.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(AVERAGE_VOTE)));
                show.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW)));
                arrayList.add(show);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public long insertShow(TvShowModel show) {
        open();
        ContentValues args = new ContentValues();
        args.put(TITLE, show.getName());
        args.put(IMAGE, show.getPosterPath());
        args.put(AIR_DATE, show.getFirstAirDate());
        args.put(AVERAGE_VOTE, show.getVoteAverage());
        args.put(OVERVIEW, show.getOverview());
        return database.insert(DATABASE_TABLE, null, args);
    }

    public int deleteShow(int id) {
        return database.delete(TABLE_NOTE_2, BaseColumns._ID + " = '" + id + "'", null);
    }
}
