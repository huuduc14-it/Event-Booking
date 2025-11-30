package com.example.mobileapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Attendee;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.AttendeeResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.network.SimpleResponse;
import com.example.mobileapp.ui.adapter.AttendeeAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventManagerActivity extends AppCompatActivity {

    private int eventId; // ID sự kiện đang quản lý
    private RecyclerView rvAttendees;
    private Button btnImport, btnExportExcel, btnExportPDF;
    private TextView tvTitle, tvSold, tvRevenue;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manager);

        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        initViews();
        setupFilePicker();

        loadEventStats();
        loadAttendees();

        btnImport.setOnClickListener(v -> openFilePicker());
        btnExportExcel.setOnClickListener(v -> downloadFile("excel"));
        btnExportPDF.setOnClickListener(v -> downloadFile("pdf"));
    }

    private void initViews() {
        rvAttendees = findViewById(R.id.rvAttendees);
        rvAttendees.setLayoutManager(new LinearLayoutManager(this));

        btnImport = findViewById(R.id.btnImportExcel);
        btnExportExcel = findViewById(R.id.btnExportExcel);
        btnExportPDF = findViewById(R.id.btnExportPDF);

        tvTitle = findViewById(R.id.tvManagerEventTitle);
        tvSold = findViewById(R.id.tvTotalSold);
        tvRevenue = findViewById(R.id.tvRevenue);
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        uploadExcelFile(fileUri);
                    }
                });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        filePickerLauncher.launch(intent);
    }

    private void uploadExcelFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            RequestBody requestFile = RequestBody.create(MediaType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "import_data.xlsx", requestFile);

            RequestBody eventIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(eventId));
            RequestBody ticketTypePart = RequestBody.create(MediaType.parse("text/plain"), "1");
            RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), "0");

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.importAttendees(body, eventIdPart, ticketTypePart, pricePart).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EventManagerActivity.this, "Import thành công!", Toast.LENGTH_SHORT).show();
                        loadAttendees();
                    } else {
                        Toast.makeText(EventManagerActivity.this, "Import thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(EventManagerActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đọc file", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(String type) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ResponseBody> call = type.equals("excel") ?
                apiService.exportExcel(eventId) : apiService.exportPDF(eventId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = saveFileToDownloads(response.body(), "report_event_" + eventId + (type.equals("excel") ? ".xlsx" : ".pdf"));
                    if (success) {
                        Toast.makeText(EventManagerActivity.this, "Đã lưu vào thư mục Download", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EventManagerActivity.this, "Lỗi tải file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean saveFileToDownloads(ResponseBody body, String fileName) {
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, fileName);

            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) break;
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void loadEventStats() {
        // TODO: implement API call to load stats
    }

    private void loadAttendees() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAttendees(eventId).enqueue(new Callback<AttendeeResponse>() {
            @Override
            public void onResponse(Call<AttendeeResponse> call, Response<AttendeeResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<Attendee> list = response.body().data;
                    AttendeeAdapter adapter = new AttendeeAdapter(list);
                    rvAttendees.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<AttendeeResponse> call, Throwable t) {}
        });
    }
}
