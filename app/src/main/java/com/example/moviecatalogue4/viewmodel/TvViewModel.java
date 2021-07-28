package com.example.moviecatalogue4.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviecatalogue4.BuildConfig;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.model.TvResponse;
import com.example.moviecatalogue4.model.TvShowModel;
import com.example.moviecatalogue4.network.GetDataService;
import com.example.moviecatalogue4.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.API_KEY;
    private static String currentLanguage = "";
    private static String currentQuery = "";
    private MutableLiveData<List<TvShowModel>> showList;

    public LiveData<List<TvShowModel>> getShowList(Context context, String language, String query) {
        if(showList == null || !currentLanguage.equals(language) || !currentQuery.equals(query)) {
            showList = new MutableLiveData<>();
            currentLanguage = language;
            currentQuery = query;
            setShowList(language, context, query);
        }
        return showList;
    }

    private void setShowList(String language, Context context, String query) {
        generateDataFromApi(language, context, query);
    }

    private void generateDataFromApi(String language, final Context context, String query) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<TvResponse> call;
        if(!query.equals("NORMAL")) {
            call = service.getSearchedShows(API_KEY, language, query);
        } else {
            call = service.getShows(API_KEY, language);
        }

        call.enqueue(new Callback<TvResponse>() {
            @Override
            public void onResponse(@NonNull Call<TvResponse> call, @NonNull Response<TvResponse> response) {
                if(!response.isSuccessful()) {
                    Log.d("ERROR", "CODE: " + response.code());
                }

                if(response.body() != null) {
                    showList.setValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvResponse> call, @NonNull Throwable throwable) {
                Toast.makeText(context, context.getString(R.string.error_load), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
