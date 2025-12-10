package com.example.mobileapp.network.dto;

public class BookingListResponse {
    public boolean success;
    public BookingItem[] data;

    public static class BookingItem {
        public int booking_id;
        public double total_amount;
        public String payment_status;
        public String created_at;
        public int event_id;
        public String event_title;
        public String start_time;
        public String thumbnail_url;
        public int ticket_count;
    }
}