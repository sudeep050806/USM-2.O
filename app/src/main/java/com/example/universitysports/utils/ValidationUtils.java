package com.example.universitysports.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextInputLayout;
import android.widget.Toast;

/**
 * ValidationUtils - Comprehensive input validation
 */
public class ValidationUtils {

    private ValidationUtils() {}

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && 
               Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate phone number (10-15 digits)
     */
    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && 
               phone.length() >= 10 && 
               phone.length() <= 15 &&
               Patterns.PHONE.matcher(phone).matches();
    }

    /**
     * Validate password strength (min 6, with letter & number)
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }
        // At least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }

    /**
     * Check if passwords match
     */
    public static boolean doPasswordsMatch(String p1, String p2) {
        return p1 != null && p1.equals(p2);
    }

    /**
     * Validate required field
     */
    public static boolean isRequiredField(String value) {
        return TextUtils.isEmpty(value);
    }

    /**
     * Set error on TextInputLayout
     */
    public static void setError(TextInputLayout til, String message) {
        if (til != null) {
            til.setError(message);
            til.requestFocus();
        }
    }

    /**
     * Clear error from TextInputLayout
     */
    public static void clearError(TextInputLayout til) {
        if (til != null) {
            til.setError(null);
        }
    }

    /**
     * Show toast message
     */
    public static void showToast(android.content.Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show long toast
     */
    public static void showLongToast(android.content.Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}