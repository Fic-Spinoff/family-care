package es.udc.apm.familycare.maps;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
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


public class GeofenceRemoveService extends IntentService {
    private static String TAG = "GeofenceUpdateService";

    private GeofencingClient mGeofendcingClient = null;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingRequest.Builder builder = new GeofencingRequest.Builder();


    public GeofenceRemoveService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LatLng center = intent.getExtras().getParcelable("Center");
        mGeofendcingClient = LocationServices.getGeofencingClient(this.getBaseContext());
        ArrayList<String> list = new ArrayList<String>();
        list.add((GeofenceStore.getInstance()).getGeofence(center).getRequestId());
        mGeofendcingClient.removeGeofences(list);
    }
}
