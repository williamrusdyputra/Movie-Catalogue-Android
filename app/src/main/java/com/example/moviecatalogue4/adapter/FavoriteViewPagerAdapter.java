package com.example.moviecatalogue4.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.fragment.FavoriteMoviesFragment;
import com.example.moviecatalogue4.fragment.FavoriteShowsFragment;

public class FavoriteViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private int numOfTabs;

    public FavoriteViewPagerAdapter(FragmentManager fm, int numOfTabs, Context context) {
        super(fm);
        this.numOfTabs =
                numOfTabs;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new FavoriteMoviesFragment();
            case 1:
                return new FavoriteShowsFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0: return context.getString(R.string.favorite_movies);
            case 1: return context.getString(R.string.favorite_shows);
        }
        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
