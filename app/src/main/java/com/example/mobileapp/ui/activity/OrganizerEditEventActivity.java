package com.example.mobileapp.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Category;
import com.example.mobileapp.network.ApiResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.CategoryResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.dto.CreateEventRequest;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerEditEventActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etLocation, etAddress, etThumbUrl, etVideoUrl;
    private Spinner spinnerCategory;
    private Button btnUpdate;
    private int eventId;
    private List<Category> categories = new ArrayList<>();
    private ArrayAdapter<Category> categoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_create_event); // Reuse same layout

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Lỗi: Không có sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etTitle = findViewById(R.id.etEventTitle);
        etDesc = findViewById(R.id.etEventDesc);
        etLocation = findViewById(R.id.etLocation);
        etAddress = findViewById(R.id.etAddress);
        etThumbUrl = findViewById(R.id.etImageUrl);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdate = findViewById(R.id.btnCreateEvent);
        btnUpdate.setText("Cập nhật sự kiện");

        // Setup category spinner
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Pre-fill with existing data if passed
        String title = getIntent().getStringExtra("TITLE");
        String desc = getIntent().getStringExtra("DESC");
        String location = getIntent().getStringExtra("LOCATION");
        String address = getIntent().getStringExtra("ADDRESS");
        String thumb = getIntent().getStringExtra("THUMB");
        String video = getIntent().getStringExtra("VIDEO_URL");
        int catId = getIntent().getIntExtra("CATEGORY_ID", 1);

        if (title != null) etTitle.setText(title);
        if (desc != null) etDesc.setText(desc);
        if (location != null) etLocation.setText(location);
        if (address != null) etAddress.setText(address);
        if (thumb != null) etThumbUrl.setText(thumb);
        if (video != null) etVideoUrl.setText(video);

        loadCategories(catId);
        btnUpdate.setOnClickListener(v -> updateEvent());
    }

    private void loadCategories(int selectedCategoryId) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAllCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body().getData());
                    categoryAdapter.notifyDataSetChanged();
                    
                    // Select the current category
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getCategory_id() == selectedCategoryId) {
                            spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(OrganizerEditEventActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent() {
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateEventRequest request = new CreateEventRequest();
        request.title = etTitle.getText().toString().trim();
        request.description = etDesc.getText().toString().trim();
        request.location_name = etLocation.getText().toString().trim();
        request.address = etAddress.getText().toString().trim();
        request.thumbnail_url = etThumbUrl.getText().toString().trim();
        request.video_url = etVideoUrl.getText().toString().trim();
        request.start_time = ""; // Can't update time easily, backend should handle null
        request.end_time = "";
        
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        request.category_id = selectedCategory != null ? selectedCategory.getCategory_id() : 1;
        request.ticket_types = null; // Not updating tickets here

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.updateEvent("Bearer " + token, eventId, request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Toast.makeText(OrganizerEditEventActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OrganizerEditEventActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(OrganizerEditEventActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
