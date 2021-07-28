package com.example.moviecatalogue4.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviecatalogue4.BuildConfig;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.model.MovieModel;
import com.example.moviecatalogue4.model.MovieResponse;
import com.example.moviecatalogue4.network.GetDataService;
import com.example.moviecatalogue4.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.API_KEY;
    private static String currentLanguage = "";
    private static String currentQuery = "";
    private MutableLiveData<List<MovieModel>> movieList;

    private void setMovies(final Context context, String language, String query) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<MovieResponse> call;
        if(!query.equals("NORMAL")) {
            call = service.getSearchedMovies(API_KEY, language, query);
        } else {
            call = service.getMovies(API_KEY, language);
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.body() != null) {
                    movieList.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable throwable) {
                Toast.makeText(context, context.getString(R.string.error_load), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<MovieModel>> getMovieList(Context context, String language, String query) {
        if(movieList == null || !currentLanguage.equals(language) || !currentQuery.equals(query)) {
            movieList = new MutableLiveData<>();
            currentLanguage = language;
            currentQuery = query;
            setMovies(context, language, query);
        }
        return movieList;
    }
}
