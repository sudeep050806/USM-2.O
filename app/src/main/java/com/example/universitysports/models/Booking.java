package com.example.universitysports.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Booking model for ground reservations
 */
public class Booking implements Parcelable {
    private int id;
    private int userId;
    private int groundId;
    private String bookingDate; // Format: YYYY-MM-DD
    private String timeSlot;    // Format: "HH:MM - HH:MM"
    private String status;      // "Pending", "Confirmed", "Cancelled"
    private String groundName;
    private String userName;
    private long createdAt;

    public Booking() {
        this.createdAt = System.currentTimeMillis();
        this.status = "Pending";
    }

    public Booking(int userId, int groundId, String bookingDate, String timeSlot) {
        this.userId = userId;
        this.groundId = groundId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.createdAt = System.currentTimeMillis();
        this.status = "Pending";
    }

    protected Booking(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        groundId = in.readInt();
        bookingDate = in.readString();
        timeSlot = in.readString();
        status = in.readString();
        groundName = in.readString();
        userName = in.readString();
        createdAt = in.readLong();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getGroundId() { return groundId; }
    public void setGroundId(int groundId) { this.groundId = groundId; }
    
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getGroundName() { return groundName; }
    public void setGroundName(String groundName) { this.groundName = groundName; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(groundId);
        dest.writeString(bookingDate);
        dest.writeString(timeSlot);
        dest.writeString(status);
        dest.writeString(groundName);
        dest.writeString(userName);
        dest.writeLong(createdAt);
    }
}