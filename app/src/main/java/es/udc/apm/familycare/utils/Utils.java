package es.udc.apm.familycare.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    private final Context mContext;

    public Utils(Context con) {
        mContext = con;
    }

// --Commented out by Inspection START (22/04/2018 19:26):
//    /**
//     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
//     * Encoded email is also used as "userEmail", list and item "owner" value
//     */
//    public static String encodeEmail(String userEmail) {
//        return userEmail.replace(".", ",");
//    }
// --Commented out by Inspection STOP (22/04/2018 19:26)

    //This is a method to Check if the device internet connection is currently on
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
