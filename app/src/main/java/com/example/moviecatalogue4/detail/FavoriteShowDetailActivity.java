package com.example.moviecatalogue4.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.FavoriteShowsAdapter;
import com.example.moviecatalogue4.database.FavoriteShowHelper;
import com.example.moviecatalogue4.fragment.FavoriteShowsFragment;
import com.example.moviecatalogue4.model.TvShowModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class FavoriteShowDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POS = "extra_pos";
    public static final int REQUEST_DELETE = 300;
    public static final int RESULT_DELETE = 301;
    private final String STATE_SHOW = "state_show";
    private FavoriteShowHelper favShowHelper;

    private int position;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton favoriteBtn;
    private ProgressBar progressBar;

    private TvShowModel show;

    private ImageView showPoster, backgroundImage;
    private AppCompatRatingBar ratingBar;
    private TextView showTitle, showAirDate, showOverview;
    private TextView airDateTv, overviewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_show_detail);

        Toolbar toolbar = findViewById(R.id.fav_show_toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        position = getIntent().getIntExtra(EXTRA_POS, 0);

        getViews();
        hideViews();

        collapsingToolbarLayout.setTitleEnabled(false);

        if(savedInstanceState == null) {
            showLoading(true);
            favShowHelper = FavoriteShowHelper.getInstance(getApplicationContext());
            favShowHelper.open();
            show = favShowHelper.getAllFavoriteShows().get(position);
            generateView(show);
            favShowHelper.close();
        } else {
            show = savedInstanceState.getParcelable(STATE_SHOW);
            if(show != null)
                generateView(show);
        }

        getSupportActionBar().setTitle(show.getName());

        showLoading(false);

        favShowHelper = FavoriteShowHelper.getInstance(getApplicationContext());
        favShowHelper.open();
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent, getTheme()));
                favoriteBtn.setImageTintList(getResources().getColorStateList(R.color.white, getTheme()));

                long result = favShowHelper.deleteShow(show.getId());
                if (result > 0) {
                    FavoriteShowsAdapter adapter = (FavoriteShowsAdapter) FavoriteShowsFragment.rvFavoriteShow.getAdapter();
                    if(adapter != null) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_POS, position);
                        setResult(RESULT_DELETE, intent);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(FavoriteShowDetailActivity.this, R.string.error_del, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateView(TvShowModel show){
        String poster = "https://image.tmdb.org/t/p/w500" + show.getPosterPath();

        favoriteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));
        favoriteBtn.setImageTintList(getResources().getColorStateList(R.color.colorAccent, getTheme()));

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
        collapsingToolbarLayout = findViewById(R.id.fav_show_collapsing_toolbar);
        progressBar = findViewById(R.id.progress_bar_fav_show_detail);
        favoriteBtn = findViewById(R.id.fab_fav_show);
        showPoster = findViewById(R.id.fav_show_poster);
        backgroundImage = findViewById(R.id.fav_show_image_background);
        ratingBar = findViewById(R.id.fav_rating_bar);
        showTitle = findViewById(R.id.fav_show_title);
        showAirDate = findViewById(R.id.fav_show_first_air_date);
        showOverview = findViewById(R.id.fav_show_overview);
        airDateTv = findViewById(R.id.textView6);
        overviewTv = findViewById(R.id.textView7);
    }

    private void hideViews() {
        showPoster.setVisibility(View.GONE);
        showTitle.setVisibility(View.GONE);
        showAirDate.setVisibility(View.GONE);
        showOverview.setVisibility(View.GONE);
        airDateTv.setVisibility(View.GONE);
        overviewTv.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SHOW, show);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
