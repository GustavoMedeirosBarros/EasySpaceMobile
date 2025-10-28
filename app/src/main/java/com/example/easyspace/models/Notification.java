package com.example.easyspace.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification implements Serializable {
    private String id;
    private String title;
    private String message;
    private String type;
    private long timestamp;
    private boolean read;

    public Notification() {
    }

    public Notification(String title, String message, String type, long timestamp, boolean read) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getFormattedTime() {
        long diff = System.currentTimeMillis() - timestamp;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " dia" + (days > 1 ? "s" : "") + " atrás";
        } else if (hours > 0) {
            return hours + " hora" + (hours > 1 ? "s" : "") + " atrás";
        } else if (minutes > 0) {
            return minutes + " minuto" + (minutes > 1 ? "s" : "") + " atrás";
        } else {
            return "Agora";
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
