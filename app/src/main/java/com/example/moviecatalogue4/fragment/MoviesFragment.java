package com.example.moviecatalogue4.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.MoviesViewAdapter;
import com.example.moviecatalogue4.detail.MovieDetailActivity;
import com.example.moviecatalogue4.model.MovieModel;
import com.example.moviecatalogue4.support.ItemClickSupport;
import com.example.moviecatalogue4.viewmodel.MovieViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class MoviesFragment extends Fragment {

    private ProgressBar progressBar;
    private MoviesViewAdapter moviesViewAdapter;
    private String mSearchString;
    private ShimmerLayout shimmerLayout;

    private static final String SEARCH_KEY = "search";

    private MovieViewModel movieViewModel;
    private static String query = "";
    private RecyclerView rvList;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_KEY);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.movie_progress_bar);
        progressBar.setVisibility(View.GONE);

        moviesViewAdapter = new MoviesViewAdapter(getActivity());
        moviesViewAdapter.notifyDataSetChanged();

        shimmerLayout = view.findViewById(R.id.shimmer_layout);

        rvList = view.findViewById(R.id.movie_rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(moviesViewAdapter);

        String language = Locale.getDefault().getLanguage();
        if(language.equals("in")) language = "id";

        query = "NORMAL";

        shimmerLayout.startShimmerAnimation();

        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getMovieList(getActivity(), language, query).observe(this, getMovie);

        ItemClickSupport.addTo(rvList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailActivity.EXTRA_POS, position);
                detailIntent.putExtra(MovieDetailActivity.EXTRA_QUERY_MOVIE, query);
                startActivity(detailIntent);
            }
        });
    }

    private Observer<List<MovieModel>> getMovie = new Observer<List<MovieModel>>() {
        @Override
        public void onChanged(List<MovieModel> movieList) {
            if(movieList != null) {
                moviesViewAdapter.setData(movieList);
                rvList.setVisibility(View.VISIBLE);
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_KEY, mSearchString);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull final Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search Movies...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length() > 2) {
                    query = s;
                    String language = Locale.getDefault().getLanguage();
                    if(language.equals("in")) language = "id";
                    rvList.setVisibility(View.INVISIBLE);
                    shimmerLayout.setVisibility(View.VISIBLE);
                    shimmerLayout.startShimmerAnimation();
                    movieViewModel.getMovieList(getActivity(), language, s).observe(Objects.requireNonNull(getActivity()), getMovie);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                menu.findItem(R.id.action_change_settings).setVisible(false);
                menu.findItem(R.id.action_set_alarm).setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                menu.findItem(R.id.action_change_settings).setVisible(true);
                menu.findItem(R.id.action_set_alarm).setVisible(true);
                if(query.equals("NORMAL")) return true;
                query = "NORMAL";
                String language = Locale.getDefault().getLanguage();
                if(language.equals("in")) language = "id";
                movieViewModel.getMovieList(getActivity(), language, query).observe(Objects.requireNonNull(getActivity()), getMovie);
                return true;
            }
        });
    }
}
