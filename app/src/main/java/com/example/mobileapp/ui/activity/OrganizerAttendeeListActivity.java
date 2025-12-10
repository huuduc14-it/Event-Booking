package com.example.mobileapp.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileapp.R;
import com.example.mobileapp.network.ApiResponse;
import com.example.mobileapp.network.ApiService;
import com.example.mobileapp.network.AttendeeResponse;
import com.example.mobileapp.network.RetrofitClient;
import com.example.mobileapp.ui.adapter.AttendeeAdapter;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerAttendeeListActivity extends AppCompatActivity {

    private static final int REQ_STORAGE = 101;

    private ProgressBar progressBar;
    private TextView tvError, tvTitle;
    private AttendeeAdapter adapter;
    private int eventId;

    private final ActivityResultLauncher<String> filePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), this::onFilePicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_attendee_list);

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        String eventTitle = getIntent().getStringExtra("EVENT_TITLE");

        RecyclerView rv = findViewById(R.id.rvAttendees);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Người tham dự - " + eventTitle);

        adapter = new AttendeeAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        Button btnExportExcel = findViewById(R.id.btnExportExcel);
        Button btnExportPdf = findViewById(R.id.btnExportPdf);
        Button btnImport = findViewById(R.id.btnImport);

        btnExportExcel.setOnClickListener(v -> exportFile(true));
        btnExportPdf.setOnClickListener(v -> exportFile(false));
        btnImport.setOnClickListener(v -> pickFile());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttendees();
    }

    private void loadAttendees() {
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            showError("Vui lòng đăng nhập");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getAttendees("Bearer " + token, eventId).enqueue(new Callback<AttendeeResponse>() {
            @Override
            public void onResponse(Call<AttendeeResponse> call, Response<AttendeeResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    showError("Lỗi server");
                    return;
                }
                AttendeeResponse body = response.body();
                if (!body.success) {
                    showError(body.message != null ? body.message : "Lỗi");
                    return;
                }
                adapter.setItems(body.data);
            }

            @Override
            public void onFailure(Call<AttendeeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Kết nối thất bại");
            }
        });
    }

    private void pickFile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_STORAGE);
                return;
            }
        }
        filePicker.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickFile();
        }
    }

    private void onFilePicked(Uri uri) {
        if (uri == null) return;
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            showError("Vui lòng đăng nhập");
            return;
        }

        try {
            ContentResolver resolver = getContentResolver();
            String fileName = getFileName(uri);
            InputStream is = resolver.openInputStream(uri);
            if (is == null) {
                showError("Không mở được file");
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] tmp = new byte[4096];
            int n;
            while ((n = is.read(tmp)) != -1) {
                buffer.write(tmp, 0, n);
            }
            byte[] bytes = buffer.toByteArray();
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);

            RequestBody eventIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(eventId));
            RequestBody ticketTypeIdPart = RequestBody.create(MediaType.parse("text/plain"), "1");
            RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), "0");

            progressBar.setVisibility(View.VISIBLE);
            ApiService api = RetrofitClient.getInstance().create(ApiService.class);
            api.importAttendees("Bearer " + token, body, eventIdPart, ticketTypeIdPart, pricePart)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().success) {
                                Toast.makeText(OrganizerAttendeeListActivity.this, "Import thành công", Toast.LENGTH_SHORT).show();
                                loadAttendees();
                            } else {
                                showError("Import thất bại");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            showError("Import lỗi");
                        }
                    });
        } catch (Exception e) {
            showError("Lỗi đọc file");
        }
    }

    private void exportFile(boolean isExcel) {
        String token = getSharedPreferences("AUTH", MODE_PRIVATE).getString("TOKEN", "");
        if (token.isEmpty()) {
            showError("Vui lòng đăng nhập");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        Call<ResponseBody> call = isExcel ? api.exportExcel("Bearer " + token, eventId) : api.exportPDF("Bearer " + token, eventId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    saveToDownloads(response.body(), isExcel ? "attendees.xlsx" : "attendees.pdf");
                } else {
                    showError("Export thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Kết nối lỗi");
            }
        });
    }

    private void saveToDownloads(ResponseBody body, String name) {
        try {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outFile = new File(downloads, name);
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(body.bytes());
            fos.flush();
            fos.close();
            Toast.makeText(this, "Đã lưu " + name + " trong Downloads", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            showError("Không lưu được file");
        }
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}