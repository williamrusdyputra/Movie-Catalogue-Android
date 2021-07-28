package com.example.moviecatalogue4.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.moviecatalogue4.callback.LoadFavoriteShowsCallback;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.FavoriteShowsAdapter;
import com.example.moviecatalogue4.database.FavoriteShowHelper;
import com.example.moviecatalogue4.detail.FavoriteShowDetailActivity;
import com.example.moviecatalogue4.model.TvShowModel;
import com.example.moviecatalogue4.support.ItemClickSupport;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class FavoriteShowsFragment extends Fragment implements LoadFavoriteShowsCallback {

    private ProgressBar progressBar;

    private static final String EXTRA_STATE = "extra_state";

    public static RecyclerView rvFavoriteShow;
    private FavoriteShowsAdapter adapter;
    private FavoriteShowHelper helper;

    private LinearLayout emptyView;

    public FavoriteShowsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_shows, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyView = view.findViewById(R.id.empty_show_view);

        rvFavoriteShow = view.findViewById(R.id.rv_favorite_show_list);
        rvFavoriteShow.setLayoutManager(new LinearLayoutManager(getActivity()));

        helper = FavoriteShowHelper.getInstance(getContext());
        helper.open();

        progressBar = view.findViewById(R.id.progress_bar_favorite_show);
        progressBar.setVisibility(View.GONE);

        adapter = new FavoriteShowsAdapter(getActivity());
        adapter.notifyDataSetChanged();
        rvFavoriteShow.setAdapter(adapter);

        if(savedInstanceState == null) {
            new FavoriteShowsFragment.LoadShowsAsync(helper, this).execute();
        } else {
            ArrayList<TvShowModel> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if(list != null) {
                adapter.setFavoriteShowList(list);
            }
        }

        ItemClickSupport.addTo(rvFavoriteShow).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent detailIntent = new Intent(getActivity(), FavoriteShowDetailActivity.class);
                detailIntent.putExtra(FavoriteShowDetailActivity.EXTRA_POS, position);
                startActivityForResult(detailIntent, FavoriteShowDetailActivity.REQUEST_DELETE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == FavoriteShowDetailActivity.REQUEST_DELETE) {
                if (resultCode == FavoriteShowDetailActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(FavoriteShowDetailActivity.EXTRA_POS, 0);
                    adapter.removeItem(position);

                    if(adapter.getFavoriteShowList().size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getFavoriteShowList());
    }

    private static class LoadShowsAsync extends AsyncTask<Void, Void, ArrayList<TvShowModel>> {

        private final WeakReference<FavoriteShowHelper> weakShowHelper;
        private final WeakReference<LoadFavoriteShowsCallback> weakCallback;

        private LoadShowsAsync(FavoriteShowHelper helper, LoadFavoriteShowsCallback callback) {
            weakShowHelper = new WeakReference<>(helper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<TvShowModel> doInBackground(Void... voids) {
            return weakShowHelper.get().getAllFavoriteShows();
        }

        @Override
        protected void onPostExecute(ArrayList<TvShowModel> shows) {
            super.onPostExecute(shows);
            weakCallback.get().postExecute(shows);
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
    public void postExecute(ArrayList<TvShowModel> shows) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.setFavoriteShowList(shows);

        if(shows.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }
}
