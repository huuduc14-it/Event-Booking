package com.example.mobileapp.network.dto;

public class TicketTypeDto {
    public String name;
    public double price;
    public int quantity;
    public String description;

    public TicketTypeDto(String name, double price, int quantity, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }
}
