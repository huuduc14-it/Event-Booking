package com.example.mobileapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.network.ApiResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.dto.TicketTypeResponse;
import com.example.mobileapp.ui.adapter.TicketTypeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerManageTicketActivity extends AppCompatActivity {

    private RecyclerView rvTicketTypes;
    private ProgressBar progressBar;
    private TextView tvError;
    private TicketTypeAdapter adapter;
    private int eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_manage_tickets);

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvTicketTypes = findViewById(R.id.rvTicketTypes);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        adapter = new TicketTypeAdapter(this::onUpdateTicket);
        rvTicketTypes.setLayoutManager(new LinearLayoutManager(this));
        rvTicketTypes.setAdapter(adapter);

        loadTicketTypes();
    }

    private void loadTicketTypes() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getTicketTypes(eventId).enqueue(new Callback<TicketTypeResponse>() {
            @Override
            public void onResponse(Call<TicketTypeResponse> call, Response<TicketTypeResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    adapter.setItems(response.body().data);
                } else {
                    showError("Không thể tải danh sách vé");
                }
            }

            @Override
            public void onFailure(Call<TicketTypeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Lỗi kết nối");
            }
        });
    }

    private void onUpdateTicket(int ticketTypeId, int newQuantity) {
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.updateTicketQuantity("Bearer " + token, ticketTypeId, newQuantity).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Toast.makeText(OrganizerManageTicketActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadTicketTypes(); // Reload to show updated data
                } else {
                    String msg = response.body() != null ? response.body().message : "Lỗi cập nhật";
                    Toast.makeText(OrganizerManageTicketActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrganizerManageTicketActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(msg);
    }
}
