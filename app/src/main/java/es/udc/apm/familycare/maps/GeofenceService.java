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

import es.udc.apm.familycare.CustomMapFragment;


public class GeofenceService extends Service {
    private static String TAG = "GeofenceService";

    public final static String UPDATE_GEOFENCES = "UPDATE_GEOFENCES";

    private GeofencingClient mGeofendcingClient = null;
    private PendingIntent mGeofencePendingIntent;
    private static HashMap<LatLng, Geofence> mGeofencingMap = new HashMap<LatLng, Geofence>();

    GeofenceReceiver geofenceReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        geofenceReceiver = new GeofenceReceiver();
        mGeofendcingClient = LocationServices.getGeofencingClient(this.getApplicationContext());

        Log.i(TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_GEOFENCES);
        registerReceiver(geofenceReceiver, intentFilter);
        Log.i(TAG, "Service Started");
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class GeofenceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mGeofencingMap = CustomMapFragment.getGeofences();
            updateGeofences();
        }
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
        ArrayList<Geofence> list = new ArrayList<Geofence>(mGeofencingMap.values());
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(list);
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
