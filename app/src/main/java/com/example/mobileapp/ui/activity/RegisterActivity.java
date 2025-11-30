package com.example.mobileapp.ui.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.mobileapp.R;
import com.example.mobileapp.network.RegisterResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtSignupEmail);
        edtPassword = findViewById(R.id.edtSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);

        // Xử lý nút Tạo tài khoản
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(name, email, password);
            }
        });
    }

//    private void registerUser(String name, String email, String password) {
//        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
//
//        Call<RegisterResponse> call = apiService.register(name, email, password);
//
//        call.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//
//                if (!response.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                RegisterResponse apiResponse = response.body();
//
//                if (apiResponse != null && apiResponse.getMessage() != null) {
//
//                    String msg = apiResponse.getMessage();
//
//                    if (msg.contains("Đăng ký thành công")) {
//                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        finish(); // quay về login
//                    } else {
//                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(RegisterActivity.this, "Lỗi response!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
private void registerUser(String name, String email, String password) {

    ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

    Call<RegisterResponse> call = apiService.register(name, email, password);

    call.enqueue(new Callback<RegisterResponse>() {
        @Override
        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

            if (!response.isSuccessful()) {
                try {
                    // Lấy JSON lỗi
                    String errorJson = response.errorBody().string();

                    JSONObject obj = new JSONObject(errorJson);

                    if (obj.has("message")) {
                        String msg = obj.getString("message");
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Lỗi server không xác định!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Lỗi đọc thông báo lỗi!", Toast.LENGTH_SHORT).show();
                }
                return;
            }


            RegisterResponse api = response.body();
            if (api == null) {
                Toast.makeText(RegisterActivity.this, "Lỗi response!", Toast.LENGTH_SHORT).show();
                return;
            }

            // ---- dùng success thay vì contains() ----
            if (api.isSuccess()) {
                Toast.makeText(RegisterActivity.this, api.getMessage(), Toast.LENGTH_SHORT).show();
                finish(); // quay lại login
            } else {
                Toast.makeText(RegisterActivity.this, api.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<RegisterResponse> call, Throwable t) {
            Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    });
}


}
