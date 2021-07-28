package com.example.moviecatalogue4.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<MovieModel> results;

    public MovieResponse(List<MovieModel> results) {
        this.results = results;
    }

    public List<MovieModel> getResults() {
        return results;
    }
}
