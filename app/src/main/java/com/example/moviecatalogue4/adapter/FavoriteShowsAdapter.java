package com.example.moviecatalogue4.adapter;

import android.app.Activity;
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
import com.example.moviecatalogue4.model.TvShowModel;

import java.util.ArrayList;

public class FavoriteShowsAdapter extends RecyclerView.Adapter<FavoriteShowsAdapter.FavoriteShowsHolder> {

    private ArrayList<TvShowModel> showList = new ArrayList<>();
    private Activity activity;

    public FavoriteShowsAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<TvShowModel> getFavoriteShowList() {
        return showList;
    }

    public void setFavoriteShowList(ArrayList<TvShowModel> showList) {
        if (showList.size() > 0) {
            this.showList.clear();
        }
        this.showList.addAll(showList);
        notifyDataSetChanged();
    }

    public void addFavoriteShow(TvShowModel show) {
        this.showList.add(show);
        notifyItemInserted(showList.size()-1);
    }

    public void removeItem(int position) {
        this.showList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, showList.size());
    }


    @NonNull
    @Override
    public FavoriteShowsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_tv, parent, false);
        return new FavoriteShowsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteShowsHolder holder, int position) {
        TvShowModel show = showList.get(position);

        holder.showPoster.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_scale_animation));

        String poster = "https://image.tmdb.org/t/p/w500" + show.getPosterPath();
        Glide.with(activity)
                .load(poster)
                .apply(new RequestOptions().override(500, 500))
                .into(holder.showPoster);

        holder.showPoster.setClipToOutline(true);
        holder.title.setText(show.getName());
        holder.firstAirDate.setText(show.getFirstAirDate());

        String overview = show.getOverview();
        if(overview.equals("")) {
            overview = activity.getString(R.string.no_indonesian_translate);
        }
        holder.overview.setText(overview);
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    class FavoriteShowsHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView showPoster;
        TextView title, firstAirDate, overview;

        FavoriteShowsHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.show_card_view);
            showPoster = itemView.findViewById(R.id.show_poster);
            title = itemView.findViewById(R.id.show_title);
            firstAirDate = itemView.findViewById(R.id.show_first_air_date);
            overview = itemView.findViewById(R.id.show_overview);
        }
    }
}
