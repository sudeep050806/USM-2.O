# University Sports Management App

A comprehensive Android application for managing university sports facilities, bookings, and events. Built with Java using SQLite database and Material Design.

## Features

### User Features
- User Registration & Login with Email OTP Verification
- Browse all available sports grounds with detailed information
- Ground booking system with time slot selection
- View and manage personal bookings (cancel if needed)
- Browse upcoming sports events and register
- Clean, intuitive Material Design UI

### Admin Features
- Secure Admin Dashboard
- Manage sports grounds (Add/Edit/Delete)
- View and approve/reject all bookings
- Create and manage sports events
- Monitor facility usage statistics and revenue

## Tech Stack

- **Language**: Java
- **Database**: SQLite (via DBHelper)
- **UI**: Material Design 3, ConstraintLayout, RecyclerView, CardView
- **Architecture**: Activity-based (MVC pattern)
- **Email**: JavaMail API for OTP and notifications
- **Min SDK**: 24, Target SDK**: 35

## Project Structure

```
app/src/main/java/com/example/universitysports/
├── adapters/           # RecyclerView adapters (GroundAdapter, TimeSlotAdapter)
├── auth/              # Authentication activities (Login, Signup, OTP, Splash)
├── bookings/          # Booking system (BookGround, MyBookings, Manage)
├── dashboard/         # Dashboards (User & Admin, FacilityUsage)
├── events/            # Event management (UpcomingEvents, ManageEvents)
├── grounds/           # Ground browsing (GroundList, GroundDetail, ManageGrounds)
├── helpers/           # Helper classes (DBHelper, EmailSender, SessionManager, AppConfig)
├── models/            # Data models (User, Ground, Booking, Event)
├── utils/             # Utility classes (ValidationUtils, NetworkUtils)
├── ActivityUserDashboard.java
├── AdminDashboardActivity.java
├── SplashActivity.java
└── MainActivity.java
```

## Setup Instructions

### 1. Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK with API level 35

### 2. Clone/Import Project
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project folder and select it
4. Wait for Gradle sync to complete

### 3. Configure Email (JavaMail API)

The app uses Gmail SMTP for sending OTP and notifications. **For production, replace with your own email credentials.**

#### Using Gmail (For Testing):
1. Go to `app/src/main/java/com/example/universitysports/helpers/EmailSender.java`
2. Update these constants:
   ```java
   private static final String SMTP_HOST = "smtp.gmail.com";
   private static final String SMTP_PORT = "587";
   private static final String EMAIL_FROM = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-app-password";
   ```
3. For Gmail, you need to generate an **App Password** (not your regular password):
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
   - Use that 16-character password

#### Using a Custom SMTP Server (Production):
Update the EmailSender class with your SMTP server details:
```java
private static final String SMTP_HOST = "your-smtp-server.com";
private static final String SMTP_PORT = "587";
private static final String EMAIL_FROM = "noreply@youruniversity.edu";
private static final String EMAIL_PASSWORD = "your-password";
```

### 4. Run the App
1. Connect an Android device or start an emulator (API 24+)
2. Click "Run" in Android Studio (or press Ctrl+R)
3. The app will install and launch

## Login Credentials

### Default Admin Account
- **Email**: admin@sports.univ.edu
- **Password**: admin123

### Regular Users
- Register through the Signup screen
- OTP verification will be sent to the provided email (demo mode: OTP = "123456")

## Database Schema

The app uses SQLite with four main tables:

### Users
- id (PK), name, email, phone, password, is_verified, user_type, created_at

### Grounds
- id (PK), name, description, sport_type, capacity, opening_time, closing_time, is_active

### Bookings
- id (PK), user_id, ground_id, booking_date, start_time, end_time, status, ground_name, user_name

### Events
- id (PK), event_name, description, event_date, event_time, venue, organizer, max_participants, registered_count, is_active

## Key Features Implementation

### 1. Booking System
- Real-time slot availability checking
- Prevents double booking
- Automatic email confirmation
- Cancellation support

### 2. OTP Verification
- 6-digit OTP generated on signup
- Sent via email using JavaMail API
- 60-second cooldown for resend

### 3. Admin Management
- Full CRUD for grounds and events
- Booking approval/rejection workflow
- Facility usage statistics

## UI/UX Highlights

- Material Design 3 components
- Smooth RecyclerView lists with card-based layouts
- Responsive design for all screen sizes
- Dark/light theme support (via Material 3)
- Loading states and error handling

## Error Handling & Validation

- Email format validation
- Password strength check
- Phone number validation
- Required field checking
- Network connectivity awareness
- Database transaction safety

## Future Improvements (Bonus)

The current codebase is structured to easily integrate:

### 1. Firebase Migration
- Replace DBHelper with Firebase Realtime Database or Firestore
- Use Firebase Auth for authentication
- Implement Firebase Cloud Messaging for notifications
- Use Firebase Storage for ground images

### 2. MVVM Architecture
- Introduce ViewModel classes
- Use LiveData for reactive UI updates
- Implement Repository pattern
- Add Data Binding

### 3. REST API Integration
- Create a backend REST API (Node.js/Django/Laravel)
- Replace local DB with API calls using Retrofit
- Add offline sync capability
- Implement JWT authentication

### 4. Additional Features
- Push notifications for booking reminders
- Ground photos and ratings
- QR code check-in system
- Payment gateway integration
- Multi-language support
- Dark mode toggle

## Troubleshooting

### JavaMail Not Working
- Ensure you're using an App Password for Gmail
- Check that "Less secure app access" is enabled (if applicable)
- Verify network connectivity
- Check logcat for detailed error messages

### Database Issues
- The app auto-creates the database on first launch
- To reset: Clear app data or uninstall/reinstall
- Check Logcat for SQLite errors

### Build Errors
- Clean and rebuild project: Build → Clean Project → Rebuild Project
- Invalidate caches: File → Invalidate Caches & Restart
- Ensure all Gradle dependencies are synced

## Contributing

This is a starter project - feel free to enhance it! Suggested improvements:
- Add more sports categories
- Implement advanced reporting
- Add user profile management
- Integrate maps for venue locations
- Add photo galleries for each ground

## License

This project is provided as-is for educational purposes.

---

**Note**: For production deployment, ensure you:
1. Hash passwords (use BCrypt)
2. Use HTTPS for all API calls
3. Implement proper session management
4. Add comprehensive logging
5. Perform security testing
6. Add proper error reporting (Crashlytics)
7. Follow Google Play policies