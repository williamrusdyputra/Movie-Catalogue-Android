package com.example.moviecatalogue4.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviecatalogue4.R;
import com.example.moviecatalogue4.adapter.FavoriteViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

public class FavoriteFragment extends Fragment {

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        final ViewPager viewPager = view.findViewById(R.id.fav_viewpager);

        final FavoriteViewPagerAdapter pagerAdapter = new FavoriteViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getActivity());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager
                (viewPager);
    }
}
