package com.example.universitysports.helpers;

import java.util.Random;

/**
 * AppConfig - Centralized configuration constants
 */
public class AppConfig {

    // OTP Configuration
    public static final int OTP_LENGTH = 6;
    public static final long OTP_EXPIRY_TIME = 2 * 60 * 1000; // 2 minutes in ms
    public static final long OTP_RESEND_COOLDOWN = 30 * 1000; // 30 seconds
    public static final int MAX_OTP_ATTEMPTS = 3;

    // Time slots
    public static final String[] TIME_SLOTS = {
            "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00"
    };

    // Booking rules
    public static final long MIN_BOOKING_ADVANCE_HOURS = 2;
    public static final long MAX_BOOKING_DAYS_AHEAD = 30;

    /**
     * Generate random 6-digit OTP
     */
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Debug mode
    public static final boolean DEBUG_MODE = true;
}