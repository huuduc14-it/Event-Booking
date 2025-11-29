package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateEventReq {
    public String title;
    public String description;

    @SerializedName("location_name")
    public String locationName;

    public String address;

    @SerializedName("start_time")
    public String startTime; // Format: "2025-06-15 18:00:00"

    @SerializedName("end_time")
    public String endTime;

    @SerializedName("thumbnail_url")
    public String thumbnailUrl;

    @SerializedName("category_id")
    public int categoryId;

    @SerializedName("artist_ids")
    public List<Integer> artistIds;

    // The Nested List of Ticket Types
    public List<TicketConfig> tickets;

    // --- SUB CLASS FOR TICKETS ---
    public static class TicketConfig {
        public String name;
        public double price;

        @SerializedName("total_quantity")
        public int totalQuantity;

        public String description;

        // Constructor for easy creation
        public TicketConfig(String name, double price, int totalQuantity, String description) {
            this.name = name;
            this.price = price;
            this.totalQuantity = totalQuantity;
            this.description = description;
        }
    }
}