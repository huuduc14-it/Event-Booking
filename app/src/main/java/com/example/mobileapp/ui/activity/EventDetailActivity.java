package com.example.mobileapp.ui.activity;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.example.mobileapp.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mobileapp.data.model.Event;
import com.example.mobileapp.network.EventDetailResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.dto.TicketTypeResponse;
import com.example.mobileapp.network.dto.BookingRequest;
import com.example.mobileapp.network.dto.BookingResponse;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvTitle, tvLocation, tvAddress, tvTime, tvEndTime, tvDescription, tvVideoUrl;
    ImageView imgThumbnail;
    Button btnBuy;
    List<TicketTypeResponse.TicketType> ticketTypes = new ArrayList<>();

    int eventId;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Mapping UI
        imgThumbnail = findViewById(R.id.imgBanner);
        tvTitle = findViewById(R.id.tvEventTitle);
        tvLocation = findViewById(R.id.tvEventLocation);
        tvAddress = findViewById(R.id.tvEventAddress);
        tvTime = findViewById(R.id.tvEventTime);
        tvEndTime = findViewById(R.id.tvEventEndTime);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvVideoUrl = findViewById(R.id.tvVideoUrl);

        // Nhận eventId từ Intent
        eventId = getIntent().getIntExtra("event_id", -1);

        if (eventId == -1) {
            finish();
            return;
        }

        // Load dữ liệu
        loadEventDetail(eventId);

        // Setup nút mua vé
        btnBuy = findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
            String token = prefs.getString("TOKEN", null);
            
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để mua vé", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            
            // Load ticket types and show dialog
            loadTicketTypesAndShowDialog(token);
        });
    }

    private void loadTicketTypesAndShowDialog(String token) {
        apiService.getTicketTypes(eventId).enqueue(new Callback<TicketTypeResponse>() {
            @Override
            public void onResponse(Call<TicketTypeResponse> call, Response<TicketTypeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ticketTypes = response.body().getData();
                    if (ticketTypes != null && !ticketTypes.isEmpty()) {
                        showTicketSelectionDialog(token);
                    } else {
                        Toast.makeText(EventDetailActivity.this, "Không có loại vé nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventDetailActivity.this, "Không thể tải thông tin vé", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TicketTypeResponse> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTicketSelectionDialog(String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn loại vé");
        
        // Create ticket type names array
        String[] ticketNames = new String[ticketTypes.size()];
        for (int i = 0; i < ticketTypes.size(); i++) {
            TicketTypeResponse.TicketType tt = ticketTypes.get(i);
            ticketNames[i] = tt.getName() + " - " + String.format("%,.0f", tt.getPrice()) + "đ (Còn: " + tt.getRemaining() + ")";
        }
        
        final int[] selectedIndex = {0};
        final int[] quantity = {1};
        
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ticket_selection, null);
        Spinner spinnerTicketType = dialogView.findViewById(R.id.spinnerTicketType);
        NumberPicker numberPickerQty = dialogView.findViewById(R.id.numberPickerQty);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ticketNames);
        spinnerTicketType.setAdapter(adapter);
        
        numberPickerQty.setMinValue(1);
        numberPickerQty.setMaxValue(10);
        numberPickerQty.setValue(1);
        
        spinnerTicketType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedIndex[0] = position;
                int maxQty = Math.min(10, ticketTypes.get(position).getRemaining());
                numberPickerQty.setMaxValue(maxQty > 0 ? maxQty : 1);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        numberPickerQty.setOnValueChangedListener((picker, oldVal, newVal) -> quantity[0] = newVal);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Đặt vé", (dialog, which) -> {
            TicketTypeResponse.TicketType selectedTicket = ticketTypes.get(selectedIndex[0]);
            createBooking(token, selectedTicket, quantity[0]);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void createBooking(String token, TicketTypeResponse.TicketType ticketType, int quantity) {
        BookingRequest request = new BookingRequest();
        request.event_id = eventId;
        request.items = new ArrayList<>();
        
        BookingRequest.BookingItem item = new BookingRequest.BookingItem();
        item.ticket_type_id = ticketType.getTicketTypeId();
        item.quantity = quantity;
        request.items.add(item);
        
        apiService.createBooking("Bearer " + token, request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EventDetailActivity.this, "Đặt vé thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to Payment Activity
                    Intent intent = new Intent(EventDetailActivity.this, PaymentActivity.class);
                    intent.putExtra("BOOKING_ID", response.body().getData().getBookingId());
                    intent.putExtra("TOTAL_AMOUNT", response.body().getData().getTotalAmount());
                    startActivity(intent);
                } else {
                    String errorMsg = "Lỗi đặt vé";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(EventDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventDetail(int id) {
        apiService.getEventDetail(eventId).enqueue(new Callback<EventDetailResponse>() {
            @Override
            public void onResponse(Call<EventDetailResponse> call, Response<EventDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Event event = response.body().getData();

                        tvTitle.setText(event.getTitle());
                        tvLocation.setText(event.getLocationName());
                        
                        String address = event.getAddress();
                        if (address != null && !address.isEmpty()) {
                            tvAddress.setText(address);
                        } else {
                            tvAddress.setText("Chưa có địa chỉ chi tiết");
                        }
                        
                        tvTime.setText(event.getStart_time().replace("T", " ").replace(".000Z", ""));
                        
                        String endTime = event.getEnd_time();
                        if (endTime != null && !endTime.isEmpty()) {
                            tvEndTime.setText("Kết thúc: " + endTime.replace("T", " ").replace(".000Z", ""));
                        } else {
                            tvEndTime.setText("");
                        }
                        
                        tvDescription.setText(event.getDescription());
                        
                        String videoUrl = event.getVideo_url();
                        if (videoUrl != null && !videoUrl.isEmpty()) {
                            tvVideoUrl.setText("Video: " + videoUrl);
                        } else {
                            tvVideoUrl.setText("");
                        }

                        Glide.with(EventDetailActivity.this)
                                .load(event.getThumbnail_url())
                                .placeholder(R.drawable.placeholder)
                                .into(imgThumbnail);

                        getSupportActionBar().setTitle(event.getTitle());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventDetailResponse> call, Throwable t) { }
        });
    }

    // Share menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
//            shareEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void shareEvent(String eventId) {
        String url = "https://yourdomain.com/event/" + eventId;
        String shareText = "Xem sự kiện này: " + url;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Chia sẻ sự kiện");
        startActivity(shareIntent);
    }
}

