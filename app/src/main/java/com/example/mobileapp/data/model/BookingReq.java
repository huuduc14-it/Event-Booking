package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingReq {

    @SerializedName("event_id")
    public int eventId;

    @SerializedName("items")
    public List<BookingItem> items;

    // Inner class for the items array
    public static class BookingItem {
        @SerializedName("ticket_type_id")
        public int ticketTypeId;

        @SerializedName("quantity")
        public int quantity;

        public BookingItem(int ticketTypeId, int quantity) {
            this.ticketTypeId = ticketTypeId;
            this.quantity = quantity;
        }
    }
}