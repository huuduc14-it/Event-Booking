package com.example.mobileapp.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.ReviewResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private TextView tvEventTitle;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;

    private int eventId;
    private String eventTitle;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        tvEventTitle = findViewById(R.id.tvEventTitle);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitReview);

        // Lấy dữ liệu từ Intent
        eventId = getIntent().getIntExtra("event_id", 0);
        eventTitle = getIntent().getStringExtra("event_title");

        tvEventTitle.setText(eventTitle);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        token = prefs.getString("TOKEN", null);

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.createReview("Bearer " + token, eventId, rating, comment)
                .enqueue(new Callback<ReviewResponse>() {
//                    @Override
//                    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
//                        if (response.isSuccessful() && response.body() != null) {
//                            Toast.makeText(FeedbackActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                            finish(); // Đóng activity
//                        } else {
//                            Toast.makeText(FeedbackActivity.this, "Không gửi được đánh giá", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                    @Override
                    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                        Log.d("API_DEBUG", "Code: " + response.code());
                        Log.d("API_DEBUG", "Body: " + response.body());
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(FeedbackActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(FeedbackActivity.this, "Không gửi được đánh giá", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<ReviewResponse> call, Throwable t) {
                        Toast.makeText(FeedbackActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
