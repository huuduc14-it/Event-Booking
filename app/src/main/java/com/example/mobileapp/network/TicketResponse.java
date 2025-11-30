package com.example.mobileapp.network;

import com.example.mobileapp.data.model.Ticket;

import java.util.List;

public class TicketResponse {
    private boolean success;
    private List<Ticket> tickets;

    public boolean isSuccess() {
        return success;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}

