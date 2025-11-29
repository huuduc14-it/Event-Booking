package com.example.mobileapp.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MyTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // 1. Get Data from Intent (Passed from the previous list screen)
        // You should pass these when the user clicks a ticket in their history
        String qrCodeString = getIntent().getStringExtra("QR_CODE_STRING");
        String eventTitle = getIntent().getStringExtra("EVENT_TITLE");
        String ticketName = getIntent().getStringExtra("TICKET_NAME");

        // Fallback for testing if you run this screen directly
        if (qrCodeString == null) qrCodeString = "EVENT-1-TICKET-999-TEST-ID";

        // 2. Setup UI
        TextView tvTitle = findViewById(R.id.tvEventTitle);
        TextView tvName = findViewById(R.id.tvTicketType);
        ImageView imgQr = findViewById(R.id.imgQrCode);

        tvTitle.setText(eventTitle != null ? eventTitle : "My Event");
        tvName.setText(ticketName != null ? ticketName : "General Admission");

        // 3. GENERATE QR CODE
        try {
            generateQRCode(qrCodeString, imgQr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateQRCode(String text, ImageView imageView) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            // Create a matrix of pixels (Black/White) for the text
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);

            // Convert matrix to Bitmap Image
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            // Set image to view
            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}