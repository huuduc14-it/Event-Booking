package com.example.mobileapp.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

// --- ADD THIS IMPORT ---
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Artist;
import com.example.mobileapp.data.model.ArtistResponse; // Make sure this class exists
import com.example.mobileapp.data.model.CreateEventReq;
import com.example.mobileapp.data.model.ApiResponse;
import com.example.mobileapp.data.network.ApiClient;
import com.example.mobileapp.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.example.mobileapp.utils.FileUtil; // Your helper class
import java.io.File;
import com.google.gson.Gson;
public class OrganizerCreateEventActivity extends AppCompatActivity {

    // Hardcoded token for testing
//    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NCwiZW1haWwiOiJiQGdtYWlsLmNvbSIsInJvbGUiOiJhdHRlbmRlZSIsImlhdCI6MTc2NDM0NjYwNywiZXhwIjoxNzY0OTUxNDA3fQ.coStu3pZrP1rjZVy1cdGThcF0VWE0zWCiAuc_qkhMpw";

    // UI Components
    private ImageView imgThumbnailPreview;
    private Button btnSelectImage;
    private Uri selectedImageUri = null; // Holds the chosen image
    private EditText etTitle, etDescription, etLocationName, etAddress;
    private EditText etStartTime, etEndTime, etThumbnail;
    private Spinner spinnerCategory;
    private LinearLayout layoutTicketContainer;
    private Button btnAddTicketRow, btnCreate, btnSelectArtist; // Added btnSelectArtist

    // Artist Data
    private List<Artist> availableArtists = new ArrayList<>();
    private boolean[] selectedArtistBooleans; // Tracks which boxes are checked
    private ArrayList<Integer> selectedArtistIds = new ArrayList<>();

    // Data for Spinner
    private String[] categoryNames = {"Music", "Theater", "Sports"};
    private int[] categoryIds = {1, 2, 3};
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgThumbnailPreview.setImageURI(selectedImageUri); // Show preview
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_create_event);

        initViews();
        setupCategorySpinner();

        // 1. Load Artists immediately when screen opens
        fetchArtists();

        // Listeners
        btnAddTicketRow.setOnClickListener(v -> addTicketRow());
        btnCreate.setOnClickListener(v -> performCreateEvent());

        // 2. Open Dialog when clicked
        btnSelectArtist.setOnClickListener(v -> showArtistDialog());

        // Add Listener for Image Button
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // Add one initial empty ticket row
        addTicketRow();
    }

    // --- NEW: FETCH ARTISTS FROM BACKEND ---
    private void fetchArtists() {
        // Log that we are starting
        Log.d("API_DEBUG", "Starting to fetch artists...");

        ApiClient.getService().getArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                // Case 1: Success
                if (response.isSuccessful() && response.body() != null) {
                    availableArtists = response.body().data;
                    selectedArtistBooleans = new boolean[availableArtists.size()];

                    Log.d("API_DEBUG", "Success! Found " + availableArtists.size() + " artists.");

                    // Show a small toast to confirm it worked
                    Toast.makeText(OrganizerCreateEventActivity.this, "Loaded " + availableArtists.size() + " artists", Toast.LENGTH_SHORT).show();
                }
                // Case 2: Server Error (404, 500)
                else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("API_FAIL", "Server Error Code: " + response.code());
                        Log.e("API_FAIL", "Error Body: " + errorBody);
                        Toast.makeText(OrganizerCreateEventActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                // Case 3: Network/Parsing Error
                Log.e("API_FAIL", "Network Failure: " + t.getMessage());
                t.printStackTrace(); // This prints the full crash report to Logcat
                Toast.makeText(OrganizerCreateEventActivity.this, "Connection Error: Check Logcat", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- NEW: SHOW MULTI-SELECT DIALOG ---
    private void showArtistDialog() {
        if (availableArtists.isEmpty()) {
            Toast.makeText(this, "No artists found / Loading...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert List<Artist> to String[] so the Dialog can display names
        String[] artistNames = new String[availableArtists.size()];
        for (int i = 0; i < availableArtists.size(); i++) {
            artistNames[i] = availableArtists.get(i).name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Performing Artists");

        // The Boolean array keeps track of checks even if you close and reopen the dialog
        builder.setMultiChoiceItems(artistNames, selectedArtistBooleans, (dialog, which, isChecked) -> {
            selectedArtistBooleans[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // User clicked OK - Gather selected IDs
            selectedArtistIds.clear();
            StringBuilder buttonText = new StringBuilder();

            for (int i = 0; i < availableArtists.size(); i++) {
                if (selectedArtistBooleans[i]) {
                    Artist artist = availableArtists.get(i);
                    selectedArtistIds.add(artist.id);
                    buttonText.append(artist.name).append(", ");
                }
            }

            // Update UI Button Text
            if (!selectedArtistIds.isEmpty()) {
                String text = buttonText.substring(0, buttonText.length() - 2); // Remove last comma
                btnSelectArtist.setText(text);
            } else {
                btnSelectArtist.setText("Select Artists (0 selected)");
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addTicketRow() {
        View ticketView = getLayoutInflater().inflate(R.layout.item_ticket_type, null);
        View btnRemove = ticketView.findViewById(R.id.btnRemoveTicket);
        btnRemove.setOnClickListener(v -> layoutTicketContainer.removeView(ticketView));
        layoutTicketContainer.addView(ticketView);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void performCreateEvent() {
        // 1. EXTRACT SIMPLE STRINGS
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocationName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        if (title.isEmpty() || startTime.isEmpty()) {
            etTitle.setError("Required"); return;
        }

        // 2. CONVERT STRINGS TO REQUEST BODY (text/plain)
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody locPart = RequestBody.create(MediaType.parse("text/plain"), location);
        RequestBody addrPart = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody startPart = RequestBody.create(MediaType.parse("text/plain"), startTime);
        RequestBody endPart = RequestBody.create(MediaType.parse("text/plain"), endTime);

        // Category ID (Int -> String -> RequestBody)
        int pos = spinnerCategory.getSelectedItemPosition();
        String catIdStr = String.valueOf(categoryIds[pos]);
        RequestBody catPart = RequestBody.create(MediaType.parse("text/plain"), catIdStr);

        // 3. PREPARE COMPLEX DATA (Tickets & Artists)
        // Gather Tickets from UI Loop
        List<CreateEventReq.TicketConfig> ticketsList = new ArrayList<>();
        for(int i = 0; i < layoutTicketContainer.getChildCount(); i++) {
            View row = layoutTicketContainer.getChildAt(i);
            EditText etName = row.findViewById(R.id.etTicketName);
            EditText etPrice = row.findViewById(R.id.etTicketPrice);
            EditText etQty = row.findViewById(R.id.etTicketQty);
            EditText etDesc = row.findViewById(R.id.etTicketDesc);

            String tName = etName.getText().toString().trim();
            String tPriceStr = etPrice.getText().toString().trim();
            String tQtyStr = etQty.getText().toString().trim();
            String tDesc = etDesc.getText().toString().trim();

            if (!tName.isEmpty() && !tPriceStr.isEmpty() && !tQtyStr.isEmpty()) {
                ticketsList.add(new CreateEventReq.TicketConfig(
                        tName, Double.parseDouble(tPriceStr), Integer.parseInt(tQtyStr), tDesc
                ));
            }
        }

        if (ticketsList.isEmpty()) {
            Toast.makeText(this, "Add a ticket!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- MAGIC PART: CONVERT LISTS TO JSON STRING ---

        Gson gson = new Gson();
        String ticketsJsonString = gson.toJson(ticketsList);       // "[{name:'VIP'...}]"
        String artistsJsonString = gson.toJson(selectedArtistIds); // "[1, 2]"

        RequestBody ticketsPart = RequestBody.create(MediaType.parse("application/json"), ticketsJsonString);
        RequestBody artistsPart = RequestBody.create(MediaType.parse("application/json"), artistsJsonString);

        // 4. PREPARE IMAGE FILE
        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            try {
                // Convert URI -> Real File
                File file = FileUtil.from(this, selectedImageUri);

                // Create Body
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);

                // Wrap in Multipart (Key name "thumbnail" matches Node.js upload.single('thumbnail'))
                imagePart = MultipartBody.Part.createFormData("thumbnail", file.getName(), reqFile);
            } catch (Exception e) {
                Toast.makeText(this, "Image Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

//         5. GET TOKEN
//         (Using hardcoded token for testing, or use SharedPreferences)

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        if (token == null) {
            Toast.makeText(this, "You are not logged in",Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(this, LoginActivity.class);
             startActivity(intent);
             finish();
        }
        String authHeader = "Bearer " + token;

//        6 Call API

        ApiService apiService = ApiClient.getService();
        Call<ApiResponse> call = apiService.createEventMultipart(
                authHeader,
                titlePart, descPart, locPart, addrPart, startPart, endPart, catPart,
                ticketsPart, artistsPart,
                imagePart // Can be null
        );

        btnCreate.setEnabled(false);
        btnCreate.setText("Uploading...");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnCreate.setEnabled(true);
                btnCreate.setText("Create Event");
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrganizerCreateEventActivity.this, "Success! ID: " + response.body().eventId, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("API_FAIL", err);
                        Toast.makeText(OrganizerCreateEventActivity.this, "Failed: " + err, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {}
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnCreate.setEnabled(true);
                btnCreate.setText("Create Event");
                Toast.makeText(OrganizerCreateEventActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etLocationName = findViewById(R.id.etLocationName);
        etAddress = findViewById(R.id.etAddress);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        imgThumbnailPreview = findViewById(R.id.imgThumbnailPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnCreate = findViewById(R.id.btnCreateEvent);
        layoutTicketContainer = findViewById(R.id.layoutTicketContainer);
        btnAddTicketRow = findViewById(R.id.btnAddTicketRow);

        // --- ADDED THIS LINE ---
        btnSelectArtist = findViewById(R.id.btnSelectArtists);
    }
}