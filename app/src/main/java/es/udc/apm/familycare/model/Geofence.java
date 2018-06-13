package es.udc.apm.familycare.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

public class Geofence {

    @Exclude
    private String uid;

    private Center center;

    private float radius;

    public Geofence(){

    }

    public Geofence(LatLng center, float radius) {
        this.center = new Center();
        this.center.latitude = center.latitude;
        this.center.longitude = center.longitude;
        this.radius = radius;
    }

    public Geofence(String uid, LatLng center, float radius) {
        this.uid = uid;
        this.center = new Center();
        this.center.latitude = center.latitude;
        this.center.longitude = center.longitude;
        this.radius = radius;
    }

    public Geofence withId(String uid) {
        this.uid = uid;
        return this;
    }

    public LatLng getCenter() {
        return new LatLng(this.center.latitude, this.center.longitude);
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static class Center {
        private double latitude;
        private double longitude;

        public Center() {

        }

        public Center(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
