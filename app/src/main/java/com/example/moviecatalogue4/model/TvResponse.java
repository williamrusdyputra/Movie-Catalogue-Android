package com.example.moviecatalogue4.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvResponse {

    @SerializedName("results")
    private List<TvShowModel> results;

    public TvResponse(List<TvShowModel> results) {
        this.results = results;
    }

    public List<TvShowModel> getResults() {
        return results;
    }
}
