package com.example.moviecatalogue4.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.moviecatalogue4.callback.LoadFavoriteMoviesCallback;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.FavoriteMoviesAdapter;
import com.example.moviecatalogue4.database.FavoriteMovieHelper;
import com.example.moviecatalogue4.detail.FavoriteMovieDetailActivity;
import com.example.moviecatalogue4.model.MovieModel;
import com.example.moviecatalogue4.support.ItemClickSupport;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.moviecatalogue4.database.MappingHelper.mapCursorToArrayList;
import static com.example.moviecatalogue4.database.DatabaseContract.MovieColumns.CONTENT_URI;

public class FavoriteMoviesFragment extends Fragment implements LoadFavoriteMoviesCallback {

    private ProgressBar progressBar;

    private static final String EXTRA_STATE = "extra_state";

    public static RecyclerView rvFavoriteMovie;
    private FavoriteMoviesAdapter adapter;
    private FavoriteMovieHelper helper;

    private LinearLayout emptyView;

    public FavoriteMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyView = view.findViewById(R.id.empty_view);

        rvFavoriteMovie = view.findViewById(R.id.rv_favorite_movie_list);
        rvFavoriteMovie.setLayoutManager(new LinearLayoutManager(getActivity()));

        helper = FavoriteMovieHelper.getInstance(getContext());
        helper.open();

        progressBar = view.findViewById(R.id.progress_bar_favorite_movie);
        progressBar.setVisibility(View.GONE);

        adapter = new FavoriteMoviesAdapter(getActivity());
        adapter.notifyDataSetChanged();
        rvFavoriteMovie.setAdapter(adapter);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, getContext());
        Objects.requireNonNull(getContext()).getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);

        if(savedInstanceState == null) {
            new LoadMoviesAsync(getContext(), this).execute();
        } else {
            ArrayList<MovieModel> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if(list != null) {
                adapter.setFavoriteMovieList(list);
            }
        }

        ItemClickSupport.addTo(rvFavoriteMovie).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent detailIntent = new Intent(getActivity(), FavoriteMovieDetailActivity.class);
                detailIntent.putExtra(FavoriteMovieDetailActivity.EXTRA_POS, position);
                startActivityForResult(detailIntent, FavoriteMovieDetailActivity.REQUEST_DELETE);
            }
        });
    }

    public static class DataObserver extends ContentObserver {
        final Context context;
        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadMoviesAsync(context, (LoadFavoriteMoviesCallback) context).execute();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == FavoriteMovieDetailActivity.REQUEST_DELETE) {
                if (resultCode == FavoriteMovieDetailActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(FavoriteMovieDetailActivity.EXTRA_POS, 0);
                    adapter.removeItem(position);

                    if(adapter.getFavoriteMovieList().size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getFavoriteMovieList());
    }

    private static class LoadMoviesAsync extends AsyncTask<Void, Void, Cursor> {

        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoriteMoviesCallback> weakCallback;

        private LoadMoviesAsync(Context context, LoadFavoriteMoviesCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Context context = weakContext.get();
            return context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor movies) {
            super.onPostExecute(movies);
            weakCallback.get().postExecute(movies);
        }
    }

    @Override
    public void preExecute() {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(Cursor movies) {
        progressBar.setVisibility(View.INVISIBLE);

        ArrayList<MovieModel> movieList = mapCursorToArrayList(movies);
        if (movieList.size() > 0) {
            emptyView.setVisibility(View.INVISIBLE);
            adapter.setFavoriteMovieList(movieList);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            adapter.setFavoriteMovieList(new ArrayList<MovieModel>());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }
}
