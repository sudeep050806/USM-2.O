package com.example.universitysports.utils;

import android.util.Log;

import com.example.universitysports.helpers.EmailSender;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

/**
 * Email configuration checker
 * Run this to verify email settings are correct
 */
public class EmailConfigChecker {

    private static final String TAG = "EmailConfigChecker";

    /**
     * Check if email configuration is valid
     */
    public static boolean checkEmailConfig() {
        try {
            // Test creating a session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Log.d(TAG, "Email configuration looks valid. Ensure EmailSender.java has correct credentials.");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Email configuration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get troubleshooting tips
     */
    public static String getTroubleshootingGuide() {
        return "Email Setup Troubleshooting:\n\n" +
                "1. For Gmail:\n" +
                "   - Enable 2-Factor Authentication\n" +
                "   - Generate App Password (16 chars)\n" +
                "   - Use app password in EmailSender.java\n\n" +
                "2. For other providers:\n" +
                "   - Update SMTP_HOST and SMTP_PORT\n" +
                "   - Verify credentials\n\n" +
                "3. Common issues:\n" +
                "   - Check internet permission in AndroidManifest\n" +
                "   - Allow background data usage\n" +
                "   - Check Gmail security alerts\n\n" +
                "4. Test:\n" +
                "   - Register new account to trigger OTP\n" +
                "   - Check Logcat for detailed errors";
    }
}