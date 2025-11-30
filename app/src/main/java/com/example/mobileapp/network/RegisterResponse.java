package com.example.mobileapp.network;

public class RegisterResponse {
    private boolean success;
    private String message;
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private int id;
        private String full_name;
        private String email;

        public int getId() { return id; }
        public String getFullName() { return full_name; }
        public String getEmail() { return email; }
    }
}
