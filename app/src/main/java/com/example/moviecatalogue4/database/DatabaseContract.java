package com.example.moviecatalogue4.database;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NOTE = "favorite_movie";
    static String TABLE_NOTE_2 = "favorite_show";
    public static final String AUTHORITY = "com.example.moviecatalogue4";
    private static final String SCHEME = "content";

    public static final class MovieColumns implements BaseColumns {
        public static String TITLE = "title";
        public static String IMAGE = "image";
        public static String RELEASE_DATE = "release_date";
        public static String RATE = "rating";
        public static String OVERVIEW = "overview";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NOTE)
                .build();
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static byte[] getBlob(Cursor cursor, String columnName) {
        return cursor.getBlob(cursor.getColumnIndex(columnName));
    }

    static final class ShowColumns implements BaseColumns {
        static String TITLE = "title";
        static String IMAGE = "image";
        static String AVERAGE_VOTE = "average_vote";
        static String AIR_DATE = "air_date";
        static String OVERVIEW = "overview";
    }
}
