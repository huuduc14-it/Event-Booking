package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Ticket;
import com.example.mobileapp.data.model.User;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.ProfileResponse;
import com.example.mobileapp.network.RetrofitClient;
//import com.example.mobileapp.network.TicketResponse;
import com.example.mobileapp.ui.adapter.TicketAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgAvatar, imgCamera;
    TextView tvUserName, tvEmail, tvPhone;
    RecyclerView rvTickets;
    Toolbar toolbar;
    Button btnLogout, btnRegisterOrganizer, btnManageEvents;
    TicketAdapter ticketAdapter;
    List<Ticket> ticketList = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    ApiService apiService;

    int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgCamera = findViewById(R.id.btnCamera);
// Bắt sự kiện nhấn vào nút camera
        imgCamera.setOnClickListener(v -> openCamera());

        // Toolbar
        toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imgAvatar = findViewById(R.id.imgAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
//        rvTickets = findViewById(R.id.rvTickets);
//
//        // RecyclerView tickets
//        rvTickets.setLayoutManager(new LinearLayoutManager(this));
//        ticketAdapter = new TicketAdapter(this, ticketList);
//        rvTickets.setAdapter(ticketAdapter);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Lấy thông tin user từ SharedPreferences

        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if(token != null){
            apiService.getProfile("Bearer " + token).enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if(response.isSuccessful() && response.body() != null){
                        ProfileResponse profile = response.body();

                        // User info
                        if(profile.getUser() != null){
                            tvUserName.setText(profile.getUser().getFullName());
                            tvEmail.setText(profile.getUser().getEmail());

                            String avatarUrl = profile.getUser().getAvatarUrl();
                            if(avatarUrl != null && !avatarUrl.isEmpty()){
                                Glide.with(ProfileActivity.this)
                                        .load(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : R.drawable.placeholder)
                                        .circleCrop()
                                        .into(imgAvatar);
                            }
                        }

                        // Tickets
                        List<Ticket> tickets = profile.getTickets();
                        if(tickets != null){
                            ticketList.clear();
                            ticketList.addAll(tickets);
                            ticketAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Không tải được thông tin profile!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
        }
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // 1. Xóa token
            SharedPreferences logoutprefs = getSharedPreferences("AUTH", MODE_PRIVATE);
            SharedPreferences.Editor editor = logoutprefs.edit();
            editor.clear(); // hoặc editor.remove("TOKEN");
            editor.apply();

            // 2. Chuyển về màn hình Login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);
            finish(); // đóng ProfileActivity
        });

        // Organizer buttons
        btnRegisterOrganizer = findViewById(R.id.btnRegisterOrganizer);
        btnManageEvents = findViewById(R.id.btnManageEvents);

        btnRegisterOrganizer.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrganizerRegisterActivity.class);
            startActivity(intent);
        });

        btnManageEvents.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrganizerDashboardActivity.class);
            startActivity(intent);
        });

        // Check if user is organizer to show/hide buttons
        checkOrganizerStatus(token);


    }

    private void checkOrganizerStatus(String token) {
        if (token == null) return;
        
        // Call API to check organizer status via dashboard endpoint
        apiService.getDashboard("Bearer " + token).enqueue(new Callback<com.example.mobileapp.network.DashboardResponse>() {
            @Override
            public void onResponse(Call<com.example.mobileapp.network.DashboardResponse> call, Response<com.example.mobileapp.network.DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // User is organizer - show manage button, hide register button
                    btnRegisterOrganizer.setVisibility(android.view.View.GONE);
                    btnManageEvents.setVisibility(android.view.View.VISIBLE);
                } else {
                    // User is not organizer - show register button, hide manage button
                    btnRegisterOrganizer.setVisibility(android.view.View.VISIBLE);
                    btnManageEvents.setVisibility(android.view.View.GONE);
                }
            }

            @Override
            public void onFailure(Call<com.example.mobileapp.network.DashboardResponse> call, Throwable t) {
                // Default: show register button
                btnRegisterOrganizer.setVisibility(android.view.View.VISIBLE);
                btnManageEvents.setVisibility(android.view.View.GONE);
            }
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Hiển thị ảnh lên avatar
            imgAvatar.setImageBitmap(imageBitmap);
        }
    }



//    private void loadTickets(int userId){
//        // Gọi API lấy vé của user
//        apiService.getTicketsByUser(userId).enqueue(new Callback<TicketResponse>() {
//            @Override
//            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
//                if(response.isSuccessful() && response.body() != null){
//                    List<Ticket> tickets = response.body().getTickets();
//                    if(tickets != null){
//                        ticketList.clear();
//                        ticketList.addAll(tickets);
//                        ticketAdapter.notifyDataSetChanged();
//                    }
//                } else {
//                    Toast.makeText(ProfileActivity.this, "Không tải được vé!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TicketResponse> call, Throwable t) {
//                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: "+t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
