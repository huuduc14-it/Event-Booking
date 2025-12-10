package com.example.mobileapp.network.dto;

public class CheckInResponse {
    public boolean success;
    public String message;
    public TicketDetail ticket;

    public static class TicketDetail {
        public int ticket_id;
        public String qr_code;
        public boolean is_used;
        public int event_id;
        public String event_title;
        public String start_time;
        public String full_name;
        public String email;
        public String phone;
        public String ticket_type_name;
        public double price;
    }
}