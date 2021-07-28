package com.example.moviecatalogue4.provider;

import com.example.moviecatalogue4.database.DatabaseContract;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.example.moviecatalogue4.database.FavoriteMovieHelper;
import com.example.moviecatalogue4.fragment.FavoriteMoviesFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MovieProvider extends ContentProvider {

    private static final int MOVIE = 1;
    private static final int MOVIE_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private FavoriteMovieHelper movieHelper;

    static {
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TABLE_NOTE, MOVIE);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TABLE_NOTE + "/#",MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        movieHelper = FavoriteMovieHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NotNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        movieHelper.open();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = movieHelper.queryProvider();
                break;
            case MOVIE_ID:
                cursor = movieHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(@NotNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues contentValues) {
        movieHelper.open();
        long added;
        if (sUriMatcher.match(uri) == MOVIE) {
            added = movieHelper.insertProvider(contentValues);
        } else {
            added = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(DatabaseContract.MovieColumns.CONTENT_URI, new FavoriteMoviesFragment.DataObserver(new Handler(), getContext()));
        return Uri.parse(DatabaseContract.MovieColumns.CONTENT_URI + "/" + added);
    }

    @Override
    public int delete(@NotNull Uri uri, String s, String[] strings) {
        movieHelper.open();
        int deleted;
        if (sUriMatcher.match(uri) == MOVIE_ID) {
            deleted = movieHelper.deleteProvider(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(DatabaseContract.MovieColumns.CONTENT_URI, new FavoriteMoviesFragment.DataObserver(new Handler(), getContext()));
        return deleted;
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        movieHelper.open();
        int updated;
        if (sUriMatcher.match(uri) == MOVIE_ID) {
            updated = movieHelper.updateProvider(uri.getLastPathSegment(), contentValues);
        } else {
            updated = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(DatabaseContract.MovieColumns.CONTENT_URI, new FavoriteMoviesFragment.DataObserver(new Handler(), getContext()));
        return updated;
    }
}
