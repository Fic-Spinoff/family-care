package es.udc.apm.familycare.maps;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {
    protected static final String TAG = "GeofenceTransitionsIS";


    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());
            return;
        }
        getGeofenceTransitionDetails(event);

    }

    private void getGeofenceTransitionDetails(GeofencingEvent event) {

        // Get the transition type.
        int geofenceTransition = event.getGeofenceTransition();

        List<Geofence> triggeringGeofences = null;
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.e(TAG, "Enter in geofence");
        }

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e(TAG, "Exit from Geofence");
        }
    }
}