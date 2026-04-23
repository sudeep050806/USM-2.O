# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep model classes (Parcelable)
-keep class com.example.universitysports.models.** { *; }
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep JavaMail classes
-dontwarn javax.mail.**
-dontwarn javax.activation.**
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }

# Keep helper classes
-keep class com.example.universitysports.helpers.** { *; }

# Keep adapters
-keep class com.example.universitysports.adapters.** { *; }