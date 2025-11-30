package com.example.mobileapp.network;

import com.example.mobileapp.data.model.Event;

public class EventDetailResponse {
    private boolean success;
    private Event data;

    public boolean isSuccess() { return success; }
    public Event getData() { return data; }
}

