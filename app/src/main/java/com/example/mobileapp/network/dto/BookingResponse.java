//package com.example.mobileapp.network.dto;
//
//public class BookingResponse {
//    public boolean success;
//    public String message;
//    public BookingData data;
//
//    public boolean isSuccess() { return success; }
//    public BookingData getData() { return data; }
//
//    public static class BookingData {
//        public int booking_id;
//        public double total_amount;
//        public String payment_status;
//        public BookingInfo booking_info;
//        public TicketInfo[] tickets;
//
//        public int getBookingId() { return booking_id; }
//        public double getTotalAmount() { return total_amount; }
//    }
//
//    public static class BookingInfo {
//        public int booking_id;
//        public int user_id;
//        public int event_id;
//        public double total_amount;
//        public String payment_status;
//        public String created_at;
//        public String event_title;
//        public String start_time;
//        public String location_name;
//    }
//
//    public static class TicketInfo {
//        public int ticket_id;
//        public String qr_code;
//        public boolean is_used;
//        public String ticket_type_name;
//        public double price;
//    }
//}
package com.example.mobileapp.network.dto;

public class BookingResponse {
    public boolean success;
    public String message;
    public BookingData data;

    public boolean isSuccess() { return success; }
    public BookingData getData() { return data; }

    public static class BookingData {
        public int booking_id;
        public Object total_amount; // có thể là number hoặc string
        public String payment_status;
        public BookingInfo booking_info;
        public TicketInfo[] tickets;

        public int getBookingId() { return booking_id; }
        public double getTotalAmount() {
            if (total_amount instanceof Number) {
                return ((Number) total_amount).doubleValue();
            } else if (total_amount instanceof String) {
                return Double.parseDouble((String) total_amount);
            }
            return 0;
        }
    }

    public static class BookingInfo {
        public int booking_id;
        public int user_id;
        public int event_id;
        public String total_amount; // server trả về string "1000000.00"
        public String payment_status;
        public String created_at;
        public String event_title;
        public String start_time;
        public String location_name;
    }

    public static class TicketInfo {
        public int ticket_id;
        public String qr_code;
        public int is_used; // server trả về 0 hoặc 1
        public String ticket_type_name;
        public String price; // server trả về string "1000000.00"

        public boolean isUsed() { return is_used == 1; }
    }
}
