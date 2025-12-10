package com.example.mobileapp.network.dto;

public class OrganizerProfileUpdateRequest {
    public String organization_name;
    public String full_name;
    public String phone;
    public String avatar_url;

    public OrganizerProfileUpdateRequest(String orgName, String fullName, String phone, String avatarUrl) {
        this.organization_name = orgName;
        this.full_name = fullName;
        this.phone = phone;
        this.avatar_url = avatarUrl;
    }
}