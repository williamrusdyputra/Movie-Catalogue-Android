package com.example.moviecatalogue4.callback;

import android.database.Cursor;

public interface LoadFavoriteMoviesCallback {
    void preExecute();
    void postExecute(Cursor movies);
}
