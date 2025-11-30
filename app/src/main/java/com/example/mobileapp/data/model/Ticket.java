package com.example.mobileapp.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//public class Ticket {
//
//    private String eventName;
//    private String seat;
//    private String date; // ngày giờ sự kiện
//    private String price;
//
//    public Ticket(String eventName, String seat, String date, String price) {
//        this.eventName = eventName;
//        this.seat = seat;
//        this.date = date;
//        this.price = price;
//    }
//
//    public String getEventName() { return eventName; }
//    public String getSeat() { return seat; }
//    public String getDate() { return date; }
//    public String getPrice() { return price; }
//
//    // method kiểm tra vé đã diễn ra chưa
//    public boolean isPast() {
//        if (date == null || date.isEmpty()) return false;
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date eventDate = sdf.parse(date);
//            Date now = new Date();
//            return now.after(eventDate); // true nếu sự kiện đã diễn ra
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
public class Ticket {

    private int ticket_id;
    private String qr_code;
    private int is_used;

    private int event_id;
    private String event_title;
    private String start_time;
    private String thumbnail_url;

    private String ticket_type_name;
    private String price;
    private String purchase_date;

    public int getTicketId() { return ticket_id; }
    public String getQrCode() { return qr_code; }
    public boolean isUsed() { return is_used == 1; }

    public int getEventId() { return event_id; }
    public String getEventTitle() { return event_title; }
    public String getStartTime() { return start_time; }
    public String getThumbnailUrl() { return thumbnail_url; }

    public String getTicketTypeName() { return ticket_type_name; }
    public String getPrice() { return price; }
    public String getPurchaseDate() { return purchase_date; }

    // Kiểm tra đã diễn ra chưa
    public boolean isPast() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date eventDate = sdf.parse(start_time);
            return new Date().after(eventDate);
        } catch (Exception e) {
            return false;
        }
    }
}

