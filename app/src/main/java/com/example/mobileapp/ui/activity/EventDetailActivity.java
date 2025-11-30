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

import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvTitle, tvLocation, tvTime, tvDescription;
    ImageView imgThumbnail;

    int eventId; // nhận từ intent
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
        tvTime = findViewById(R.id.tvEventTime);
        tvDescription = findViewById(R.id.tvEventDescription);

        // Nhận eventId từ Intent
        eventId = getIntent().getIntExtra("event_id", -1);

        if (eventId == -1) {
            finish();
            return;
        }

        // Load dữ liệu
        loadEventDetail(eventId);
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
                        tvTime.setText(event.getStart_time().replace("T", " ").replace(".000Z", ""));
                        tvDescription.setText(event.getDescription());
//                        tvTitle.setText(title);
//                        tvLocation.setText(location);
//                        tvTime.setText(startTime.replace("T", " ").replace(".000Z", ""));
//                        tvDescription.setText(description);

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

