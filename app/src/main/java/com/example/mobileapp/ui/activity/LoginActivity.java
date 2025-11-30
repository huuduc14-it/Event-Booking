package com.example.mobileapp.ui.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mobileapp.R;
import com.example.mobileapp.network.LoginResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // XML của bạn

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);

        // Khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Chuyển sang màn hình đăng ký
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void loginUser() {
//        String email = edtEmail.getText().toString().trim();
//        String password = edtPassword.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
//        Call<LoginResponse> call = api.login(email, password);
//
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//
//                if (!response.isSuccessful()) {
//                    // Lấy message từ body
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Toast.makeText(LoginActivity.this, errorBody, Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
//                    }
//                    return;
//                }
//
//                LoginResponse apiResponse = response.body();
//
//                if (apiResponse != null && apiResponse.getToken() != null && !apiResponse.getToken().isEmpty()) {
//
//                    String token = apiResponse.getToken();
//
//                    getSharedPreferences("AUTH", MODE_PRIVATE)
//                            .edit()
//                            .putString("TOKEN", token)
//                            .apply();
//
//                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//
//                } else {
//                    Toast.makeText(LoginActivity.this,
//                            apiResponse != null ? apiResponse.getMessage() : "Sai email hoặc mật khẩu!",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<LoginResponse> call = api.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (!response.isSuccessful()) {
                    // Lấy thông điệp lỗi từ backend
                    try {
                        String errorJson = response.errorBody().string();

                        // Lấy message từ JSON
                        JSONObject obj = new JSONObject(errorJson);
                        String msg = obj.optString("message", "Lỗi không xác định");

                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                // ✔ Khi request thành công (status 200)
                LoginResponse apiResponse = response.body();
                if (apiResponse == null) {
                    Toast.makeText(LoginActivity.this, "Lỗi dữ liệu server!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ❌ Nếu message không phải "Login thành công" → show lỗi và return
                if (!"Đăng nhập thành công".equals(apiResponse.getMessage())) {
                    Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✔ Đúng rồi → Lấy token
                String token = apiResponse.getToken();
                if (token == null || token.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Không nhận được token!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✔ Lưu token
                getSharedPreferences("AUTH", MODE_PRIVATE)
                        .edit()
                        .putString("TOKEN", token)
                        .putInt("USER_ID", apiResponse.getUser().getId())
                        .putString("USER_NAME", apiResponse.getUser().getFullName())
                        .apply();

                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
