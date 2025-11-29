package com.example.mobileapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Artist {
    @SerializedName("artist_id")
    public int id;

    @SerializedName("name")
    public String name;

    // We override toString() so the Dialog shows the name, not the memory address
    @Override
    public String toString() {
        return name;
    }
}