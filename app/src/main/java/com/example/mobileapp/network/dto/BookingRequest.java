package com.example.mobileapp.network.dto;

import java.util.List;

public class BookingRequest {
    public int event_id;
    public List<BookingItem> items;

    public BookingRequest() {}

    public BookingRequest(int event_id, List<BookingItem> items) {
        this.event_id = event_id;
        this.items = items;
    }

    public static class BookingItem {
        public int ticket_type_id;
        public int quantity;

        public BookingItem() {}

        public BookingItem(int ticket_type_id, int quantity) {
            this.ticket_type_id = ticket_type_id;
            this.quantity = quantity;
        }
    }
}
