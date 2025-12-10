package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.DashboardEvent;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.DashboardResponse;
import com.example.mobileapp.network.RetrofitClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerProfileActivity extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private ImageView btnBack, btnSettings, btnChangeAvatar;
    private TextView tvOrganizerName, tvEmail, tvVerifiedBadge;
    private TextView tvTotalEvents, tvTotalTickets, tvTotalRevenue;
    private TextView tvTaxCode, tvAddress, tvContactEmail, tvContactPhone;
    private LinearLayout btnEditProfile, btnMyEvents, btnBankAccount, btnSupport;
    private Button btnLogout;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_profile);

        initViews();
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        setupClickListeners();
        loadOrganizerProfile();
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);

        tvOrganizerName = findViewById(R.id.tvOrganizerName);
        tvEmail = findViewById(R.id.tvEmail);
        tvVerifiedBadge = findViewById(R.id.tvVerifiedBadge);

        tvTotalEvents = findViewById(R.id.tvTotalEvents);
        tvTotalTickets = findViewById(R.id.tvTotalTickets);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        tvTaxCode = findViewById(R.id.tvTaxCode);
        tvAddress = findViewById(R.id.tvAddress);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        tvContactPhone = findViewById(R.id.tvContactPhone);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnMyEvents = findViewById(R.id.btnMyEvents);
        btnBankAccount = findViewById(R.id.btnBankAccount);
        btnSupport = findViewById(R.id.btnSupport);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
            // TODO: Open settings activity
        });

        btnChangeAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "Thay đổi ảnh đại diện", Toast.LENGTH_SHORT).show();
            // TODO: Open image picker
        });

        btnEditProfile.setOnClickListener(v -> {
            // Mở màn hình chỉnh sửa hồ sơ
            // Intent intent = new Intent(this, OrganizerEditProfileActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
        });

        btnMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerDashboardActivity.class);
            startActivity(intent);
        });

        btnBankAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Quản lý tài khoản ngân hàng", Toast.LENGTH_SHORT).show();
            // TODO: Open bank account management
        });

        btnSupport.setOnClickListener(v -> {
            Toast.makeText(this, "Hỗ trợ", Toast.LENGTH_SHORT).show();
            // TODO: Open support activity
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadOrganizerProfile() {
        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);
        String userName = prefs.getString("USER_NAME", "Organizer");

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        // Set basic info from SharedPreferences
        tvOrganizerName.setText(userName);

        // Load dashboard data for stats
        apiService.getDashboard("Bearer " + token).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    List<DashboardEvent> events = response.body().data;

                    // Calculate totals
                    int totalEvents = events != null ? events.size() : 0;
                    int totalTickets = 0;
                    double totalRevenue = 0;

                    if (events != null) {
                        for (DashboardEvent event : events) {
                            totalTickets += event.total_tickets_sold;
                            totalRevenue += event.total_revenue;
                        }
                    }

                    // Update UI
                    tvTotalEvents.setText(String.valueOf(totalEvents));
                    tvTotalTickets.setText(String.valueOf(totalTickets));
                    tvTotalRevenue.setText(String.format("%,.0fđ", totalRevenue));
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(OrganizerProfileActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Load organizer profile details (tax_code, address, etc.) from API
        // For now, display placeholder
        tvTaxCode.setText("Chưa cập nhật");
        tvAddress.setText("Chưa cập nhật");
        tvContactEmail.setText("Chưa cập nhật");
        tvContactPhone.setText("Chưa cập nhật");
    }

    private void logout() {
        // Clear all auth data
        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // Navigate to Login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}