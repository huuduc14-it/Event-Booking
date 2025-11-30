package com.example.mobileapp.network;

import com.example.mobileapp.data.model.Event;
import java.util.List;

public class EventSearchResponse {
    private String keyword;
    private String date;           // thêm để lưu ngày filter
    private Integer category_id;   // thêm để lưu category filter
    private int count;
    private List<Event> events;

    public String getKeyword() { return keyword; }
    public String getDate() { return date; }
    public Integer getCategory_id() { return category_id; }
    public int getCount() { return count; }
    public List<Event> getEvents() { return events; }
}
