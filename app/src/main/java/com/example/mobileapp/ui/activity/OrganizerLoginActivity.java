package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.LoginResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerLoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView txtRegister, txtBackToUser;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_login);

        // Init views
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        txtRegister = findViewById(R.id.txtRegister);
        txtBackToUser = findViewById(R.id.txtBackToUser);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Login button click
        btnLogin.setOnClickListener(v -> loginOrganizer());

        // Register link click
        txtRegister.setOnClickListener(v -> {
            // Mở màn hình đăng ký user trước, sau đó đăng ký organizer
            Intent intent = new Intent(OrganizerLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Back to user app
        txtBackToUser.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerLoginActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loginOrganizer() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        apiService.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (!response.isSuccessful()) {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject obj = new JSONObject(errorJson);
                        String msg = obj.optString("message", "Đăng nhập thất bại");
                        Toast.makeText(OrganizerLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(OrganizerLoginActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                LoginResponse apiResponse = response.body();
                if (apiResponse == null || apiResponse.getToken() == null) {
                    Toast.makeText(OrganizerLoginActivity.this, "Lỗi dữ liệu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lưu token
                String token = apiResponse.getToken();
                SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
                editor.putString("TOKEN", token);
                editor.putInt("USER_ID", apiResponse.getUser().getId());
                editor.putString("USER_NAME", apiResponse.getUser().getFullName());
                editor.apply();

                // Kiểm tra xem user có phải organizer không
                checkOrganizerStatus(token);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(OrganizerLoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOrganizerStatus(String token) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getDashboard("Bearer " + token).enqueue(new Callback<com.example.mobileapp.network.DashboardResponse>() {
            @Override
            public void onResponse(Call<com.example.mobileapp.network.DashboardResponse> call, 
                                   Response<com.example.mobileapp.network.DashboardResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // Là Organizer → Lưu trạng thái và vào Dashboard
                    getSharedPreferences("AUTH", MODE_PRIVATE)
                            .edit()
                            .putBoolean("IS_ORGANIZER", true)
                            .apply();

                    Toast.makeText(OrganizerLoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(OrganizerLoginActivity.this, OrganizerDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Chưa là Organizer → Hỏi đăng ký
                    Toast.makeText(OrganizerLoginActivity.this, 
                            "Tài khoản chưa đăng ký làm Organizer. Vui lòng đăng ký!", 
                            Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(OrganizerLoginActivity.this, OrganizerRegisterActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<com.example.mobileapp.network.DashboardResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrganizerLoginActivity.this, 
                        "Tài khoản chưa đăng ký làm Organizer!", 
                        Toast.LENGTH_LONG).show();
                
                Intent intent = new Intent(OrganizerLoginActivity.this, OrganizerRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
