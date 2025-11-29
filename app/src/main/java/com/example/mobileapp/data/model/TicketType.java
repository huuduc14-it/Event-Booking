package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class TicketType {

    @SerializedName("ticket_type_id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("price")
    public double price;

    @SerializedName("total_quantity")
    public int totalQuantity;

    @SerializedName("remaining")
    public int remaining;

    // --- ADD THESE NEW FIELDS ---

    @SerializedName("event_id")
    public int event_id;  // Needed for "Add Ticket"

    @SerializedName("description")
    public String description; // Needed to prevent Backend Crash
    public int selectedQty = 0;
}