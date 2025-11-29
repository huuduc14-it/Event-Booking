package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingHistoryItem {

    @SerializedName("booking_id")
    public int bookingId;

    @SerializedName("event_title")
    public String eventTitle;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("location_name")
    public String locationName;

    @SerializedName("ticket_name")
    public String ticketName; // e.g. "VIP"

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("total_amount")
    public double totalAmount;

    // IMPORTANT: Your backend must return this string!
    // It is the UUID string (e.g. "1-5-a1b2...") used to generate the image.
    @SerializedName("qr_code")
    public String qrCode;
}