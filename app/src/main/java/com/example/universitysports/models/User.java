package com.example.universitysports.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User model representing all users (Student, Teacher, Admin)
 */
public class User implements Parcelable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role; // "student", "teacher", "admin"
    private boolean isVerified;
    private long createdAt;

    public User() {
        this.createdAt = System.currentTimeMillis();
    }

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        password = in.readString();
        role = in.readString();
        isVerified = in.readByte() != 0;
        createdAt = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeString(role);
        dest.writeByte((byte) (isVerified ? 1 : 0));
        dest.writeLong(createdAt);
    }
}