package es.udc.apm.familycare.maps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;


public class GeofenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "GeofenceTransitionsIS";

    private boolean checkInside(LatLng center, float radius, double longitude, double latitude) {
        return calculateDistance(
                center.longitude, center.latitude, longitude, latitude
        ) < (double) radius;}

    private double calculateDistance(
            double longitude1, double latitude1,
            double longitude2, double latitude2) {
        double c =
                Math.sin(Math.toRadians(latitude1)) *
                        Math.sin(Math.toRadians(latitude2)) +
                        Math.cos(Math.toRadians(latitude1)) *
                                Math.cos(Math.toRadians(latitude2)) *
                                Math.cos(Math.toRadians(longitude2) -
                                        Math.toRadians(longitude1));
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

        // Avisos aqui
        Log.e(TAG,"Not in safe zone");

    }
}