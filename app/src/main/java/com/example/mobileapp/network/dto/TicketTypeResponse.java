package com.example.mobileapp.network.dto;

public class TicketTypeResponse {
    public boolean success;
    public TicketType[] data;

    public static class TicketType {
        public int ticket_type_id;
        public String name;
        public double price;
        public int total_quantity;
        public int remaining;
        public String description;
    }
}
