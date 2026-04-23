package com.example.universitysports.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.universitysports.models.Booking;
import com.example.universitysports.models.Event;
import com.example.universitysports.models.Ground;
import com.example.universitysports.models.OTP;
import com.example.universitysports.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * DBHelper - SQLite Database Helper with OTP support
 * Handles all database operations for the University Sports Management App
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "UniversitySports.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_OTP = "otp";
    private static final String TABLE_GROUNDS = "grounds";
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String TABLE_EVENTS = "events";

    // Users columns
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PHONE = "phone";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_ROLE = "role";
    private static final String COL_USER_VERIFIED = "is_verified";
    private static final String COL_USER_CREATED = "created_at";

    // OTP columns
    private static final String COL_OTP_ID = "id";
    private static final String COL_OTP_EMAIL = "email";
    private static final String COL_OTP_CODE = "otp";
    private static final String COL_OTP_EXPIRY = "expiry_time";
    private static final String COL_OTP_ATTEMPTS = "attempts";
    private static final String COL_OTP_USED = "is_used";

    // Grounds columns
    private static final String COL_GROUND_ID = "id";
    private static final String COL_GROUND_NAME = "name";
    private static final String COL_GROUND_LOCATION = "location";
    private static final String COL_GROUND_DESC = "description";
    private static final String COL_GROUND_SPORT = "sport_type";
    private static final String COL_GROUND_CAPACITY = "capacity";
    private static final String COL_GROUND_FROM = "available_from";
    private static final String COL_GROUND_TO = "available_to";
    private static final String COL_GROUND_ACTIVE = "is_active";

    // Bookings columns
    private static final String COL_BOOKING_ID = "id";
    private static final String COL_BOOKING_USER_ID = "user_id";
    private static final String COL_BOOKING_GROUND_ID = "ground_id";
    private static final String COL_BOOKING_DATE = "booking_date";
    private static final String COL_BOOKING_SLOT = "time_slot";
    private static final String COL_BOOKING_STATUS = "status";
    private static final String COL_BOOKING_GROUND_NAME = "ground_name";
    private static final String COL_BOOKING_USER_NAME = "user_name";

    // Events columns
    private static final String COL_EVENT_ID = "id";
    private static final String COL_EVENT_TITLE = "title";
    private static final String COL_EVENT_DESC = "description";
    private static final String COL_EVENT_DATE = "event_date";
    private static final String COL_EVENT_TIME = "event_time";
    private static final String COL_EVENT_VENUE = "venue";
    private static final String COL_EVENT_ORGANIZER = "organizer";
    private static final String COL_EVENT_MAX = "max_participants";
    private static final String COL_EVENT_REG = "registered_count";
    private static final String COL_EVENT_ACTIVE = "is_active";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_ROLE + " TEXT, " +
                COL_USER_VERIFIED + " INTEGER DEFAULT 0, " +
                COL_USER_CREATED + " INTEGER" +
                ")";
        db.execSQL(createUsers);

        // Create OTP table
        String createOTP = "CREATE TABLE " + TABLE_OTP + " (" +
                COL_OTP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_OTP_EMAIL + " TEXT, " +
                COL_OTP_CODE + " TEXT, " +
                COL_OTP_EXPIRY + " INTEGER, " +
                COL_OTP_ATTEMPTS + " INTEGER DEFAULT 0, " +
                COL_OTP_USED + " INTEGER DEFAULT 0" +
                ")";
        db.execSQL(createOTP);

        // Create grounds table
        String createGrounds = "CREATE TABLE " + TABLE_GROUNDS + " (" +
                COL_GROUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_GROUND_NAME + " TEXT, " +
                COL_GROUND_LOCATION + " TEXT, " +
                COL_GROUND_DESC + " TEXT, " +
                COL_GROUND_SPORT + " TEXT, " +
                COL_GROUND_CAPACITY + " INTEGER, " +
                COL_GROUND_FROM + " TEXT, " +
                COL_GROUND_TO + " TEXT, " +
                COL_GROUND_ACTIVE + " INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createGrounds);

        // Create bookings table
        String createBookings = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_USER_ID + " INTEGER, " +
                COL_BOOKING_GROUND_ID + " INTEGER, " +
                COL_BOOKING_DATE + " TEXT, " +
                COL_BOOKING_SLOT + " TEXT, " +
                COL_BOOKING_STATUS + " TEXT DEFAULT 'Pending', " +
                COL_BOOKING_GROUND_NAME + " TEXT, " +
                COL_BOOKING_USER_NAME + " TEXT, " +
                "FOREIGN KEY(" + COL_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_BOOKING_GROUND_ID + ") REFERENCES " + TABLE_GROUNDS + "(" + COL_GROUND_ID + ")" +
                ")";
        db.execSQL(createBookings);

        // Create events table
        String createEvents = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EVENT_TITLE + " TEXT, " +
                COL_EVENT_DESC + " TEXT, " +
                COL_EVENT_DATE + " TEXT, " +
                COL_EVENT_TIME + " TEXT, " +
                COL_EVENT_VENUE + " TEXT, " +
                COL_EVENT_ORGANIZER + " TEXT, " +
                COL_EVENT_MAX + " INTEGER, " +
                COL_EVENT_REG + " INTEGER DEFAULT 0, " +
                COL_EVENT_ACTIVE + " INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createEvents);

        // Insert default admin
        insertDefaultAdmin(db);
        
        // Insert sample grounds
        insertSampleGrounds(db);
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, "System Admin");
        values.put(COL_USER_EMAIL, "admin@sports.univ.edu");
        values.put(COL_USER_PHONE, "1234567890");
        values.put(COL_USER_PASSWORD, hashPassword("admin123"));
        values.put(COL_USER_ROLE, "admin");
        values.put(COL_USER_VERIFIED, 1);
        values.put(COL_USER_CREATED, System.currentTimeMillis());
        db.insert(TABLE_USERS, null, values);
    }

    private void insertSampleGrounds(SQLiteDatabase db) {
        String[] grounds = {
            "Football Ground,Main Stadium,Full-size football field with natural grass,Football,22,06:00,22:00",
            "Basketball Court,Sports Complex,Indoor basketball court with professional flooring,Basketball,20,08:00,21:00",
            "Cricket Pitch,North Campus,Professional cricket ground with practice nets,Cricket,30,07:00,19:00",
            "Tennis Courts,South Block,Tennis courts with synthetic surface,Tennis,4,06:00,20:00",
            "Volleyball Court,Indoor Arena,Indoor volleyball court with wooden floor,Volleyball,12,08:00,22:00",
            "Badminton Hall,Sports Center,Badminton courts with synthetic mats,Badminton,8,07:00,21:00"
        };

        for (String g : grounds) {
            String[] p = g.split(",");
            ContentValues values = new ContentValues();
            values.put(COL_GROUND_NAME, p[0]);
            values.put(COL_GROUND_LOCATION, p[1]);
            values.put(COL_GROUND_DESC, p[2]);
            values.put(COL_GROUND_SPORT, p[3]);
            values.put(COL_GROUND_CAPACITY, Integer.parseInt(p[4]));
            values.put(COL_GROUND_FROM, p[5]);
            values.put(COL_GROUND_TO, p[6]);
            values.put(COL_GROUND_ACTIVE, 1);
            db.insert(TABLE_GROUNDS, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== USER OPERATIONS ====================

    /** Register new user */
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PHONE, user.getPhone());
        values.put(COL_USER_PASSWORD, hashPassword(user.getPassword()));
        values.put(COL_USER_ROLE, user.getRole());
        values.put(COL_USER_VERIFIED, user.isVerified() ? 1 : 0);
        values.put(COL_USER_CREATED, user.getCreatedAt());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    /** Check if email exists */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /** Validate login credentials */
    public User validateLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashed = hashPassword(password);
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?",
                new String[]{email, hashed},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)));
            user.setVerified(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_VERIFIED)) == 1);
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_CREATED)));
            cursor.close();
        }
        return user;
    }

    /** Get user by email */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COL_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setVerified(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_VERIFIED)) == 1);
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)));
            cursor.close();
        }
        return user;
    }

    /** Verify user OTP */
    public boolean verifyUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_VERIFIED, 1);

        int rows = db.update(TABLE_USERS, values,
                COL_USER_EMAIL + " = ?",
                new String[]{email});
        return rows > 0;
    }

    /** Get users by role */
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COL_USER_ROLE + " = ?",
                new String[]{role},
                null, null,
                COL_USER_CREATED + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)));
                user.setVerified(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_VERIFIED)) == 1);
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    // ==================== OTP OPERATIONS ====================

    /** Save OTP to database with secure hash */
    public long saveOTP(OTP otp) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Generate salt and hash the OTP for security
        String salt = com.example.universitysports.helpers.OTPUtil.generateSalt();
        String hashedOtp = com.example.universitysports.helpers.OTPUtil.hashOTP(otp.getOtp(), salt);

        ContentValues values = new ContentValues();
        values.put(COL_OTP_EMAIL, otp.getEmail());
        values.put(COL_OTP_CODE, hashedOtp); // Store hashed version
        values.put(COL_OTP_EXPIRY, otp.getExpiryTime());
        values.put(COL_OTP_ATTEMPTS, otp.getAttempts());
        values.put(COL_OTP_USED, otp.isUsed() ? 1 : 0);

        // Delete any existing OTP for this email
        db.delete(TABLE_OTP, COL_OTP_EMAIL + " = ?", new String[]{otp.getEmail()});

        long id = db.insert(TABLE_OTP, null, values);
        db.close();
        return id;
    }

    /** Get OTP by email */
    public OTP getOTPByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OTP,
                null,
                COL_OTP_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        OTP otp = null;
        if (cursor != null && cursor.moveToFirst()) {
            otp = new OTP();
            otp.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_OTP_ID)));
            otp.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_OTP_EMAIL)));
            otp.setOtp(cursor.getString(cursor.getColumnIndexOrThrow(COL_OTP_CODE)));
            otp.setExpiryTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_OTP_EXPIRY)));
            otp.setAttempts(cursor.getInt(cursor.getColumnIndexOrThrow(COL_OTP_ATTEMPTS)));
            otp.setUsed(cursor.getInt(cursor.getColumnIndexOrThrow(COL_OTP_USED)) == 1);
            cursor.close();
        }
        return otp;
    }

    /** Verify OTP */
    public OTPVerifyResult verifyOTP(String email, String enteredOtp) {
        OTP otp = getOTPByEmail(email);

        if (otp == null) {
            return new OTPVerifyResult(false, "No OTP found. Please request a new one.");
        }

        if (otp.isUsed()) {
            deleteOTP(email); // Clean up used OTP
            return new OTPVerifyResult(false, "OTP already used. Please request a new one.");
        }

        if (otp.isExpired()) {
            deleteOTP(email); // Clean up expired OTP
            return new OTPVerifyResult(false, "OTP expired. Please request a new one.");
        }

        if (otp.isMaxAttemptsExceeded()) {
            deleteOTP(email); // Clean up locked OTP
            return new OTPVerifyResult(false, "Max attempts exceeded. Please try again later.");
        }

        // Verify using secure hash comparison
        String storedHash = otp.getOtp(); // This now contains "salt:hash"
        if (!com.example.universitysports.helpers.OTPUtil.verifyOTP(enteredOtp, storedHash)) {
            // Increment attempts
            incrementOTPAttempts(email);
            int remaining = 3 - (otp.getAttempts() + 1);
            String msg = com.example.universitysports.helpers.OTPUtil.getAttemptsMessage(otp.getAttempts() + 1, 3);
            return new OTPVerifyResult(false, msg);
        }

        // Success - mark as used
        markOTPAsUsed(email);
        return new OTPVerifyResult(true, "OTP verified successfully");
    }

        if (otp.isUsed()) {
            deleteOTP(email); // Clean up used OTP
            return new OTPVerifyResult(false, "OTP already used. Please request a new one.");
        }

        if (otp.isExpired()) {
            deleteOTP(email); // Clean up expired OTP
            return new OTPVerifyResult(false, "OTP expired. Please request a new one.");
        }

        if (otp.isMaxAttemptsExceeded()) {
            deleteOTP(email); // Clean up locked OTP
            return new OTPVerifyResult(false, "Max attempts exceeded. Please try again later.");
        }

        // Verify using secure hash comparison
        String storedHash = otp.getOtp(); // This now contains "salt:hash"
        if (!com.example.universitysports.helpers.OTPUtil.verifyOTP(enteredOtp, storedHash)) {
            // Increment attempts
            incrementOTPAttempts(email);
            int remaining = 3 - (otp.getAttempts() + 1);
            String msg = com.example.universitysports.helpers.OTPUtil.getAttemptsMessage(otp.getAttempts() + 1, 3);
            return new OTPVerifyResult(false, msg);
        }

        // Success - mark as used
        markOTPAsUsed(email);
        return new OTPVerifyResult(true, "OTP verified successfully");
    }

    /** Increment OTP attempts */
    private void incrementOTPAttempts(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        OTP otp = getOTPByEmail(email);
        if (otp != null) {
            ContentValues values = new ContentValues();
            values.put(COL_OTP_ATTEMPTS, otp.getAttempts() + 1);
            db.update(TABLE_OTP, values, COL_OTP_EMAIL + " = ?", new String[]{email});
        }
        db.close();
    }

    /** Mark OTP as used */
    private void markOTPAsUsed(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_OTP_USED, 1);
        db.update(TABLE_OTP, values, COL_OTP_EMAIL + " = ?", new String[]{email});
        db.close();
    }

    /** Delete OTP */
    public void deleteOTP(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OTP, COL_OTP_EMAIL + " = ?", new String[]{email});
        db.close();
    }

    /** OTP Verify Result class */
    public static class OTPVerifyResult {
        private boolean success;
        private String message;

        public OTPVerifyResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    // ==================== GROUND OPERATIONS ====================

    public long addGround(Ground ground) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GROUND_NAME, ground.getName());
        values.put(COL_GROUND_LOCATION, ground.getLocation());
        values.put(COL_GROUND_DESC, ground.getDescription());
        values.put(COL_GROUND_SPORT, ground.getSportType());
        values.put(COL_GROUND_CAPACITY, ground.getCapacity());
        values.put(COL_GROUND_FROM, ground.getAvailableFrom());
        values.put(COL_GROUND_TO, ground.getAvailableTo());
        values.put(COL_GROUND_ACTIVE, ground.isActive() ? 1 : 0);
        long id = db.insert(TABLE_GROUNDS, null, values);
        db.close();
        return id;
    }

    public boolean updateGround(Ground ground) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GROUND_NAME, ground.getName());
        values.put(COL_GROUND_LOCATION, ground.getLocation());
        values.put(COL_GROUND_DESC, ground.getDescription());
        values.put(COL_GROUND_SPORT, ground.getSportType());
        values.put(COL_GROUND_CAPACITY, ground.getCapacity());
        values.put(COL_GROUND_FROM, ground.getAvailableFrom());
        values.put(COL_GROUND_TO, ground.getAvailableTo());
        values.put(COL_GROUND_ACTIVE, ground.isActive() ? 1 : 0);

        int rows = db.update(TABLE_GROUNDS, values,
                COL_GROUND_ID + " = ?",
                new String[]{String.valueOf(ground.getId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteGround(int groundId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GROUND_ACTIVE, 0);
        int rows = db.update(TABLE_GROUNDS, values,
                COL_GROUND_ID + " = ?",
                new String[]{String.valueOf(groundId)});
        db.close();
        return rows > 0;
    }

    public List<Ground> getAllGrounds() {
        List<Ground> grounds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GROUNDS,
                null,
                COL_GROUND_ACTIVE + " = 1",
                null, null, null,
                COL_GROUND_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Ground ground = new Ground();
                ground.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GROUND_ID)));
                ground.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_NAME)));
                ground.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_LOCATION)));
                ground.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_DESC)));
                ground.setSportType(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_SPORT)));
                ground.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GROUND_CAPACITY)));
                ground.setAvailableFrom(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_FROM)));
                ground.setAvailableTo(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_TO)));
                ground.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GROUND_ACTIVE)) == 1);
                grounds.add(ground);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return grounds;
    }

    public Ground getGroundById(int groundId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GROUNDS,
                null,
                COL_GROUND_ID + " = ?",
                new String[]{String.valueOf(groundId)},
                null, null, null);

        Ground ground = null;
        if (cursor != null && cursor.moveToFirst()) {
            ground = new Ground();
            ground.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GROUND_ID)));
            ground.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_NAME)));
            ground.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_LOCATION)));
            ground.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_DESC)));
            ground.setSportType(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_SPORT)));
            ground.setCapacity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GROUND_CAPACITY)));
            ground.setAvailableFrom(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_FROM)));
            ground.setAvailableTo(cursor.getString(cursor.getColumnIndexOrThrow(COL_GROUND_TO)));
            ground.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GROUND_ACTIVE)) == 1);
            cursor.close();
        }
        return ground;
    }

    // ==================== BOOKING OPERATIONS ====================

    public long createBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_USER_ID, booking.getUserId());
        values.put(COL_BOOKING_GROUND_ID, booking.getGroundId());
        values.put(COL_BOOKING_DATE, booking.getBookingDate());
        values.put(COL_BOOKING_SLOT, booking.getTimeSlot());
        values.put(COL_BOOKING_STATUS, booking.getStatus());
        values.put(COL_BOOKING_GROUND_NAME, booking.getGroundName());
        values.put(COL_BOOKING_USER_NAME, booking.getUserName());

        long id = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return id;
    }

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS,
                null,
                COL_BOOKING_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                COL_BOOKING_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking booking = extractBookingFromCursor(cursor);
                bookings.add(booking);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return bookings;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS,
                null,
                null, null, null, null,
                COL_BOOKING_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking booking = extractBookingFromCursor(cursor);
                bookings.add(booking);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return bookings;
    }

    private Booking extractBookingFromCursor(Cursor cursor) {
        Booking booking = new Booking();
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOKING_ID)));
        booking.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOKING_USER_ID)));
        booking.setGroundId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BOOKING_GROUND_ID)));
        booking.setBookingDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_DATE)));
        booking.setTimeSlot(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_SLOT)));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_STATUS)));
        booking.setGroundName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_GROUND_NAME)));
        booking.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_USER_NAME)));
        return booking;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, status);
        int rows = db.update(TABLE_BOOKINGS, values,
                COL_BOOKING_ID + " = ?",
                new String[]{String.valueOf(bookingId)});
        db.close();
        return rows > 0;
    }

    public boolean cancelBooking(int bookingId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKING_STATUS, "Cancelled");
        int rows = db.update(TABLE_BOOKINGS, values,
                COL_BOOKING_ID + " = ? AND " + COL_BOOKING_USER_ID + " = ?",
                new String[]{String.valueOf(bookingId), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    /**
     * Check if time slot is available for booking
     */
    public boolean isSlotAvailable(int groundId, String date, String timeSlot) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKINGS,
                null,
                COL_BOOKING_GROUND_ID + " = ? AND " +
                COL_BOOKING_DATE + " = ? AND " +
                COL_BOOKING_SLOT + " = ? AND " +
                COL_BOOKING_STATUS + " != 'Cancelled'",
                new String[]{String.valueOf(groundId), date, timeSlot},
                null, null, null);

        boolean available = cursor.getCount() == 0;
        cursor.close();
        return available;
    }

    // ==================== EVENT OPERATIONS ====================

    public long createEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_TITLE, event.getTitle());
        values.put(COL_EVENT_DESC, event.getDescription());
        values.put(COL_EVENT_DATE, event.getEventDate());
        values.put(COL_EVENT_TIME, event.getEventTime());
        values.put(COL_EVENT_VENUE, event.getVenue());
        values.put(COL_EVENT_ORGANIZER, event.getOrganizer());
        values.put(COL_EVENT_MAX, event.getMaxParticipants());
        values.put(COL_EVENT_REG, event.getRegisteredCount());
        values.put(COL_EVENT_ACTIVE, event.isActive() ? 1 : 0);

        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    public boolean updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_TITLE, event.getTitle());
        values.put(COL_EVENT_DESC, event.getDescription());
        values.put(COL_EVENT_DATE, event.getEventDate());
        values.put(COL_EVENT_TIME, event.getEventTime());
        values.put(COL_EVENT_VENUE, event.getVenue());
        values.put(COL_EVENT_ORGANIZER, event.getOrganizer());
        values.put(COL_EVENT_MAX, event.getMaxParticipants());
        values.put(COL_EVENT_REG, event.getRegisteredCount());
        values.put(COL_EVENT_ACTIVE, event.isActive() ? 1 : 0);

        int rows = db.update(TABLE_EVENTS, values,
                COL_EVENT_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_ACTIVE, 0);
        int rows = db.update(TABLE_EVENTS, values,
                COL_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)});
        db.close();
        return rows > 0;
    }

    public List<Event> getUpcomingEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS,
                null,
                COL_EVENT_ACTIVE + " = 1",
                null, null, null,
                COL_EVENT_DATE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID)));
                event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE)));
                event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC)));
                event.setEventDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)));
                event.setEventTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TIME)));
                event.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_VENUE)));
                event.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_ORGANIZER)));
                event.setMaxParticipants(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_MAX)));
                event.setRegisteredCount(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_REG)));
                event.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ACTIVE)) == 1);
                events.add(event);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return events;
    }

    public Event getEventById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS,
                null,
                COL_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)},
                null, null, null);

        Event event = null;
        if (cursor != null && cursor.moveToFirst()) {
            event = new Event();
            event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID)));
            event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE)));
            event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC)));
            event.setEventDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)));
            event.setEventTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TIME)));
            event.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_VENUE)));
            event.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_ORGANIZER)));
            event.setMaxParticipants(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_MAX)));
            event.setRegisteredCount(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_REG)));
            event.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ACTIVE)) == 1);
            cursor.close();
        }
        return event;
    }

    // ==================== STATISTICS ====================

    public int getTotalBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS, null);
        int count = cursor.getCount() > 0 ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    public int getActiveGroundsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_GROUNDS + " WHERE " + COL_GROUND_ACTIVE + " = 1",
                null);
        int count = cursor.getCount() > 0 ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    public int getUpcomingEventsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_EVENTS + " WHERE " + COL_EVENT_ACTIVE + " = 1",
                null);
        int count = cursor.getCount() > 0 ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    public int getBookingCountForGround(int groundId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_BOOKINGS +
                        " WHERE " + COL_BOOKING_GROUND_ID + " = ?",
                new String[]{String.valueOf(groundId)});
        int count = cursor.getCount() > 0 ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    // ==================== UTILITY ====================

    /**
     * Simple password hashing (for demo - use BCrypt in production)
     */
    private String hashPassword(String password) {
        // In production, use BCrypt or similar
        return password; // ⚠️ TEMPORARY - Add proper hashing before launch!
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}