package com.example.moviecatalogue4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.moviecatalogue4.model.MovieModel;

import java.util.ArrayList;

import static com.example.moviecatalogue4.database.DatabaseContract.MovieColumns.IMAGE;
import static com.example.moviecatalogue4.database.DatabaseContract.MovieColumns.OVERVIEW;
import static com.example.moviecatalogue4.database.DatabaseContract.MovieColumns.RATE;
import static com.example.moviecatalogue4.database.DatabaseContract.MovieColumns.RELEASE_DATE;
import static com.example.moviecatalogue4.database.DatabaseContract.MovieColumns.TITLE;
import static com.example.moviecatalogue4.database.DatabaseContract.TABLE_NOTE;

public class FavoriteMovieHelper {
    private static final String DATABASE_TABLE = TABLE_NOTE;
    private static DatabaseHelper databaseHelper;
    private static FavoriteMovieHelper INSTANCE;

    private static SQLiteDatabase database;

    private FavoriteMovieHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static FavoriteMovieHelper getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if(INSTANCE == null) {
                    INSTANCE = new FavoriteMovieHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();

        if(database.isOpen()) database.close();
    }

    public ArrayList<MovieModel> getAllFavoriteMovies() {
        ArrayList<MovieModel> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                BaseColumns._ID + " ASC",
                null);
        cursor.moveToFirst();
        MovieModel movie;
        if (cursor.getCount() > 0) {
            do {
                movie = new MovieModel();
                movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                movie.setPoster(cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(RELEASE_DATE)));
                movie.setAdult(cursor.getInt(cursor.getColumnIndexOrThrow(RATE)) > 0);
                movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW)));
                arrayList.add(movie);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(DATABASE_TABLE, null
                , BaseColumns._ID + " = ?"
                , new String[]{id}
                , null
                , null
                , null
                , null);
    }

    public Cursor queryProvider() {
        return database.query(DATABASE_TABLE
                , null
                , null
                , null
                , null
                , null
                , BaseColumns._ID + " ASC");
    }

    public long insertProvider(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int updateProvider(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, BaseColumns._ID + " = ?", new String[]{id});
    }

    public int deleteProvider(String id) {
        return database.delete(DATABASE_TABLE, BaseColumns._ID + " = ?", new String[]{id});
    }

    public long insertMovie(MovieModel movie) {
        ContentValues args = new ContentValues();
        args.put(TITLE, movie.getTitle());
        args.put(IMAGE, movie.getPoster());
        args.put(RELEASE_DATE, movie.getReleaseDate());
        args.put(RATE, movie.isAdult());
        args.put(OVERVIEW, movie.getOverview());
        return database.insert(DATABASE_TABLE, null, args);
    }

    public int deleteMovie(int id) {
        return database.delete(TABLE_NOTE, BaseColumns._ID + " = '" + id + "'", null);
    }
}
