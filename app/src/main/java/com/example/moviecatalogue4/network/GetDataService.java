package com.example.moviecatalogue4.network;

import com.example.moviecatalogue4.model.MovieResponse;
import com.example.moviecatalogue4.model.TvResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("movie/upcoming")
    Call<MovieResponse> getMovies(@Query("api_key") String API_KEY, @Query("language") String lang);

    @GET("discover/tv")
    Call<TvResponse> getShows(@Query("api_key") String API_KEY, @Query("language") String lang);

    @GET("search/movie")
    Call<MovieResponse> getSearchedMovies(@Query("api_key") String API_KEY, @Query("language") String lang, @Query("query") String name);

    @GET("search/tv")
    Call<TvResponse> getSearchedShows(@Query("api_key") String API_KEY, @Query("language") String lang, @Query("query") String name);

    @GET("discover/movie")
    Call<MovieResponse> getReleasedTodayMovies(@Query("api_key") String API_KEY, @Query("primary_release_date.gte") String primaryDateGTE, @Query("primary_release_date.lte") String primaryDateLTE);
}
