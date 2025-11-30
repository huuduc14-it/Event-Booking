package com.example.mobileapp.network;

import com.example.mobileapp.data.model.Ticket;
import com.example.mobileapp.data.model.User;

import java.util.List;

public class ProfileResponse {
    private boolean success;
    private User user;
    private List<Ticket> tickets;

    // getters & setters
    public boolean isSuccess() { return success; }
    public User getUser() { return user; }
    public List<Ticket> getTickets() { return tickets; }
}

