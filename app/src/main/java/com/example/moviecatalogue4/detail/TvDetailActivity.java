package com.example.moviecatalogue4.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.FavoriteShowsAdapter;
import com.example.moviecatalogue4.database.FavoriteShowHelper;
import com.example.moviecatalogue4.fragment.FavoriteShowsFragment;
import com.example.moviecatalogue4.model.TvShowModel;
import com.example.moviecatalogue4.viewmodel.TvViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TvDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POS = "extra_pos";
    public static final String EXTRA_QUERY = "extra_query_show";
    public static final String EXTRA_SHOW = "extra_show";
    public static final int RESULT_ADD = 101;

    private int position;

    private TvShowModel show;

    private FavoriteShowHelper favShowHelper;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton favoriteBtn;
    private ProgressBar progressBar;

    private ImageView showPoster, backgroundImage;
    private AppCompatRatingBar ratingBar;
    private TextView showTitle, showAirDate, showOverview;
    private TextView airDateTv, overviewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        position = getIntent().getIntExtra(EXTRA_POS, 0);
        String query = getIntent().getStringExtra(EXTRA_QUERY);

        getViews();
        hideViews();

        collapsingToolbarLayout.setTitleEnabled(false);

        String language = Locale.getDefault().getLanguage();
        if(language.equals("in")) language = "id";

        final TvViewModel tvViewModel = ViewModelProviders.of(this).get(TvViewModel.class);
        tvViewModel.getShowList(this, language, query).observe(this, getShow);

        showLoading(true);

        favShowHelper = FavoriteShowHelper.getInstance(getApplicationContext());
        favShowHelper.open();
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));
                favoriteBtn.setImageTintList(getResources().getColorStateList(R.color.colorAccent, getTheme()));
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SHOW, show);

                long result = favShowHelper.insertShow(show);
                if (result > 0) {
                    show.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    FavoriteShowsAdapter adapter = (FavoriteShowsAdapter) FavoriteShowsFragment.rvFavoriteShow.getAdapter();
                    if(adapter != null) {
                        adapter.addFavoriteShow(show);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_add, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Observer<List<TvShowModel>> getShow = new Observer<List<TvShowModel>>() {
        @Override
        public void onChanged(List<TvShowModel> showList) {
            if (showList != null) {
                TvShowModel show = showList.get(position);
                Objects.requireNonNull(getSupportActionBar()).setTitle(show.getName());
                generateView(show);
                showLoading(false);
            }
        }
    };

    @SuppressLint("RestrictedApi")
    private void generateView(TvShowModel show){
        this.show = show;
        String poster = "https://image.tmdb.org/t/p/w500" + show.getPosterPath();

        Glide.with(this)
                .load(poster)
                .apply(new RequestOptions().override(500, 500))
                .into(showPoster);

        Glide.with(this)
                .load(poster)
                .apply(new RequestOptions().override(700, 700))
                .into(backgroundImage);

        showTitle.setText(show.getName());
        showAirDate.setText(show.getFirstAirDate());

        ratingBar.setRating(show.getVoteAverage().floatValue()/2);

        String overview = show.getOverview();
        if(overview.equals("")) {
            overview = getString(R.string.no_indonesian_translate);
        }
        showOverview.setText(overview);

        showPoster.setVisibility(View.VISIBLE);
        showTitle.setVisibility(View.VISIBLE);
        showAirDate.setVisibility(View.VISIBLE);
        showOverview.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        favoriteBtn.setVisibility(View.VISIBLE);
        airDateTv.setVisibility(View.VISIBLE);
        overviewTv.setVisibility(View.VISIBLE);
    }

    private void showLoading(Boolean state) {
        if(state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getViews() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_2);
        favoriteBtn = findViewById(R.id.fab2);
        progressBar = findViewById(R.id.progress_bar_detail_2);
        showPoster = findViewById(R.id.item_show_poster);
        backgroundImage = findViewById(R.id.image_background_2);
        ratingBar = findViewById(R.id.rating_bar);
        showTitle = findViewById(R.id.item_show_title);
        showAirDate = findViewById(R.id.item_show_first_air_date);
        showOverview = findViewById(R.id.item_show_overview);
        airDateTv = findViewById(R.id.textView4);
        overviewTv = findViewById(R.id.textView5);
    }

    @SuppressLint("RestrictedApi")
    private void hideViews() {
        showPoster.setVisibility(View.GONE);
        showTitle.setVisibility(View.GONE);
        showAirDate.setVisibility(View.GONE);
        showOverview.setVisibility(View.GONE);
        airDateTv.setVisibility(View.GONE);
        overviewTv.setVisibility(View.GONE);
        favoriteBtn.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        favShowHelper.close();
    }
}
