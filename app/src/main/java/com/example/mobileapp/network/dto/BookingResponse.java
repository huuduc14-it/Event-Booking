package com.example.mobileapp.network.dto;

public class BookingResponse {
    public boolean success;
    public String message;
    public BookingData data;

    public static class BookingData {
        public int booking_id;
        public double total_amount;
        public String payment_status;
        public BookingInfo booking_info;
        public TicketInfo[] tickets;
    }

    public static class BookingInfo {
        public int booking_id;
        public int user_id;
        public int event_id;
        public double total_amount;
        public String payment_status;
        public String created_at;
        public String event_title;
        public String start_time;
        public String location_name;
    }

    public static class TicketInfo {
        public int ticket_id;
        public String qr_code;
        public boolean is_used;
        public String ticket_type_name;
        public double price;
    }
}
