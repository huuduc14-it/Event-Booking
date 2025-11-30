package com.example.mobileapp.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp.R;
import com.example.mobileapp.network.SimpleResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.RetrofitClient;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etLocation, etThumbUrl;
    private EditText etTicketName, etTicketPrice, etTicketQty;
    private Button btnPickDate, btnCreate;
    private String selectedDateTime = ""; // Format: YYYY-MM-DD HH:mm:ss

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Binding Views
        etTitle = findViewById(R.id.etEventTitle);
        etDesc = findViewById(R.id.etEventDesc);
        etLocation = findViewById(R.id.etLocation);
        etThumbUrl = findViewById(R.id.etImageUrl);
        etTicketName = findViewById(R.id.etTicketName);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etTicketQty = findViewById(R.id.etTicketQty);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnCreate = findViewById(R.id.btnCreateEvent);

        btnPickDate.setOnClickListener(v -> showDateTimePicker());

        btnCreate.setOnClickListener(v -> createEvent());
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                selectedDateTime = String.format("%d-%02d-%02d %02d:%02d:00", 
                        year, month + 1, dayOfMonth, hourOfDay, minute);
                btnPickDate.setText(selectedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createEvent() {
        String title = etTitle.getText().toString().trim();

        if (title.isEmpty() || selectedDateTime.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        List<TicketType> tickets = new ArrayList<>();
        tickets.add(new TicketType(
                etTicketName.getText().toString(),
                Double.parseDouble(etTicketPrice.getText().toString().isEmpty() ? "0" : etTicketPrice.getText().toString()),
                Integer.parseInt(etTicketQty.getText().toString().isEmpty() ? "0" : etTicketQty.getText().toString())
        ));

        CreateEventRequest request = new CreateEventRequest();
        request.title = title;
        request.description = etDesc.getText().toString();
        request.start_time = selectedDateTime;
        request.location_name = etLocation.getText().toString();
        request.thumbnail_url = etThumbUrl.getText().toString();
        request.category_id = 1;
        request.ticket_types = tickets;

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.createEvent(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateEventActivity.this, "Tạo sự kiện thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Lỗi tạo sự kiện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class CreateEventRequest {
        public String title, description, start_time, location_name, thumbnail_url;
        public int category_id;
        public List<TicketType> ticket_types;
    }

    public static class TicketType {
        public String name;
        public double price;
        public int quantity;
        TicketType(String n, double p, int q) { name = n; price = p; quantity = q; }
    }
}
