package es.udc.apm.familycare.maps;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class GeofenceRemoveService extends IntentService {
    private static String TAG = "GeofenceUpdateService";

    private GeofencingClient mGeofendcingClient = null;


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
