package com.example.universitysports.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * NetworkUtils - Check network connectivity
 */
public class NetworkUtils {

    /**
     * Check if internet is available
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && 
                   activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}