package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;

public class OrganizerSplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_splash);

        new Handler().postDelayed(() -> {
            // Kiểm tra đã login chưa
            SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
            String token = prefs.getString("TOKEN", null);
            boolean isOrganizer = prefs.getBoolean("IS_ORGANIZER", false);

            Intent intent;
            if (token != null && isOrganizer) {
                // Đã login và là Organizer → vào Dashboard
                intent = new Intent(OrganizerSplashActivity.this, OrganizerDashboardActivity.class);
            } else {
                // Chưa login hoặc chưa là Organizer → vào Login
                intent = new Intent(OrganizerSplashActivity.this, OrganizerLoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}
