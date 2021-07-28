package com.example.moviecatalogue4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    private Switch dailySwitch, releaseSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dailySwitch = findViewById(R.id.daily_switch);
        releaseSwitch = findViewById(R.id.release_switch);

        init();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setSwitch() {
        boolean checked = false;
        Intent data = new Intent();

        SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.package_name), MODE_PRIVATE).edit();

        if(dailySwitch.isChecked()) {
            editor.putBoolean(getResources().getString(R.string.shared_key_daily), true);
            editor.apply();
            checked = true;
        } else {
            editor.putBoolean(getResources().getString(R.string.shared_key_daily), false);
            editor.apply();
        }

        data.putExtra("daily", checked);

        if(releaseSwitch.isChecked()) {
            editor.putBoolean(getResources().getString(R.string.shared_key_release), true);
            editor.apply();
            checked =  true;
        } else {
            editor.putBoolean(getResources().getString(R.string.shared_key_release), false);
            editor.apply();
            checked = false;
        }

        data.putExtra("release", checked);
        setResult(150, data);
    }

    private void init() {
        SharedPreferences sharedPrefs = getSharedPreferences(getResources().getString(R.string.package_name), MODE_PRIVATE);
        dailySwitch.setChecked(sharedPrefs.getBoolean(getResources().getString(R.string.shared_key_daily), true));
        releaseSwitch.setChecked(sharedPrefs.getBoolean(getResources().getString(R.string.shared_key_release), true));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setSwitch();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setSwitch();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
