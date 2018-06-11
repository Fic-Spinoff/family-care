package es.udc.apm.familycare.maps;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class GeofenceStore{
    private static HashMap<LatLng, Float> geofenceData = new HashMap<LatLng, Float>();
    private static GeofenceStore mGeofenceStore;

    private GeofenceStore(){
    }

    public static GeofenceStore getInstance() {
        if (mGeofenceStore == null)
            mGeofenceStore = new GeofenceStore();
        return mGeofenceStore;
    }

    public ArrayList<Geofence> getGeofenceList() {
        ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
        for (LatLng center : geofenceData.keySet())
        {
            Geofence g = getGeofence(center, geofenceData.get(center));
            geofenceList.add(g);
        }
        return geofenceList;
    }

    public Geofence getGeofence(LatLng center, float radius) {
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

    public void addGeofence(LatLng center, float radius) {
        geofenceData.put(center, radius);
    }

    public void removeGeofence(LatLng center){
        geofenceData.remove(center);
    }

    public void updateGeofence(LatLng center, float radius) {
        if (geofenceData.containsKey(center)) {
            geofenceData.remove(center);
        }
        geofenceData.put(center, radius);
    }

    public float getRadius(LatLng center){
        return geofenceData.get(center);
    }

    public HashMap<LatLng, Float> getGeofenceData(){
        return geofenceData;
    }
}