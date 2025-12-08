package com.example.mobileapp.ui.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapp.R;

// Placeholder screen to extend later for editing ticket types per event
public class OrganizerManageTicketActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organizer_manage_tickets);
		Toast.makeText(this, "Quản lý vé sẽ được cập nhật", Toast.LENGTH_SHORT).show();
	}
}
