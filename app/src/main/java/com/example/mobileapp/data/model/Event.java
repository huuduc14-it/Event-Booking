package com.example.mobileapp.data.model;

public class Event {
    private int event_id;
    private String title;
    private String thumbnail_url;
    private int user_id;
    private int category_id;
    private String description;
    private String location_name;
    private String address;
    private String start_time;
    private String end_time;
    private String video_url;
    
    public int getEvent_id() { return event_id; }
    public String getTitle() { return title; }
    public String getThumbnail_url() { return thumbnail_url; }
    public String getDescription() { return description; }
    public String getLocationName() { return location_name; }
    public String getAddress() { return address; }
    public String getStart_time() { return start_time; }
    public String getEnd_time() { return end_time; }
    public String getVideo_url() { return video_url; }
    public int getCategory_id() { return category_id; }
    public int getUser_id() { return user_id; }
}

