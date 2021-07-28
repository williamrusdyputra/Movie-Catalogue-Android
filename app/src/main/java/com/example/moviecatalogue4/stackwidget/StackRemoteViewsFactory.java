package com.example.moviecatalogue4.stackwidget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.database.FavoriteMovieHelper;
import com.example.moviecatalogue4.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final List<Bitmap> mWidgetItems = new ArrayList<>();
    private final Context mContext;

    StackRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        FavoriteMovieHelper helper = FavoriteMovieHelper.getInstance(mContext);
        helper.open();
        ArrayList<MovieModel> list = helper.getAllFavoriteMovies();

        for(MovieModel movie : list) {
            mWidgetItems.add(BitmapFactory.decodeByteArray(movie.getPoster(), 0, movie.getPoster().length));
        }
        helper.close();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems.get(i));
        Bundle extras = new Bundle();
        extras.putInt(ImageBannerWidget.EXTRA_ITEM, i);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
