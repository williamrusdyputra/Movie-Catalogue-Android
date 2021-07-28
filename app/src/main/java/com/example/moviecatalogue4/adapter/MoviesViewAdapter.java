package com.example.moviecatalogue4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class MoviesViewAdapter extends RecyclerView.Adapter<MoviesViewAdapter.MoviesViewHolder> {
    private Context context;
    private List<MovieModel> movieList = new ArrayList<>();

    public void setData(List<MovieModel> movieList) {
        this.movieList.clear();
        this.movieList.addAll(movieList);
        notifyDataSetChanged();
    }

    public MoviesViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_movie, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        MovieModel movie = movieList.get(position);

        holder.moviePoster.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        String poster = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(context)
                .load(poster)
                .apply(new RequestOptions().override(500, 500))
                .into(holder.moviePoster);

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
        return (movieList == null) ? 0 : movieList.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView moviePoster;
        TextView movieTitle, movieReleaseDate, movieRating, movieOverview;

        MoviesViewHolder(@NonNull View itemView) {
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
