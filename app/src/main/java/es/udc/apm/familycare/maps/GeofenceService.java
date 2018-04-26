package es.udc.apm.familycare.maps;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;



public class GeofenceService extends Service {
    private static String TAG = "GeofenceService";

    public final static String UPDATE_GEOFENCES = "UPDATE_GEOFENCES";
    public final static String REMOVE_GEOFENCES = "REMOVE_GEOFENCES";

    private GeofencingClient mGeofendcingClient = null;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    private static HashMap<LatLng, Geofence> mGeofencingMap = new HashMap<>();

    private GeofenceReceiver mGeofenceReceiver;
    private GeofenceRemoveReceiver mGeofenceRemoveReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        mGeofenceReceiver = new GeofenceReceiver();
        mGeofenceRemoveReceiver = new GeofenceRemoveReceiver();
        mGeofendcingClient = LocationServices.getGeofencingClient(this.getBaseContext());
        Intent intent = new Intent(this.getApplicationContext(), GeofenceTransitionsIntentService.class);
        mGeofencePendingIntent = PendingIntent.getService(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mGeofendcingClient.removeGeofences(mGeofencePendingIntent);
        Log.i(TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_GEOFENCES);
        registerReceiver(mGeofenceReceiver, intentFilter);
        intentFilter = new IntentFilter();
        intentFilter.addAction(REMOVE_GEOFENCES);
        registerReceiver(mGeofenceRemoveReceiver, intentFilter);

        return START_NOT_STICKY;
    }

    private Geofence buildGeofence(LatLng center, float radius){
        Geofence geofence = new Geofence.Builder()
                .setRequestId(center.toString())
                .setCircularRegion(
                        center.latitude,
                        center.longitude,
                        radius
                )
                .setExpirationDuration(Geofence. NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        return geofence;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint("MissingPermission")
    private void updateGeofences() {
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

    private GeofencingRequest getGeofencingRequest() {
        ArrayList<Geofence> geofencesList = new ArrayList<Geofence>(mGeofencingMap.values());
        builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofencesList);
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

    private class GeofenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LatLng center = intent.getExtras().getParcelable("Center");
            double radius = intent.getDoubleExtra("Radius",0);
            Geofence geofence = buildGeofence(center, (float) radius);

            if (mGeofencingMap.get(center) != null)
                mGeofencingMap.remove(center);

            mGeofencingMap.put(center, geofence);

            updateGeofences();
        }
    }

    private class GeofenceRemoveReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LatLng center = intent.getExtras().getParcelable("Center");
            mGeofencingMap.remove(center);
            ArrayList<String> list = new ArrayList<String>();
            list.add(center.toString());
            mGeofendcingClient.removeGeofences(list).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Geofence removed correctly");
                    } else {
                        Log.e(TAG, task.getException().toString());
                    }
                }
            });
        }
    }
}
