package com.example.mobileapp.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.ApiResponse;
import com.example.mobileapp.data.model.TicketListResponse;
import com.example.mobileapp.data.model.TicketType;
import com.example.mobileapp.data.network.ApiClient;
import com.example.mobileapp.ui.adapter.ManageTicketAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerManageTicketActivity extends AppCompatActivity {

    private  String token;

    private RecyclerView recyclerView;
    private ManageTicketAdapter adapter;
    private int  eventId = 6;
    private List<TicketType> ticketList = new ArrayList<>();
//    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_manage_tickets);

//         1. Get Event ID (Assuming passed from previous screen)
        eventId = getIntent().getIntExtra("event_id", 1);

//         2. Get Token
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        token = prefs.getString("auth_token", null);

        // 3. Setup Recycler
        recyclerView = findViewById(R.id.rvTicketList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ManageTicketAdapter(ticketList, new ManageTicketAdapter.OnTicketActionListener() {
            @Override
            public void onEdit(TicketType ticket) {
                showDialog(ticket); // Edit Mode
            }
            @Override
            public void onDelete(TicketType ticket) {
                deleteTicketAPI(ticket.id);
            }
        });
        recyclerView.setAdapter(adapter);

        // 4. Load Data
        loadTickets();

        // 5. FAB Listener
        findViewById(R.id.fabAddTicket).setOnClickListener(v -> showDialog(null)); // Add Mode
    }

    private void loadTickets() {
        // Passed via Intent

        ApiClient.getService().getEventTickets(eventId, token).enqueue(new Callback<TicketListResponse>() {
            @Override
            public void onResponse(Call<TicketListResponse> call, Response<TicketListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // LOG THE DATA SIZE
                    Log.d("API_DEBUG", "Found " + response.body().data.size() + " tickets");

                    ticketList.clear();
                    ticketList.addAll(response.body().data);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_FAIL", "Error Code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<TicketListResponse> call, Throwable t) {
                Log.e("API_FAIL", "Network Error: " + t.getMessage());
            }
        });
    }

    private void showDialog(@Nullable TicketType ticketToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_ticket_input, null);

        EditText etName = view.findViewById(R.id.etDialogName);
        EditText etPrice = view.findViewById(R.id.etDialogPrice);
        EditText etQty = view.findViewById(R.id.etDialogQty);

        if (ticketToEdit != null) {
            etName.setText(ticketToEdit.name);
            etPrice.setText(String.valueOf(ticketToEdit.price));
            etQty.setText(String.valueOf(ticketToEdit.totalQuantity));
            builder.setTitle("Edit Ticket");
        } else {
            builder.setTitle("Add New Ticket");
        }

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // 1. Gather Data
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String qtyStr = etQty.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Create Object
            TicketType newTicket = new TicketType();
            newTicket.name = name;
            newTicket.price = Double.parseDouble(priceStr);
            newTicket.totalQuantity = Integer.parseInt(qtyStr);

            // --- FIX 1: SET THE EVENT ID ---
            // Without this, the backend doesn't know where to put the ticket!
            newTicket.event_id = eventId;

            // --- FIX 2: PREVENT "UNDEFINED" CRASH ---
            // Your dialog has no description, so we MUST set a default string.
            // If we leave it null, Gson might skip it, causing the backend crash.
            newTicket.description = "Standard Ticket";

            // 3. Send to API
            if (ticketToEdit != null) {
                // FIX 3: Use ticketToEdit.id (Ticket ID), NOT event_id
                updateTicketAPI(ticketToEdit.id, newTicket);
            } else {
                addTicketAPI(newTicket);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addTicketAPI(TicketType ticket) {
        ApiClient.getService().addTicket("Bearer " + token, ticket).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrganizerManageTicketActivity.this, "Added!", Toast.LENGTH_SHORT).show();
                    loadTickets();
                } else {
                    // --- NEW DEBUG CODE ---
                    try {
                        // Extract the error message from the server
                        String errorBody = response.errorBody().string();
                        Log.e("API_FAIL", "Error Code: " + response.code());
                        Log.e("API_FAIL", "Error Body: " + errorBody);

                        // Show specific error in Toast
                        Toast.makeText(OrganizerManageTicketActivity.this, "Error " + response.code() + ": Check Logcat", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_FAIL", "Network Error: " + t.getMessage());
                Toast.makeText(OrganizerManageTicketActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateTicketAPI(int id, TicketType ticket) {
        ApiClient.getService().updateTicket("Bearer " + token, id, ticket).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrganizerManageTicketActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                    loadTickets();
                } else {
                    // READ THE REAL ERROR MESSAGE FROM BACKEND
                    try {
                        String errorBody = response.errorBody().string();
                        // Assuming errorBody is JSON: {"success":false, "message":"Cannot reduce..."}
                        // For quick debug, just toast the whole string or check logs
                        Log.e("API_FAIL", errorBody);
                        Toast.makeText(OrganizerManageTicketActivity.this, "Failed: Check Logcat", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) { /* ... */ }
        });
    }

    private void deleteTicketAPI(int id) {
        ApiClient.getService().deleteTicket("Bearer " + token, id).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrganizerManageTicketActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadTickets();
                } else {
                    // This handles the "Cannot delete because tickets sold" error
                    Toast.makeText(OrganizerManageTicketActivity.this, "Cannot delete: Tickets likely sold", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {}
        });
    }
}