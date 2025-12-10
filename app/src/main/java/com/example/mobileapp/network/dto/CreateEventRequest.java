package com.example.mobileapp.network.dto;

import java.util.List;

public class CreateEventRequest {
    public String title;
    public String description;
    public String start_time;
    public String end_time;
    public String location_name;
    public String address;
    public String thumbnail_url;
    public String video_url;
    public int category_id;
    public List<TicketTypeDto> ticket_types;
}