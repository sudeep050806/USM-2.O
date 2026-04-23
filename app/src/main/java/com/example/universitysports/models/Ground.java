package com.example.universitysports.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ground model representing sports facilities
 */
public class Ground implements Parcelable {
    private int id;
    private String name;
    private String location;
    private String description;
    private String sportType; // Football, Basketball, Cricket, etc.
    private int capacity;
    private String availableFrom; // HH:MM format (24hr)
    private String availableTo;   // HH:MM format (24hr)
    private boolean isActive;
    private String imageUrl; // Optional for future

    public Ground() {}

    public Ground(String name, String location, String description, String sportType, 
                  int capacity, String availableFrom, String availableTo) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.sportType = sportType;
        this.capacity = capacity;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.isActive = true;
    }

    protected Ground(Parcel in) {
        id = in.readInt();
        name = in.readString();
        location = in.readString();
        description = in.readString();
        sportType = in.readString();
        capacity = in.readInt();
        availableFrom = in.readString();
        availableTo = in.readString();
        isActive = in.readByte() != 0;
        imageUrl = in.readString();
    }

    public static final Creator<Ground> CREATOR = new Creator<Ground>() {
        @Override
        public Ground createFromParcel(Parcel in) {
            return new Ground(in);
        }

        @Override
        public Ground[] newArray(int size) {
            return new Ground[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }
    
    public String getAvailableTo() { return availableTo; }
    public void setAvailableTo(String availableTo) { this.availableTo = availableTo; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(sportType);
        dest.writeInt(capacity);
        dest.writeString(availableFrom);
        dest.writeString(availableTo);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(imageUrl);
    }
}