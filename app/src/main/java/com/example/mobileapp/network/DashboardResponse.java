package com.example.mobileapp.network;

import com.example.mobileapp.data.model.DashboardEvent;
import java.util.List;

public class DashboardResponse {
    public boolean success;
    public List<DashboardEvent> data;
    public String message;
}