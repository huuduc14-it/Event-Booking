package com.example.mobileapp.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp.R;
import com.example.mobileapp.network.SimpleResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerRegisterActivity extends AppCompatActivity {

    private EditText etOrgName;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_register);

        etOrgName = findViewById(R.id.etOrgName);
        btnRegister = findViewById(R.id.btnRegisterOrganizer);

        btnRegister.setOnClickListener(v -> {
            String orgName = etOrgName.getText().toString().trim();
            if (orgName.isEmpty()) {
                etOrgName.setError("Vui lòng nhập tên tổ chức");
                return;
            }
            registerOrganizer(orgName);
        });
    }

    private void registerOrganizer(String orgName) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        OrganizerRequest request = new OrganizerRequest(orgName);

        apiService.registerOrganizer(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrganizerRegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OrganizerRegisterActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(OrganizerRegisterActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class OrganizerRequest {
        String organization_name;
        public OrganizerRequest(String name) { this.organization_name = name; }
    }
}
