//package com.example.mobileapp.ui.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import com.example.mobileapp.R;
//public class EventDetailActivity extends AppCompatActivity {
//
//    Toolbar toolbar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_detail);
//
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // Hiển thị nút quay về (arrow back)
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Chi tiết sự kiện");
//
//        // Sự kiện khi nhấn nút back
//        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//    }
//
//    // Load menu share
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
//        return true;
//    }
//
//    // Sự kiện click nút menu
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_share) {
//            shareEvent();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void shareEvent() {
//        String text = "Check out this event on my app!";
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
//
//        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
//    }
//}
//
package com.example.mobileapp.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.jspecify.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


 import android.content.SharedPreferences;
 import android.widget.Button;
 import android.widget.Toast;
 import android.widget.NumberPicker;
 import android.widget.Spinner;
 import android.widget.ArrayAdapter;
 import android.app.AlertDialog;
 import android.view.LayoutInflater;
 import android.view.View;

import com.example.mobileapp.network.WeatherForecastResponse;
import com.example.mobileapp.network.WeatherResponse;
import com.example.mobileapp.network.WeatherRetrofitClient;
import com.example.mobileapp.network.dto.TicketTypeResponse;
 import com.example.mobileapp.network.dto.BookingRequest;
 import com.example.mobileapp.network.dto.BookingResponse;
 import java.util.ArrayList;
 import java.util.List;
public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvTitle, tvLocation, tvAddress, tvTime, tvEndTime, tvDescription, tvVideoUrl, tvWeather;
    ImageView imgThumbnail, imgWeatherIcon;

    int eventId; // nhận từ intent
    ApiService apiService;
    Button btnBuy;
    List<TicketTypeResponse.TicketType> ticketTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
//        Uri data = getIntent().getData();
//        if (data != null) {
//            // Lấy path sau /event/
//            String path = data.getPath(); // ví dụ "/123"
//            if (path != null && path.length() > 1) {
//                int eventId = Integer.parseInt(path.substring(1)); // bỏ "/"
//                // load event detail
//                loadEventDetail(eventId);
//            }
//        } else {
//            // fallback nếu mở từ app bình thường
//            int eventId = getIntent().getIntExtra("event_id", -1);
//            if (eventId != -1) {
//                loadEventDetail(eventId);
//            } else {
//                finish();
//            }
//        }
        Uri data = getIntent().getData();
        if (data != null) {
            String path = data.getPath();
            if (path != null && path.length() > 1) {
                eventId = Integer.parseInt(path.substring(1));
            }
        }
        if (eventId == 0) {
            eventId = getIntent().getIntExtra("event_id", -1);
        }
        if (eventId == -1) finish();
        else loadEventDetail(eventId);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Mapping UI
        imgThumbnail = findViewById(R.id.imgBanner);
        tvTitle = findViewById(R.id.tvEventTitle);
        tvLocation = findViewById(R.id.tvEventLocation);
        tvTime = findViewById(R.id.tvEventTime);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvAddress = findViewById(R.id.tvEventAddress);
        tvEndTime = findViewById(R.id.tvEventEndTime);
        tvVideoUrl = findViewById(R.id.tvVideoUrl);
        tvWeather = findViewById(R.id.tvWeather);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);

        // Nhận eventId từ Intent
        eventId = getIntent().getIntExtra("event_id", -1);

        if (eventId == -1) {
            finish();
            return;
        }
        loadEventDetail(eventId);
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
                        String location = tvAddress.getText().toString().trim();
                        loadWeather(location);
//                        String startTime = event.getStart_time(); // "2025-12-10T15:00:00.000Z"
//                        String forecastDate = startTime.substring(0, 10); // "2025-12-10"
//                        loadWeatherForecast(tvAddress.getText().toString().trim(), forecastDate);
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
            shareEvent(String.valueOf(eventId));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
public void shareEvent(String eventId) {
    String url = "myapp://event/" + eventId; // deep link vào app
    String shareText = "Xem sự kiện này: " + url;

    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
    sendIntent.setType("text/plain");

    Intent shareIntent = Intent.createChooser(sendIntent, "Chia sẻ sự kiện");
    startActivity(shareIntent);
}
    private void loadWeather(String location) {
        ApiService service = WeatherRetrofitClient.getInstance().create(ApiService.class);
        service.getCurrentWeather("8eaae106b89f4f03b6532739250112", location).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String temp = weather.current.temp_c + "°C";
                    String condition = weather.current.condition.text;
                    tvWeather.setText("Thời tiết: " + condition + ", " + temp);

                    // Hiển thị icon nếu muốn
                    Glide.with(EventDetailActivity.this)
                            .load("https:" + weather.current.condition.icon)
                            .into(imgWeatherIcon);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Lỗi tải thời tiết: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadWeatherForecast(String location, String date) {
        ApiService service = WeatherRetrofitClient.getInstance().create(ApiService.class);
        service.getForecastWeather("A8eaae106b89f4f03b6532739250112", location, date).enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherForecastResponse weather = response.body();
                    String temp = weather.forecast.forecastday.get(0).day.avgtemp_c + "°C";
                    String condition = weather.forecast.forecastday.get(0).day.condition.text;
                    tvWeather.setText("Dự báo: " + condition + ", " + temp);

                    Glide.with(EventDetailActivity.this)
                            .load("https:" + weather.forecast.forecastday.get(0).day.condition.icon)
                            .into(imgWeatherIcon);
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Lỗi tải dự báo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}

