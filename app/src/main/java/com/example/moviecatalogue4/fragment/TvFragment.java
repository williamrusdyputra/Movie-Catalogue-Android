package com.example.moviecatalogue4.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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
import com.example.moviecatalogue4.adapter.TvViewAdapter;
import com.example.moviecatalogue4.detail.TvDetailActivity;
import com.example.moviecatalogue4.model.TvShowModel;
import com.example.moviecatalogue4.support.ItemClickSupport;
import com.example.moviecatalogue4.viewmodel.TvViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TvFragment extends Fragment {

    private ProgressBar progressBar;
    private TvViewAdapter tvViewAdapter;
    private String mSearchString;

    private static final String SEARCH_KEY = "search";

    private TvViewModel tvViewModel;
    private static String query = "";
    private RecyclerView rvList;

    public TvFragment() {
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
        return inflater.inflate(R.layout.fragment_tv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.show_progress_bar);

        tvViewAdapter = new TvViewAdapter(getActivity());
        tvViewAdapter.notifyDataSetChanged();

        rvList = view.findViewById(R.id.show_rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(tvViewAdapter);

        String language = Locale.getDefault().getLanguage();
        if(language.equals("in")) language = "id";

        query = "NORMAL";

        tvViewModel = ViewModelProviders.of(this).get(TvViewModel.class);
        tvViewModel.getShowList(getActivity(), language, query).observe(this, getShow);

        showLoading(true);

        ItemClickSupport.addTo(rvList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent detailIntent = new Intent(getActivity(), TvDetailActivity.class);
                detailIntent.putExtra(TvDetailActivity.EXTRA_POS, position);
                detailIntent.putExtra(TvDetailActivity.EXTRA_QUERY, query);
                startActivity(detailIntent);
            }
        });
    }

    private Observer<List<TvShowModel>> getShow = new Observer<List<TvShowModel>>() {
        @Override
        public void onChanged(List<TvShowModel> movieList) {
            if(movieList != null) {
                tvViewAdapter.setData(movieList);
                rvList.setVisibility(View.VISIBLE);
                showLoading(false);
            }
        }
    };

    private void showLoading(Boolean isLoading) {
        if(isLoading) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_KEY, mSearchString);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.show_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.show_action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search Shows...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length() > 2) {
                    showLoading(true);
                    query = s;
                    String language = Locale.getDefault().getLanguage();
                    if(language.equals("in")) language = "id";
                    rvList.setVisibility(View.INVISIBLE);
                    showLoading(true);
                    tvViewModel.getShowList(getActivity(), language, s).observe(Objects.requireNonNull(getActivity()), getShow);
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
                tvViewModel.getShowList(getActivity(), language, query).observe(Objects.requireNonNull(getActivity()), getShow);
                return true;
            }
        });
    }
}
