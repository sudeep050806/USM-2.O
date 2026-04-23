package com.example.universitysports.helpers;

import android.util.Log;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * EmailSender - Handles all email operations using JavaMail API
 * Uses ExecutorService for background thread execution
 * Supports OTP sending and booking notifications
 */
public class EmailSender {

    private static final String TAG = "EmailSender";
    
    // SMTP Configuration - ⚠️ UPDATE THESE VALUES ⚠️
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "your-email@gmail.com"; // CHANGE THIS
    private static final String EMAIL_PASSWORD = "your-app-password"; // CHANGE THIS
    
    // Thread pool for sending emails in background
    private final ExecutorService executorService;
    
    public EmailSender() {
        // Single thread executor for sequential email sending
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Send OTP email asynchronously
     */
    public void sendOTPEmail(final String recipientEmail, final String otp) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String subject = "University Sports - OTP Verification";
                    String body = buildOTPEmailBody(otp);
                    sendEmail(recipientEmail, subject, body);
                    Log.d(TAG, "OTP email sent to: " + recipientEmail);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send OTP email: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(final String recipientEmail, final String groundName,
                                         final String date, final String time, final String bookingId) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String subject = "Booking Confirmed - " + groundName;
                    String body = "Your ground booking has been confirmed!\n\n" +
                            "Booking ID: " + bookingId + "\n" +
                            "Ground: " + groundName + "\n" +
                            "Date: " + date + "\n" +
                            "Time: " + time + "\n\n" +
                            "📍 Please arrive 10 minutes before your scheduled time.\n" +
                            "🎫 Bring your student/staff ID card for verification.\n\n" +
                            "Regards,\n" +
                            "University Sports Management";
                    sendEmail(recipientEmail, subject, body);
                    Log.d(TAG, "Booking confirmation sent to: " + recipientEmail);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send booking confirmation: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Send booking cancellation email
     */
    public void sendCancellationEmail(final String recipientEmail, final String groundName, 
                                       final String date, final String time) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String subject = "Booking Cancelled - " + groundName;
                    String body = "Your ground booking has been cancelled.\n\n" +
                            "Ground: " + groundName + "\n" +
                            "Date: " + date + "\n" +
                            "Time: " + time + "\n\n" +
                            "We apologize for any inconvenience.\n\n" +
                            "Regards,\n" +
                            "University Sports Management";
                    sendEmail(recipientEmail, subject, body);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send cancellation email: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Send event registration confirmation
     */
    public void sendEventRegistrationEmail(final String recipientEmail, final String eventName,
                                            final String eventDate, final String eventTime) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String subject = "Event Registered - " + eventName;
                    String body = "You have successfully registered for the event!\n\n" +
                            "Event: " + eventName + "\n" +
                            "Date: " + eventDate + "\n" +
                            "Time: " + eventTime + "\n\n" +
                            "Please check the event details and arrive at the venue on time.\n\n" +
                            "Regards,\n" +
                            "University Sports Management";
                    sendEmail(recipientEmail, subject, body);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send event registration email: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Core email sending method
     */
    private void sendEmail(String recipient, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.timeout", "10000"); // 10 sec timeout
        props.put("mail.smtp.connectiontimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    /**
     * Build OTP email HTML body
     */
    private String buildOTPEmailBody(String otp) {
        return "Dear Student,\n\n" +
                "Welcome to University Sports Management System!\n\n" +
                "Your One-Time Password (OTP) for email verification is: **" + otp + "**\n\n" +
                "⏰ This OTP is valid for 2 minutes.\n" +
                "🔒 For security, do not share this OTP with anyone.\n\n" +
                "If you didn't request this OTP, please ignore this email.\n\n" +
                "Best regards,\n" +
                "University Sports Management Team";
    }

    /**
     * Shutdown executor service (call when app closes)
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}