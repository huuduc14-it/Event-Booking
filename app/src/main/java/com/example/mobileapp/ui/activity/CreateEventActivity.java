package com.example.mobileapp.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Category;
import com.example.mobileapp.network.ApiResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.CategoryResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.dto.CreateEventRequest;
import com.example.mobileapp.network.dto.TicketTypeDto;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etLocation, etAddress, etThumbUrl, etVideoUrl;
    private EditText etTicketName, etTicketPrice, etTicketQty, etTicketDesc;
    private Spinner spinnerCategory;
    private Button btnPickStartDate, btnPickEndDate, btnCreate;
    private String selectedStartDateTime = ""; // Format: YYYY-MM-DD HH:mm:ss
    private String selectedEndDateTime = "";
    private List<Category> categories = new ArrayList<>();
    private ArrayAdapter<Category> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_create_event);

        // Binding Views
        etTitle = findViewById(R.id.etEventTitle);
        etDesc = findViewById(R.id.etEventDesc);
        etLocation = findViewById(R.id.etLocation);
        etAddress = findViewById(R.id.etAddress);
        etThumbUrl = findViewById(R.id.etImageUrl);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etTicketName = findViewById(R.id.etTicketName);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etTicketQty = findViewById(R.id.etTicketQty);
        etTicketDesc = findViewById(R.id.etTicketDesc);
        btnPickStartDate = findViewById(R.id.btnPickStartDate);
        btnPickEndDate = findViewById(R.id.btnPickEndDate);
        btnCreate = findViewById(R.id.btnCreateEvent);

        // Setup category spinner
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        btnPickStartDate.setOnClickListener(v -> showDateTimePicker(true));
        btnPickEndDate.setOnClickListener(v -> showDateTimePicker(false));
        btnCreate.setOnClickListener(v -> createEvent());

        loadCategories();
    }

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAllCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body().getData());
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateTimePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                String dateTime = String.format("%d-%02d-%02d %02d:%02d:00", 
                        year, month + 1, dayOfMonth, hourOfDay, minute);
                if (isStartDate) {
                    selectedStartDateTime = dateTime;
                    btnPickStartDate.setText("Start: " + dateTime);
                } else {
                    selectedEndDateTime = dateTime;
                    btnPickEndDate.setText("End: " + dateTime);
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createEvent() {
        String title = etTitle.getText().toString().trim();

        if (title.isEmpty() || selectedStartDateTime.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        List<TicketTypeDto> tickets = new ArrayList<>();
        tickets.add(new TicketTypeDto(
            etTicketName.getText().toString(),
            Double.parseDouble(etTicketPrice.getText().toString().isEmpty() ? "0" : etTicketPrice.getText().toString()),
            Integer.parseInt(etTicketQty.getText().toString().isEmpty() ? "0" : etTicketQty.getText().toString()),
            null
        ));

        CreateEventRequest request = new CreateEventRequest();
        request.title = title;
        request.description = etDesc.getText().toString();
        request.start_time = selectedStartDateTime;
        request.end_time = selectedEndDateTime.isEmpty() ? null : selectedEndDateTime;
        request.location_name = etLocation.getText().toString();
        request.address = etAddress.getText().toString();
        request.thumbnail_url = etThumbUrl.getText().toString();
        request.video_url = etVideoUrl.getText().toString();
        
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        request.category_id = selectedCategory != null ? selectedCategory.getCategory_id() : 1;
        request.ticket_types = tickets;

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập tổ chức", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.createEvent("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
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
}
