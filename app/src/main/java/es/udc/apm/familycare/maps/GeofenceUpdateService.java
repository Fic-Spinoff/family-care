package es.udc.apm.familycare.maps;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;




public class GeofenceUpdateService extends IntentService {
    private static String TAG = "GeofenceUpdateService";

    private GeofencingClient mGeofendcingClient = null;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingRequest.Builder builder = new GeofencingRequest.Builder();




    public GeofenceUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mGeofendcingClient = LocationServices.getGeofencingClient(this.getBaseContext());
        updateGeofences();
    }

    @SuppressLint("MissingPermission")
    private void updateGeofences() {
        if ((GeofenceStore.getInstance()).getGeofenceList().isEmpty()){
            if (mGeofencePendingIntent != null) {
                mGeofendcingClient.removeGeofences(mGeofencePendingIntent);
            }
        } else {
            mGeofendcingClient.addGeofences(
                    getGeofencingRequest(),
                    getGeofencePendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Geofence added correctly");
                    } else {
                        Log.e(TAG, task.getException().toString());
                    }
                }
            });
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences((GeofenceStore.getInstance()).getGeofenceList());
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this.getApplicationContext(), GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

}
