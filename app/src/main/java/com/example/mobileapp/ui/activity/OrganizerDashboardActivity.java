package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.DashboardEvent;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.DashboardResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.ui.adapter.DashboardAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerDashboardActivity extends AppCompatActivity {

	private DashboardAdapter adapter;
	private ProgressBar progressBar;
	private TextView tvError;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organizer_dashboard);

		RecyclerView rv = findViewById(R.id.rvDashboard);
		progressBar = findViewById(R.id.progressBar);
		tvError = findViewById(R.id.tvError);

		adapter = new DashboardAdapter(this::openAttendees);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadDashboard();
	}

	private void loadDashboard() {
		progressBar.setVisibility(View.VISIBLE);
		tvError.setVisibility(View.GONE);
		String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
		if (token.isEmpty()) {
			showError("Vui lòng đăng nhập");
			return;
		}

		ApiService api = RetrofitClient.getInstance().create(ApiService.class);
		api.getDashboard("Bearer " + token).enqueue(new Callback<DashboardResponse>() {
			@Override
			public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
				progressBar.setVisibility(View.GONE);
				if (!response.isSuccessful() || response.body() == null) {
					showError("Lỗi server");
					return;
				}
				DashboardResponse body = response.body();
				if (!body.success) {
					showError(body.message != null ? body.message : "Lỗi");
					return;
				}
				List<DashboardEvent> list = body.data;
				adapter.setItems(list);
			}

			@Override
			public void onFailure(Call<DashboardResponse> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				showError("Kết nối thất bại");
			}
		});
	}

	private void showError(String msg) {
		tvError.setVisibility(View.VISIBLE);
		tvError.setText(msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void openAttendees(DashboardEvent event) {
		Intent i = new Intent(this, OrganizerAttendeeListActivity.class);
		i.putExtra("EVENT_ID", event.event_id);
		i.putExtra("EVENT_TITLE", event.title);
		startActivity(i);
	}
}
