package com.example.universitysports.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.universitysports.models.User;

/**
 * SessionManager - Handles user login sessions with role-based access
 * Uses SharedPreferences for persistent login state
 */
public class SessionManager {

    private static final String PREF_NAME = "SportsAppSession";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_VERIFIED = "is_verified";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session after successful verification
     */
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putBoolean(KEY_IS_VERIFIED, user.isVerified());
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * Get logged in user details
     */
    public User getUser() {
        User user = new User();
        user.setId(pref.getInt(KEY_USER_ID, -1));
        user.setName(pref.getString(KEY_USER_NAME, ""));
        user.setEmail(pref.getString(KEY_USER_EMAIL, ""));
        user.setRole(pref.getString(KEY_USER_ROLE, "student"));
        user.setVerified(pref.getBoolean(KEY_IS_VERIFIED, false));
        return user;
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get user role (student/teacher/admin)
     */
    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "student");
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return "admin".equals(getUserRole());
    }

    /**
     * Check if user is teacher/staff
     */
    public boolean isTeacher() {
        return "teacher".equals(getUserRole());
    }

    /**
     * Check if user is student
     */
    public boolean isStudent() {
        return "student".equals(getUserRole());
    }

    /**
     * Logout user - clear all session data
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update user verification status
     */
    public void setVerified(boolean verified) {
        editor.putBoolean(KEY_IS_VERIFIED, verified);
        editor.apply();
    }
}