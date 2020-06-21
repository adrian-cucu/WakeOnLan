package com.wakeonlan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SwitchCompat mNightModeSwitch;

    private boolean mThemeDark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        mThemeDark = SharedPrefsSettings.isThemeDark();
        setTheme(mThemeDark ? R.style.AppThemeDark : R.style.AppThemeLight);

        Log.d(MainActivity.TAG, "SettingsActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.toolbar);
//        mToolbar.getContext().setTheme(mThemeDark ?
//                R.style.ActionBarStyleDark : R.style.ActionBarStyleLight);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.activity_settings_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNightModeSwitch = (SwitchCompat) findViewById(R.id.switch_night_mode);
        mNightModeSwitch.setChecked(mThemeDark);
        mNightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPrefsSettings.setTheme(isChecked);

                Toast.makeText(
                        getApplicationContext(),
                        "switch is: " + (isChecked ? "ON" : "OFF"),
                        Toast.LENGTH_SHORT).show();

//                recreate();
                TaskStackBuilder.create(getApplicationContext())
                        .addNextIntent(new Intent(getApplicationContext(), MainActivity.class))
                        .addNextIntent(new Intent(getApplicationContext(), SettingsActivity.class))
                        .startActivities();
            }
        });

        SwitchCompat switchTest2 = (SwitchCompat) findViewById(R.id.switch_test_2);
        switchTest2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(MainActivity.TAG, isChecked ? "switch checked" : "switch not checked");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
//        return super.onSupportNavigateUp();
    }
}
