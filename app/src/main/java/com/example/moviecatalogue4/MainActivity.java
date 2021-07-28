package com.example.moviecatalogue4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.moviecatalogue4.adapter.CustomViewPager;
import com.example.moviecatalogue4.notification.AlarmReceiver;
import com.example.moviecatalogue4.adapter.ViewPagerAdapter;
import com.example.moviecatalogue4.fragment.FavoriteFragment;
import com.example.moviecatalogue4.fragment.MoviesFragment;
import com.example.moviecatalogue4.fragment.TvFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private CustomViewPager viewPager;
    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Movie Catalogue");

        viewPager = findViewById(R.id.viewpager);
        viewPager.disableScroll(true);
        setupViewPager(viewPager);

        final BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        alarmReceiver = new AlarmReceiver();
        SharedPreferences sharedPrefs = getSharedPreferences(getResources().getString(R.string.package_name), MODE_PRIVATE);

        if(sharedPrefs.getBoolean(getResources().getString(R.string.shared_key_daily), true)) {
            alarmReceiver.setRepeatingAlarm(this, this.getResources().getString(R.string.daily_time), this.getResources().getString(R.string.notification_message_daily), 101);
        }
        if(sharedPrefs.getBoolean(getResources().getString(R.string.shared_key_release), true)) {
             alarmReceiver.setRepeatingAlarm(this, this.getResources().getString(R.string.release_time), "RELEASE", 102);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_movies:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_tv_shows:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_favorites:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Fragment moviesFragment = new MoviesFragment();
        Fragment tvFragment = new TvFragment();
        final Fragment favoriteFragment = new FavoriteFragment();

        viewPagerAdapter.addFragment(moviesFragment);
        viewPagerAdapter.addFragment(tvFragment);
        viewPagerAdapter.addFragment(favoriteFragment);
        viewPager.setAdapter(viewPagerAdapter);

        int limit = (viewPagerAdapter.getCount() > 1 ? viewPagerAdapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == R.id.action_change_settings){
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        } else if(item.getItemId() == R.id.action_set_alarm) {
            Intent mIntent = new Intent(this, SettingActivity.class);
            startActivityForResult(mIntent, 150);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 150 && resultCode == 150) {
            if (data != null) {
                setAlarm(data.getBooleanExtra("daily", true),
                        data.getBooleanExtra("release", true));
            }
        }
    }

    private void setAlarm(boolean daily, boolean release) {
        alarmReceiver = new AlarmReceiver();

        if(daily) {
            alarmReceiver.setRepeatingAlarm(this, this.getResources().getString(R.string.daily_time), this.getResources().getString(R.string.notification_message_daily), 101);
        } else {
            alarmReceiver.cancelAlarm(this, "daily");
        }

        if(release) {
            alarmReceiver.setRepeatingAlarm(this, this.getResources().getString(R.string.release_time), "RELEASE", 102);
        } else {
            alarmReceiver.cancelAlarm(this, "release");
        }
    }
}
