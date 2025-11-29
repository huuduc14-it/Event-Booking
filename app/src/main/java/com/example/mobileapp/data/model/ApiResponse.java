package com.example.mobileapp.data.model;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    // Map JSON "msg" -> Java "message"
    @SerializedName("msg")
    public String message;

    // Map JSON "newEventId" -> Java "eventId"
    @SerializedName("newEventId")
    public Integer eventId;
}