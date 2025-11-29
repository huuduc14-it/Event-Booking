package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.ApiResponse;
import com.example.mobileapp.data.model.BookingReq;
import com.example.mobileapp.data.model.TicketListResponse;
import com.example.mobileapp.data.model.TicketType;
import com.example.mobileapp.data.network.ApiClient;
import com.example.mobileapp.data.network.ApiService;
import com.example.mobileapp.ui.adapter.BookingAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private int eventId = 6; // Dynamic Event ID
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<TicketType> ticketList = new ArrayList<>();
    private TextView tvTotalPrice;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 1. GET EVENT ID FROM INTENT (Passed from Event List)
        // Default to -1 if something goes wrong
//        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        if (eventId == -1) {
            Toast.makeText(this, "Error: No Event Selected", Toast.LENGTH_SHORT).show();
//            finish();
            return;
        }

        initViews();
        loadTickets();

        btnCheckout.setOnClickListener(v -> performBooking());
    }

    private void initViews() {
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnConfirmBooking);
        recyclerView = findViewById(R.id.rvBookingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(ticketList, this::calculateTotal);
        recyclerView.setAdapter(adapter);
    }

    private void calculateTotal() {
        double total = 0;
        boolean hasItems = false;

        for (TicketType t : ticketList) {
            if (t.selectedQty > 0) {
                total += (t.price * t.selectedQty);
                hasItems = true;
            }
        }
        tvTotalPrice.setText("$" + total);
        btnCheckout.setEnabled(hasItems);
    }

    private void loadTickets() {
        // 1. Get Token from Storage
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedToken = prefs.getString("auth_token", null);
//        String authHeader = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NCwiZW1haWwiOiJiQGdtYWlsLmNvbSIsInJvbGUiOiJvcmdhbml6ZXIiLCJpYXQiOjE3NjQ0MjU4NjAsImV4cCI6MTc2NTAzMDY2MH0.KWKL1jmOBB4qg0LQU-fnB08foYiO-X1bGVq4U6qjiTY";
        String authHeader = null;
        if (savedToken != null) {
            authHeader = "Bearer " + savedToken;
        } else {
            // Logic if user isn't logged in:
            // Since your backend requires a token, this call will fail (403) if null.
            // You might want to redirect to LoginActivity here.
            Toast.makeText(this, "Please login to view tickets", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Call API with BOTH arguments
        // Also fixed a syntax error: added '.' before enqueue
        ApiClient.getService().getEventTickets(eventId, authHeader).enqueue(new Callback<TicketListResponse>() {
            @Override
            public void onResponse(Call<TicketListResponse> call, Response<TicketListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticketList.clear();
                    ticketList.addAll(response.body().data);
                    adapter.notifyDataSetChanged();
                } else {
                    // This handles the 403 if token is invalid
                    Toast.makeText(BookingActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TicketListResponse> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Error loading tickets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performBooking() {
        // 2. RETRIEVE TOKEN DYNAMICALLY
        // This works for ANY user (Organizer or Attendee) who is logged in
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedToken = prefs.getString("auth_token", null);
//        String savedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NCwiZW1haWwiOiJiQGdtYWlsLmNvbSIsInJvbGUiOiJvcmdhbml6ZXIiLCJpYXQiOjE3NjQ0MjU4NjAsImV4cCI6MTc2NTAzMDY2MH0.KWKL1jmOBB4qg0LQU-fnB08foYiO-X1bGVq4U6qjiTY";
        if (savedToken == null) {
            Toast.makeText(this, "Please Login to Book Tickets", Toast.LENGTH_LONG).show();
            // Optional: Redirect to LoginActivity
            // startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        String authHeader = "Bearer " + savedToken;

        // 3. PREPARE REQUEST
        BookingReq req = new BookingReq();
        req.eventId = eventId;
        req.items = new ArrayList<>();

        for (TicketType t : ticketList) {
            if (t.selectedQty > 0) {
                req.items.add(new BookingReq.BookingItem(t.id, t.selectedQty));
            }
        }

        // 4. CALL API
        ApiClient.getService().bookTickets(authHeader, req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Booking Successful!", Toast.LENGTH_LONG).show();
                    finish(); // Close screen
                } else {
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Error";
                        Toast.makeText(BookingActivity.this, "Failed: " + err, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {}
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}