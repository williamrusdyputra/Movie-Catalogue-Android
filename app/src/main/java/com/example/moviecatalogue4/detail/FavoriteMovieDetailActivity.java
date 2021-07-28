package com.example.moviecatalogue4.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.FavoriteMoviesAdapter;
import com.example.moviecatalogue4.database.FavoriteMovieHelper;
import com.example.moviecatalogue4.fragment.FavoriteMoviesFragment;
import com.example.moviecatalogue4.model.MovieModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class FavoriteMovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POS = "extra_pos";
    public static final int REQUEST_DELETE = 300;
    public static final int RESULT_DELETE = 301;
    private final String STATE_MOVIE = "state_movie";
    private FavoriteMovieHelper favMovieHelper;

    private int position;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton favoriteBtn;
    private ProgressBar progressBar;

    private MovieModel movie;

    private ImageView moviePoster, backgroundImage;
    private TextView movieTitle, movieRelease, movieRating, movieOverview;
    private TextView releaseTv, ratingTv, overviewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movie_detail);

        Toolbar toolbar = findViewById(R.id.fav_toolbar);
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
            favMovieHelper = FavoriteMovieHelper.getInstance(getApplicationContext());
            favMovieHelper.open();
            movie = favMovieHelper.getAllFavoriteMovies().get(position);
            generateView(movie);
            favMovieHelper.close();
        } else {
            movie = savedInstanceState.getParcelable(STATE_MOVIE);
            if(movie != null)
                generateView(movie);
        }

        getSupportActionBar().setTitle(movie.getTitle());

        showLoading(false);

        favMovieHelper = FavoriteMovieHelper.getInstance(getApplicationContext());
        favMovieHelper.open();
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent, getTheme()));
                favoriteBtn.setImageTintList(getResources().getColorStateList(R.color.white, getTheme()));

                long result = favMovieHelper.deleteMovie(movie.getId());
                if (result > 0) {
                    FavoriteMoviesAdapter adapter = (FavoriteMoviesAdapter) FavoriteMoviesFragment.rvFavoriteMovie.getAdapter();

                    if(adapter != null) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_POS, position);
                        setResult(RESULT_DELETE, intent);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(FavoriteMovieDetailActivity.this, R.string.error_del, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateView(MovieModel movie){
        favoriteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));
        favoriteBtn.setImageTintList(getResources().getColorStateList(R.color.colorAccent, getTheme()));

        moviePoster.setImageBitmap(BitmapFactory.decodeByteArray(movie.getPoster(), 0, movie.getPoster().length));

        backgroundImage.setImageBitmap(BitmapFactory.decodeByteArray(movie.getPoster(), 0, movie.getPoster().length));

        movieTitle.setText(movie.getTitle());
        movieRelease.setText(movie.getReleaseDate());

        String rating;
        if(movie.isAdult()) rating = "17+";
        else rating = "13+";
        movieRating.setText(rating);

        String overview = movie.getOverview();
        if(overview.equals("")) {
            overview = getString(R.string.no_indonesian_translate);
        }
        movieOverview.setText(overview);

        moviePoster.setVisibility(View.VISIBLE);
        movieTitle.setVisibility(View.VISIBLE);
        movieRelease.setVisibility(View.VISIBLE);
        movieRating.setVisibility(View.VISIBLE);
        movieOverview.setVisibility(View.VISIBLE);
        releaseTv.setVisibility(View.VISIBLE);
        ratingTv.setVisibility(View.VISIBLE);
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
        collapsingToolbarLayout = findViewById(R.id.fav_collapsing_toolbar);
        favoriteBtn = findViewById(R.id.fab_fav);
        backgroundImage = findViewById(R.id.fav_image_background);
        progressBar = findViewById(R.id.progress_bar_fav_detail);
        moviePoster = findViewById(R.id.fav_movie_poster);
        movieTitle = findViewById(R.id.fav_movie_title);
        movieRelease = findViewById(R.id.fav_movie_release_date);
        movieRating = findViewById(R.id.fav_movie_rating);
        movieOverview = findViewById(R.id.fav_movie_overview);
        releaseTv = findViewById(R.id.textView);
        ratingTv = findViewById(R.id.textView2);
        overviewTv = findViewById(R.id.textView3);
    }

    private void hideViews() {
        moviePoster.setVisibility(View.GONE);
        movieTitle.setVisibility(View.GONE);
        movieRelease.setVisibility(View.GONE);
        movieRating.setVisibility(View.GONE);
        movieOverview.setVisibility(View.GONE);
        releaseTv.setVisibility(View.GONE);
        ratingTv.setVisibility(View.GONE);
        overviewTv.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MOVIE, movie);
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
