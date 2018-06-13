package es.udc.apm.familycare.maps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;

import es.udc.apm.familycare.model.Event;
import es.udc.apm.familycare.model.EventType;
import es.udc.apm.familycare.repository.EventRepository;
import es.udc.apm.familycare.utils.Constants;


public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitionsIS";

    private EventRepository mRepo = null;

    private boolean checkInside(LatLng center, float radius, double longitude, double latitude) {
        return calculateDistance(
                center.longitude, center.latitude, longitude, latitude
        ) < (double) radius;}

    private double calculateDistance(double longitude1, double latitude1, double longitude2,
                                     double latitude2) {
        double c = Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2)) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.cos(Math.toRadians(longitude2) - Math.toRadians(longitude1));
        c = c > 0 ? Math.min(1, c) : Math.max(-1, c);
        return 3959 * 1.609 * 1000 * Math.acos(c);
    }

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HashMap<LatLng, Float> geofenceData = GeofenceStore.getInstance().getGeofenceData();
        Location triggeringLocation = intent.getParcelableExtra("com.google.android.location.intent.extra.triggering_location");

        for (LatLng center : geofenceData.keySet()) {
            if (checkInside(center, geofenceData.get(center), triggeringLocation.getLongitude(), triggeringLocation.getLatitude()))
                return;
        }

        // Send event notification
        Log.e(TAG,"Out of all Geofences");
        this.sendEvent();
    }

    private void sendEvent() {
        if (this.mRepo == null) {
            this.mRepo = new EventRepository();
        }

        String uid = getSharedPreferences(Constants.Prefs.USER, MODE_PRIVATE)
                .getString(Constants.Prefs.KEY_USER_UID, null);
        if (uid != null) {
            this.mRepo.createEvent(uid, new Event(
                    EventType.LOCATION.getValue(),
                    "VIP may be lost!",
                    "Your VIP is not inside any secure zone",
                    new Timestamp(new Date())
            ));
        }
    }
}