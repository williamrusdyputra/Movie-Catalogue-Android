package com.example.moviecatalogue4.callback;

import com.example.moviecatalogue4.model.TvShowModel;

import java.util.ArrayList;

public interface LoadFavoriteShowsCallback {
    void preExecute();
    void postExecute(ArrayList<TvShowModel> shows);
}
