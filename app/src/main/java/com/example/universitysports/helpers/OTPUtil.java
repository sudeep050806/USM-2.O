package com.example.universitysports.helpers;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * OTPUtil - Advanced OTP generation, hashing, and validation
 * Implements security best practices for OTP system
 */
public class OTPUtil {

    private static final String TAG = "OTPUtil";
    private static final String OTP_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 16 bytes salt

    /**
     * Generate cryptographically secure 6-digit OTP
     */
    public static String generateSecureOTP() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000); // 6-digit number
        return String.valueOf(otp);
    }

    /**
     * Hash OTP with salt for secure storage
     * Returns format: "salt:hashedOTP" (base64 encoded)
     */
    public static String hashOTP(String otp, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(OTP_ALGORITHM);
            String combined = salt + otp;
            byte[] hash = digest.digest(combined.getBytes());
            String base64Hash = Base64.encodeToString(hash, Base64.NO_WRAP);
            return salt + ":" + base64Hash;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "SHA-256 not available", e);
            return null;
        }
    }

    /**
     * Verify OTP against stored hash
     */
    public static boolean verifyOTP(String enteredOtp, String storedHash) {
        if (storedHash == null || !storedHash.contains(":")) {
            return false;
        }

        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String expectedHash = parts[1];
        String computedHash = hashOTP(enteredOtp, salt);

        return expectedHash.equals(computedHash);
    }

    /**
     * Generate random salt for OTP hashing
     */
    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    /**
     * Calculate expiry time based on duration
     */
    public static long calculateExpiry(long durationMillis) {
        return System.currentTimeMillis() + durationMillis;
    }

    /**
     * Check if OTP is expired
     */
    public static boolean isExpired(long expiryTime) {
        return System.currentTimeMillis() > expiryTime;
    }

    /**
     * Format time remaining as human-readable string
     */
    public static String formatTimeRemaining(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * Validate OTP format (6 digits)
     */
    public static boolean isValidOTPFormat(String otp) {
        return otp != null && otp.matches("\\d{6}");
    }

    /**
     * Calculate remaining attempts message
     */
    public static String getAttemptsMessage(int attempts, int maxAttempts) {
        int remaining = maxAttempts - attempts;
        if (remaining <= 1) {
            return "⚠️ " + remaining + " attempt remaining!";
        }
        return remaining + " attempts remaining";
    }
}