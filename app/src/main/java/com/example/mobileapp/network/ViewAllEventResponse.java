package com.example.mobileapp.network;

import com.example.mobileapp.data.model.Event;

import java.util.List;

public class ViewAllEventResponse {
    private boolean success;
    private List<Event> data;

    public boolean isSuccess() { return success; }
    public List<Event> getData() { return data; }
}
