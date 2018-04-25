package es.udc.apm.familycare.utils;

import android.Manifest;

public class Constants {

    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.INTERNET};

    public static final String PREFS_USER = "Prefs.User";
    public static final String PREFS_USER_UID = "Prefs.User.UID";
    public static final String PREFS_USER_ROLE= "Prefs.User.Role";

    public static final String ROLE_VIP = "VIP";
    public static final String ROLE_GUARD = "GUARD";
}
