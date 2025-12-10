package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Event;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.ViewAllEventResponse;
import com.example.mobileapp.ui.adapter.EventAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ImageView imgProfile, iconSearch;
    RecyclerView rvSpecialEvents;
    EventAdapter adapter;
    List<Event> eventList = new ArrayList<>();
    Button btnCreateEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iconSearch = findViewById(R.id.btnSearch);
        iconSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnCreateEvent.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
            String token = prefs.getString("TOKEN", null);

            if (token != null) {
                try {
                    // Decode payload từ token
                    String[] parts = token.split("\\."); // header.payload.signature
                    String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
                    JSONObject obj = new JSONObject(payload);
                    String role = obj.getString("role"); // "attendee" hoặc "organizer"

                    if (role.equals("attendee")) {
                        // Chuyển sang đăng ký organizer
                        startActivity(new Intent(MainActivity.this, OrganizerRegisterActivity.class));
                    } else if (role.equals("organizer")) {
                        // Chuyển sang tạo sự kiện
                        startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
                    } else {
                        Toast.makeText(this, "Role không hợp lệ", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Token không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Chưa login
                Toast.makeText(this, "Bạn cần đăng nhập để tạo sự kiện", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

        });


//        imgProfile = findViewById(R.id.imgProfile);
//        imgProfile.setOnClickListener(v -> {
//            SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
//            int userId = prefs.getInt("USER_ID", -1);
//            String token = prefs.getString("TOKEN", null);
//
//            if (userId != -1 && token != null) {
//                // Đã đăng nhập → mở Profile
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                intent.putExtra("USER_ID", userId);
//                startActivity(intent);
//            } else {
//                // Chưa đăng nhập → mở LoginActivity
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                Toast.makeText(this, "Vui lòng đăng nhập để xem Profile", Toast.LENGTH_SHORT).show();
//            }
//        });
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
            int userId = prefs.getInt("USER_ID", -1);
            String token = prefs.getString("TOKEN", null);

            if (userId != -1 && token != null) {

                try {
                    // Tách token
                    String[] parts = token.split("\\.");
                    if (parts.length < 2) {
                        Toast.makeText(this, "Token không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String payloadJson = new String(
                            android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE)
                    );

                    JSONObject payload = new JSONObject(payloadJson);
                    String role = payload.optString("role", "attendee");

                    // 👉 Nếu organizer → mở OrganizerProfileActivity
                    if (role.equals("organizer")) {
                        Intent intent = new Intent(MainActivity.this, OrganizerProfileActivity.class);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                    }
                    // 👉 Nếu attendee → mở ProfileActivity
                    else {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Không đọc được token", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Chưa đăng nhập
                Toast.makeText(this, "Vui lòng đăng nhập để xem Profile", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


        rvSpecialEvents = findViewById(R.id.rvSpecialEvents);
        rvSpecialEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapter = new EventAdapter(this, eventList);
        rvSpecialEvents.setAdapter(adapter);

        loadEvents();
        LinearLayout btnMyTickets = findViewById(R.id.btnMyTickets); // LinearLayout chứa icon/text "Vé của tôi"

        btnMyTickets.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
            int userId = prefs.getInt("USER_ID", -1);
            String token = prefs.getString("TOKEN", null);

            if (userId != -1 && token != null) {
                // Người dùng đã đăng nhập → mở TicketActivity
                Intent intent = new Intent(MainActivity.this, MyTicketActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            } else {
                // Người dùng chưa đăng nhập → thông báo
                Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem vé", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void loadEvents() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.ViewAllEvents().enqueue(new Callback<ViewAllEventResponse>() {
            @Override
            public void onResponse(Call<ViewAllEventResponse> call, Response<ViewAllEventResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ViewAllEventResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }


}
