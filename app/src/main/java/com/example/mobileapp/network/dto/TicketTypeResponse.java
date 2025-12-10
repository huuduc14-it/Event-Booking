package com.example.mobileapp.network.dto;

import java.util.List;

public class TicketTypeResponse {
    public boolean success;
    public List<TicketType> data;

    public boolean isSuccess() { return success; }
    public List<TicketType> getData() { return data; }

    public static class TicketType {
        public int ticket_type_id;
        public String name;
        public double price;
        public int total_quantity;
        public int remaining;
        public String description;

        public int getTicketTypeId() { return ticket_type_id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getTotalQuantity() { return total_quantity; }
        public int getRemaining() { return remaining; }
        public String getDescription() { return description; }
    }
}