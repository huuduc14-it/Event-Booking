package com.example.mobileapp.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp.R;
import android.content.SharedPreferences;
import com.example.mobileapp.network.ApiResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.dto.OrganizerRegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerRegisterActivity extends AppCompatActivity {

    private EditText etOrgName, etTaxCode, etAddress, etBankAccount, etContactEmail, etContactPhone;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_register);

        etOrgName = findViewById(R.id.etOrgName);
        etTaxCode = findViewById(R.id.etTaxCode);
        etAddress = findViewById(R.id.etAddress);
        etBankAccount = findViewById(R.id.etBankAccount);
        etContactEmail = findViewById(R.id.etContactEmail);
        etContactPhone = findViewById(R.id.etContactPhone);
        btnRegister = findViewById(R.id.btnRegisterOrganizer);

        btnRegister.setOnClickListener(v -> {
            String orgName = etOrgName.getText().toString().trim();
            String taxCode = etTaxCode.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String bankAccount = etBankAccount.getText().toString().trim();
            String contactEmail = etContactEmail.getText().toString().trim();
            String contactPhone = etContactPhone.getText().toString().trim();

            // Validate required fields
            if (orgName.isEmpty()) {
                etOrgName.setError("Vui lòng nhập tên tổ chức");
                return;
            }
            if (taxCode.isEmpty()) {
                etTaxCode.setError("Vui lòng nhập mã số thuế");
                return;
            }
            if (address.isEmpty()) {
                etAddress.setError("Vui lòng nhập địa chỉ");
                return;
            }
            if (contactEmail.isEmpty()) {
                etContactEmail.setError("Vui lòng nhập email liên hệ");
                return;
            }
            if (contactPhone.isEmpty()) {
                etContactPhone.setError("Vui lòng nhập số điện thoại liên hệ");
                return;
            }

            registerOrganizer(orgName, taxCode, address, bankAccount, contactEmail, contactPhone);
        });
    }

    private void registerOrganizer(String orgName, String taxCode, String address, 
                                   String bankAccount, String contactEmail, String contactPhone) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        OrganizerRegisterRequest request = new OrganizerRegisterRequest(
            orgName, taxCode, address, 
            bankAccount.isEmpty() ? null : bankAccount, 
            contactEmail, contactPhone
        );

        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.registerOrganizer("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse body = response.body();
                    Toast.makeText(OrganizerRegisterActivity.this, 
                        body.message != null ? body.message : "Đăng ký thành công!", 
                        Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(OrganizerRegisterActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(OrganizerRegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
