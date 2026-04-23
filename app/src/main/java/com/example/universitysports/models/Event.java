package com.example.universitysports.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Event model for sports events
 */
public class Event implements Parcelable {
    private int id;
    private String title;
    private String description;
    private String eventDate;    // Format: YYYY-MM-DD
    private String eventTime;    // Format: HH:MM
    private String venue;
    private String organizer;
    private int maxParticipants;
    private int registeredCount;
    private boolean isActive;

    public Event() {
        this.isActive = true;
        this.registeredCount = 0;
    }

    public Event(String title, String description, String eventDate, String eventTime,
                 String venue, String organizer, int maxParticipants) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.organizer = organizer;
        this.maxParticipants = maxParticipants;
        this.isActive = true;
        this.registeredCount = 0;
    }

    protected Event(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        eventDate = in.readString();
        eventTime = in.readString();
        venue = in.readString();
        organizer = in.readString();
        maxParticipants = in.readInt();
        registeredCount = in.readInt();
        isActive = in.readByte() != 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public int getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(int registeredCount) { this.registeredCount = registeredCount; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
        dest.writeString(venue);
        dest.writeString(organizer);
        dest.writeInt(maxParticipants);
        dest.writeInt(registeredCount);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }
}