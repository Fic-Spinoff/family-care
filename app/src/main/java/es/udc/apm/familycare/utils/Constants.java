package es.udc.apm.familycare.utils;

import android.Manifest;

public class Constants {

    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH,
            /*Manifest.permission.BLUETOOTH_ADMIN, */Manifest.permission.INTERNET};

    public static final String PREFS_USER = "Prefs.User";
    public static final String PREFS_USER_UID = "Prefs.User.UID";
    public static final String PREFS_USER_ROLE= "Prefs.User.Role";

    public static final String PREFS_GUARD_LINK= "Prefs.Guard.Link";

    public static final String ROLE_VIP = "VIP";
    public static final String ROLE_GUARD = "GUARD";

    public static class Collections {
        public static final String USERS = "users";
    }

    public static class Properties {
        public static final String LINK = "link";
        public static final String VIP = "vip";
        public static final String VIP_NAME = "vipName";
        public static final String VIP_STILL_SINCE = "stillSince";
    }

    public static class Keys {
        public static final String LINK = "Key.Link";
    }

    public static class Prefs {
        public static final String USER = "Prefs.User";

        public static final String KEY_USER_UID = "Prefs.User.UID";
        public static final String KEY_USER_ROLE = "Prefs.User.Role";
        public static final String KEY_GUARD_LINK = "Prefs.Guard.Link";

        // Curate state Still or not
        public static final String KEY_VIP_STATE = "Prefs.Vip.State";
        // Is state timer created
        public static final String KEY_VIP_TIMER = "Prefs.Vip.Timer";
        // Is still state on firebase
        public static final String KEY_VIP_STILL = "Prefs.Vip.Still";
    }

    public static class MimeType {
        public static final String TEXT = "text/plain";
    }
}
