package com.yihchou.eweather;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment weatherFragment;
    private Fragment musicFragment;
    private Fragment calendarFragment;
    private Fragment settingsFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        weatherFragment = new WeatherFragment();
        musicFragment = new MusicFragment();
        calendarFragment = new CalendarFragment();
        settingsFragment = new SettingsFragment();

        activeFragment = weatherFragment;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, settingsFragment, "settings").hide(settingsFragment);
        fragmentTransaction.add(R.id.fragment_container, calendarFragment, "calendar").hide(calendarFragment);
        fragmentTransaction.add(R.id.fragment_container, musicFragment, "music").hide(musicFragment);
        fragmentTransaction.add(R.id.fragment_container, weatherFragment, "weather").commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_weather:
                        selectedFragment = weatherFragment;
                        break;
                    case R.id.nav_music:
                        selectedFragment = musicFragment;
                        break;
                    case R.id.nav_calendar:
                        selectedFragment = calendarFragment;
                        break;
                    case R.id.nav_settings:
                        selectedFragment = settingsFragment;
                        break;
                }
                showFragment(selectedFragment);
                return true;
            }
        });

        // 设置默认选择的页面
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_weather);
        }
    }

    private void showFragment(Fragment fragment) {
        if (activeFragment != fragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(activeFragment).show(fragment).commit();
            activeFragment = fragment;
        }
    }
}
