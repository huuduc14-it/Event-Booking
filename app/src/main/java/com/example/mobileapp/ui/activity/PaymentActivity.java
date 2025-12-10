package com.example.mobileapp.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobileapp.R;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private int bookingId;
    private double totalAmount;
    private TextView tvBookingId, tvTotalAmount, tvStatus;
    private Button btnPayVNPay, btnPayLater, btnViewTickets;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Get data from intent
        bookingId = getIntent().getIntExtra("BOOKING_ID", -1);
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);

        if (bookingId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin đặt vé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayBookingInfo();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbarPayment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thanh toán");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvBookingId = findViewById(R.id.tvBookingId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvStatus = findViewById(R.id.tvStatus);
        btnPayVNPay = findViewById(R.id.btnPayVNPay);
        btnPayLater = findViewById(R.id.btnPayLater);
        btnViewTickets = findViewById(R.id.btnViewTickets);

        btnPayVNPay.setOnClickListener(v -> createVNPayPayment());
        btnPayLater.setOnClickListener(v -> {
            Toast.makeText(this, "Vui lòng thanh toán trong vòng 24 giờ để giữ vé", Toast.LENGTH_LONG).show();
            finish();
        });
        btnViewTickets.setOnClickListener(v -> {
            // Navigate to tickets list
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NAVIGATE_TO", "TICKETS");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        btnViewTickets.setVisibility(android.view.View.GONE);
    }

    private void displayBookingInfo() {
        tvBookingId.setText("Mã đặt vé: #" + bookingId);
        tvTotalAmount.setText("Tổng tiền: " + String.format("%,.0f", totalAmount) + "đ");
        tvStatus.setText("Trạng thái: Chờ thanh toán");
    }

    private void createVNPayPayment() {
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        
        btnPayVNPay.setEnabled(false);
        btnPayVNPay.setText("Đang xử lý...");

        apiService.createPayment("Bearer " + token, bookingId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnPayVNPay.setEnabled(true);
                btnPayVNPay.setText("Thanh toán qua VNPay");
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonStr = response.body().string();
                        JSONObject json = new JSONObject(jsonStr);
                        String paymentUrl = json.getString("paymentUrl");
                        
                        // Open VNPay in browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                        startActivity(browserIntent);
                        
                    } catch (Exception e) {
                        Toast.makeText(PaymentActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi tạo link thanh toán", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnPayVNPay.setEnabled(true);
                btnPayVNPay.setText("Thanh toán qua VNPay");
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check payment status when user returns from VNPay
        checkPaymentStatus();
    }

    private void checkPaymentStatus() {
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        
        apiService.getBookingDetail("Bearer " + token, bookingId).enqueue(new Callback<com.example.mobileapp.network.dto.BookingResponse>() {
            @Override
            public void onResponse(Call<com.example.mobileapp.network.dto.BookingResponse> call, 
                                   Response<com.example.mobileapp.network.dto.BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String status = response.body().getData().payment_status;
                    if ("paid".equals(status)) {
                        tvStatus.setText("Trạng thái: ĐÃ THANH TOÁN ✓");
                        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        btnPayVNPay.setVisibility(android.view.View.GONE);
                        btnPayLater.setVisibility(android.view.View.GONE);
                        btnViewTickets.setVisibility(android.view.View.VISIBLE);
                        Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.mobileapp.network.dto.BookingResponse> call, Throwable t) {
                // Silent fail - just checking status
            }
        });
    }
}
