package com.example.mobileapp.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.BookingHistoryItem;
import com.example.mobileapp.network.BookingHistoryResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.ui.adapter.BookingHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private List<BookingHistoryItem> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history); // Create a simple XML with RecyclerView
        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);

// Hiện nút mũi tên quay về
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

// Sự kiện khi nhấn nút back
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.rvBookingHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use the Adapter we created earlier
        adapter = new BookingHistoryAdapter(this, historyList);
        recyclerView.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
//        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NCwiZW1haWwiOiJiQGdtYWlsLmNvbSIsInJvbGUiOiJvcmdhbml6ZXIiLCJpYXQiOjE3NjQ0MjU4NjAsImV4cCI6MTc2NTAzMDY2MH0.KWKL1jmOBB4qg0LQU-fnB08foYiO-X1bGVq4U6qjiTY";
//        // 1. Get Token
        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);
        if (token == null) {
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Call API
//        RetrofitClient.getInstance().getMyHistory("Bearer " + token).enqueue(new Callback<BookingHistoryResponse>() {
//            @Override
//            public void onResponse(Call<BookingHistoryResponse> call, Response<BookingHistoryResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    historyList.clear();
//                    historyList.addAll(response.body().data);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(BookingHistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BookingHistoryResponse> call, Throwable t) {
//                Toast.makeText(BookingHistoryActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
//            }
//        });
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.getMyHistory("Bearer " + token)
                .enqueue(new Callback<BookingHistoryResponse>() {
                    @Override
                    public void onResponse(Call<BookingHistoryResponse> call, Response<BookingHistoryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            historyList.clear();
                            historyList.addAll(response.body().data);
                            adapter.notifyDataSetChanged();
                            Log.d("API_DEBUG", "Loaded " + response.body().data.size() + " booking history items");
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e("API_FAIL", "Error Code: " + response.code());
                                Log.e("API_FAIL", "Error Body: " + errorBody);
                                Toast.makeText(BookingHistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BookingHistoryResponse> call, Throwable t) {
                        Log.e("API_FAIL", "Network Error", t);
                        Toast.makeText(BookingHistoryActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}