# Quick Start Guide

## Step-by-Step Setup

### 1. Prerequisites
- Install [Android Studio](https://developer.android.com/studio) (Arctic Fox or newer)
- Ensure you have JDK 11 installed
- Create a Google Account (for Gmail SMTP)

### 2. Open Project
1. Launch Android Studio
2. Click **File → Open**
3. Navigate to `University Sports Management 2.0` folder
4. Click **OK**
5. Wait for Gradle sync to complete (2-5 minutes)

### 3. Configure Email (Critical!)
Without this step, OTP and booking confirmations **WILL NOT WORK**.

1. Open `app/src/main/java/com/example/universitysports/helpers/EmailSender.java`
2. Locate these lines (around line 16-19):
   ```java
   private static final String EMAIL_FROM = "your-email@gmail.com";
   private static final String EMAIL_PASSWORD = "your-app-password";
   ```
3. Replace with your Gmail:
   ```java
   private static final String EMAIL_FROM = "yourname@gmail.com";
   ```
4. Generate App Password:
   - Go to https://myaccount.google.com/security
   - Enable **2-Step Verification** (if not already)
   - Click **App passwords**
   - Select "Mail" and your device
   - Copy the 16-character password
5. Set password (without spaces):
   ```java
   private static final String EMAIL_PASSWORD = "abcd efgh ijkl mnop"; // no spaces
   ```

### 4. Run the App
1. Start an emulator (API 24+) or connect a device
2. Click **Run** (green triangle) or press `Ctrl+R`
3. Wait for installation (~1-2 minutes)
4. App will launch automatically

### 5. Test Login
**Admin Account:**
- Email: `admin@sports.univ.edu`
- Password: `admin123`

**New User:**
- Tap "Sign Up"
- Fill form with valid email
- Check email for OTP (demo OTP: `123456`)
- Verify and login

## Testing Core Features

### Test Ground Booking
1. Login as student
2. Dashboard → "Book Ground"
3. Select a ground
4. Choose date (today)
5. Click "Show Available Slots"
6. Select green (available) slot
7. Click "Confirm Booking"
8. Check email for confirmation

### Test Admin Features
1. Login as admin
2. Dashboard shows stats
3. Click "Manage Grounds" → Add new ground
4. Click "Manage Bookings" → Approve/reject bookings
5. Click "Manage Events" → Create new event
6. Check "Usage Stats" for analytics

## Troubleshooting

**"Internet permission required" error:**
- Check `AndroidManifest.xml` has `<uses-permission android:name="android.permission.INTERNET" />`

**Email not sending:**
- Verify app password is correct (no spaces)
- Check "Allow less secure apps" is OFF (use App Password instead)
- Ensure device has internet connection

**Database errors:**
- Clear app data: Settings → Apps → University Sports → Storage → Clear Data
- Or uninstall and reinstall

**App crashes on startup:**
- Check Logcat in Android Studio for error details
- Ensure all dependencies are synced (File → Sync Project with Gradle Files)

**Emulator not working:**
- Create new AVD with API 35, x86_64
- Enable "Cold boot" in AVD settings

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| Gradle sync failed | File → Invalidate Caches → Restart |
| JavaMail errors | Verify Gmail credentials + App Password |
| Cannot find symbol | Clean & Rebuild project |
| Layout not showing | Check XML syntax, rebuild |
| Images not loading | Place images in `res/drawable` folder |

## Next Steps

1. **Customize** - Add your university logo, colors
2. **Expand** - Add more sports categories in `DBHelper.insertSampleGrounds()`
3. **Style** - Update colors in `res/values/colors.xml`
4. **Scale** - Migrate to Firebase for multi-user support
5. **Publish** - Generate signed APK: Build → Generate Signed Bundle/APK

## Getting Help

- Check `README.md` for detailed documentation
- Review `PROJECT_SUMMARY.md` for complete file listing
- Inspect Logcat output for runtime errors
- Refer to comments in code for explanations

---

**Project Status**: ✅ Fully functional and ready to run!