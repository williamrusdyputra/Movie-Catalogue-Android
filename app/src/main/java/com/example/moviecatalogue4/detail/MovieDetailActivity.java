package com.example.moviecatalogue4.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.moviecatalogue4.adapter.FavoriteMoviesAdapter;
import com.example.moviecatalogue4.database.FavoriteMovieHelper;
import com.example.moviecatalogue4.fragment.FavoriteMoviesFragment;
import com.example.moviecatalogue4.model.MovieModel;
import com.example.moviecatalogue4.viewmodel.MovieViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POS = "extra_pos";
    public static final String EXTRA_QUERY_MOVIE = "extra_query";
    public static final String EXTRA_MOVIE = "extra_movie";
    public static final int RESULT_ADD = 101;

    private int position;

    private FavoriteMovieHelper favMovieHelper;

    private MovieModel movie;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton favoriteBtn;
    private ProgressBar progressBar;

    private ImageView moviePoster, backgroundImage;
    private TextView movieTitle, movieRelease, movieRating, movieOverview;
    private TextView releaseTv, ratingTv, overviewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        position = getIntent().getIntExtra(EXTRA_POS, 0);
        String query = getIntent().getStringExtra(EXTRA_QUERY_MOVIE);

        getViews();
        hideViews();

        collapsingToolbarLayout.setTitleEnabled(false);

        String language = Locale.getDefault().getLanguage();
        if(language.equals("in")) language = "id";

        final MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getMovieList(this, language, query).observe(this, getMovie);

        showLoading(true);

        favMovieHelper = FavoriteMovieHelper.getInstance(getApplicationContext());
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));
                favoriteBtn.setImageTintList(getResources().getColorStateList(R.color.colorAccent, getTheme()));
                Intent intent = new Intent();
                intent.putExtra(EXTRA_MOVIE, movie);

                BitmapDrawable drawable = (BitmapDrawable) moviePoster.getDrawable();
                Bitmap poster = drawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                poster.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                movie.setPoster(baos.toByteArray());

                favMovieHelper.open();
                long result = favMovieHelper.insertMovie(movie);
                if (result > 0) {
                    movie.setId((int) result);
                    setResult(RESULT_ADD, intent);
                    FavoriteMoviesAdapter adapter = (FavoriteMoviesAdapter) FavoriteMoviesFragment.rvFavoriteMovie.getAdapter();

                    if(adapter != null) {
                        adapter.addFavoriteMovie(movie);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_add, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Observer<List<MovieModel>> getMovie = new Observer<List<MovieModel>>() {
        @Override
        public void onChanged(List<MovieModel> movieList) {
            if (movieList != null) {
                MovieModel movie = movieList.get(position);
                Objects.requireNonNull(getSupportActionBar()).setTitle(movie.getTitle());
                generateView(movie);
                showLoading(false);
            }
        }
    };

    @SuppressLint("RestrictedApi")
    private void generateView(MovieModel movie){
        this.movie = movie;
        String poster = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();

        Glide.with(this)
                .load(poster)
                .apply(new RequestOptions().override(500, 500))
                .into(moviePoster);

        Glide.with(this)
                .load(poster)
                .apply(new RequestOptions().override(700, 700))
                .into(backgroundImage);

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
        favoriteBtn.setVisibility(View.VISIBLE);
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
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        favoriteBtn = findViewById(R.id.fab);
        backgroundImage = findViewById(R.id.image_background);
        progressBar = findViewById(R.id.progress_bar_detail);
        moviePoster = findViewById(R.id.item_movie_poster);
        movieTitle = findViewById(R.id.item_movie_title);
        movieRelease = findViewById(R.id.item_movie_release_date);
        movieRating = findViewById(R.id.item_movie_rating);
        movieOverview = findViewById(R.id.item_movie_overview);
        releaseTv = findViewById(R.id.textView);
        ratingTv = findViewById(R.id.textView2);
        overviewTv = findViewById(R.id.textView3);
    }

    @SuppressLint("RestrictedApi")
    private void hideViews() {
        moviePoster.setVisibility(View.GONE);
        movieTitle.setVisibility(View.GONE);
        movieRelease.setVisibility(View.GONE);
        movieRating.setVisibility(View.GONE);
        movieOverview.setVisibility(View.GONE);
        releaseTv.setVisibility(View.GONE);
        ratingTv.setVisibility(View.GONE);
        favoriteBtn.setVisibility(View.GONE);
        overviewTv.setVisibility(View.GONE);
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
        favMovieHelper.close();
    }
}
