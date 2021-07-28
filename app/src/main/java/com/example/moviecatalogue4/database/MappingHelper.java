package com.example.moviecatalogue4.database;

import android.database.Cursor;

import com.example.moviecatalogue4.model.MovieModel;

import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<MovieModel> mapCursorToArrayList(Cursor movieCursor) {
        ArrayList<MovieModel> movieList = new ArrayList<>();
        while (movieCursor.moveToNext()) {
            int id = movieCursor.getInt(movieCursor.getColumnIndexOrThrow(DatabaseContract.MovieColumns._ID));
            String title = movieCursor.getString(movieCursor.getColumnIndexOrThrow(DatabaseContract.MovieColumns.TITLE));
            byte []image = movieCursor.getBlob(movieCursor.getColumnIndexOrThrow(DatabaseContract.MovieColumns.IMAGE));
            String releaseDate = movieCursor.getString(movieCursor.getColumnIndexOrThrow(DatabaseContract.MovieColumns.RELEASE_DATE));
            boolean rate = movieCursor.getString(movieCursor.getColumnIndexOrThrow(DatabaseContract.MovieColumns.RATE)).equals("17+");
            String overview = movieCursor.getString(movieCursor.getColumnIndexOrThrow(DatabaseContract.MovieColumns.OVERVIEW));
            movieList.add(new MovieModel(id, title, image, releaseDate, rate, overview));
        }
        return movieList;
    }
}
