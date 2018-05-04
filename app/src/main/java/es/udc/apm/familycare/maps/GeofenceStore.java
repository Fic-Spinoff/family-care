package es.udc.apm.familycare.maps;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class GeofenceStore{
    private static HashMap<LatLng, Geofence> geofenceList = new HashMap<LatLng, Geofence>();
    private static GeofenceStore mGeofenceStore;

    private GeofenceStore(){
    }

    public static GeofenceStore getInstance() {
        if (mGeofenceStore == null)
            mGeofenceStore = new GeofenceStore();
        return mGeofenceStore;
    }

    public ArrayList<Geofence> getGeofenceList() {
        return new ArrayList<>(geofenceList.values());
    }

    public Geofence getGeofence(LatLng center) {
        return geofenceList.get(center);
    }

    public void addGeofence(LatLng center, float radius) {
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
        geofenceList.put(center, geofence);
    }

    public Geofence removeGeofence(LatLng center){
        Geofence g = geofenceList.get(center);
        geofenceList.remove(center);
        return g;
    }

    public void updateGeofence(LatLng center, float radius) {
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
        if (geofenceList.containsKey(center))
            geofenceList.remove(center);
        geofenceList.put(center, geofence);
    }
}