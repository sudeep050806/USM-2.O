package com.example.universitysports.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * OTP model for storing verification codes
 */
public class OTP implements Parcelable {
    private int id;
    private String email;
    private String otp;
    private long expiryTime; // timestamp in milliseconds
    private int attempts;
    private boolean isUsed;

    public OTP() {}

    public OTP(String email, String otp, long expiryTime) {
        this.email = email;
        this.otp = otp;
        this.expiryTime = expiryTime;
        this.attempts = 0;
        this.isUsed = false;
    }

    protected OTP(Parcel in) {
        id = in.readInt();
        email = in.readString();
        otp = in.readString();
        expiryTime = in.readLong();
        attempts = in.readInt();
        isUsed = in.readByte() != 0;
    }

    public static final Creator<OTP> CREATOR = new Creator<OTP>() {
        @Override
        public OTP createFromParcel(Parcel in) {
            return new OTP(in);
        }

        @Override
        public OTP[] newArray(int size) {
            return new OTP[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    
    public long getExpiryTime() { return expiryTime; }
    public void setExpiryTime(long expiryTime) { this.expiryTime = expiryTime; }
    
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    /**
     * Check if OTP is expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    /**
     * Check if max attempts (3) exceeded
     */
    public boolean isMaxAttemptsExceeded() {
        return attempts >= 3;
    }

    /**
     * Increment attempt counter
     */
    public void incrementAttempts() {
        this.attempts++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(otp);
        dest.writeLong(expiryTime);
        dest.writeInt(attempts);
        dest.writeByte((byte) (isUsed ? 1 : 0));
    }
}