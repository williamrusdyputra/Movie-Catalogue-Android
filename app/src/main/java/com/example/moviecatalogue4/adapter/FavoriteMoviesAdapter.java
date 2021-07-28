package com.example.moviecatalogue4.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.model.MovieModel;

import java.util.ArrayList;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMoviesHolder> {

    private ArrayList<MovieModel> movieList = new ArrayList<>();
    private Context context;

    public FavoriteMoviesAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<MovieModel> getFavoriteMovieList() {
        return movieList;
    }

    public void setFavoriteMovieList(ArrayList<MovieModel> movieList) {
        if (movieList.size() > 0) {
            this.movieList.clear();
        }
        this.movieList.addAll(movieList);
        notifyDataSetChanged();
    }

    public void addFavoriteMovie(MovieModel movie) {
        this.movieList.add(movie);
        notifyItemInserted(movieList.size()-1);
    }

    public void removeItem(int position) {
        this.movieList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,movieList.size());
    }

    @NonNull
    @Override
    public FavoriteMoviesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_movie, parent, false);
        return new FavoriteMoviesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMoviesHolder holder, int position) {
        MovieModel movie = getFavoriteMovieList().get(position);

        holder.moviePoster.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        if(movie.getPoster() != null) {
            holder.moviePoster.setImageBitmap(BitmapFactory.decodeByteArray(movie.getPoster(), 0, movie.getPoster().length));
        }

        holder.moviePoster.setClipToOutline(true);
        holder.movieTitle.setText(movie.getTitle());
        holder.movieReleaseDate.setText(movie.getReleaseDate());

        String rating;
        if(movie.isAdult()) rating = "17+";
        else rating = "13+";

        holder.movieRating.setText(rating);

        String overview = movie.getOverview();
        if(overview.equals("")) {
            overview = context.getString(R.string.no_indonesian_translate);
        }
        holder.movieOverview.setText(overview);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class FavoriteMoviesHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView moviePoster;
        TextView movieTitle, movieReleaseDate, movieRating, movieOverview;

        FavoriteMoviesHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.movie_card_view);
            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieTitle = itemView.findViewById(R.id.movie_title);
            movieReleaseDate = itemView.findViewById(R.id.movie_release_date);
            movieRating = itemView.findViewById(R.id.movie_rating);
            movieOverview = itemView.findViewById(R.id.movie_overview);
        }
    }
}
