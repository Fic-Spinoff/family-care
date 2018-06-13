package es.udc.apm.familycare.maps;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import es.udc.apm.familycare.FamilyCare;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.model.Geofence;
import es.udc.apm.familycare.repository.GeofenceRepository;

public class VipMapFragment extends CustomMapFragment {
    private static final String TAG = "GuardCustomMapFragment";
    private static final int MAX_CIRCLE_RADIUS = 500;
    private static final int MIN_CIRCLE_RADIUS = 50;
    private static final int DEFAULT_CIRCLE_RADIUS = 100;

    private HashMap<String, Circle> mCircleHashMap = new HashMap<>();
    private HashMap<String, Geofence> mGeoFenceMap = new HashMap<>();
    private GoogleMap mMap;

    private FloatingActionButton mAcceptButton;
    private FloatingActionButton mDeleteButton;
    private SeekBar mSeekBar;
    private Marker lastMarker;

    private FusedLocationProviderClient mFusedLocationClient;

    private GeofenceRepository mRepo = null;
    private String uid = null;

    public VipMapFragment() {
    }

    private void showButtonLayer() {
        mAcceptButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.VISIBLE);
    }

    private void hideButtonLayer() {
        mAcceptButton.setVisibility(View.GONE);
        mDeleteButton.setVisibility(View.GONE);
        mSeekBar.setVisibility(View.GONE);
        mSeekBar.setOnSeekBarChangeListener(null);
    }

    private Marker newMarker(LatLng point) {
        return mMap.addMarker(new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private Circle newCircle(LatLng point, float radius) {
        return mMap.addCircle(new CircleOptions()
                .center(point)
                .radius(radius)
                .fillColor(getResources().getColor(R.color.mapFill))
                .strokeColor(getResources().getColor(R.color.mapStroke))
                .strokeWidth(4f));
    }

    private void setMarker(LatLng point, float radius) {
        if (lastMarker != null) {
            //mCircleHashMap.get(lastMarker.getId()).remove();
            lastMarker.remove();
        }
        super.removeSearchMarker();

        lastMarker = newMarker(point);
        Circle circle = newCircle(point, radius);
        showButtonLayer();

        //mCircleHashMap.put(lastMarker.getId(), circle);

        // On click save
        mAcceptButton.setOnClickListener(v -> {
            hideButtonLayer();
            if (this.uid != null) {
                // Save in firebase
                this.mRepo.createGeofence(this.uid, new Geofence(
                        circle.getCenter(),
                        (float) circle.getRadius()
                )).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addGeofenceFromCircle(circle);
                        // Save circle in map
                        mCircleHashMap.put(this.lastMarker.getId(), circle);
                        // Save Geofence
                        mGeoFenceMap.put(this.lastMarker.getId(), new Geofence(
                                task.getResult().getId(),
                                circle.getCenter(),
                                (float) circle.getRadius())
                        );
                        // Clear last marker ref
                        this.lastMarker = null;
                    } else {
                        this.errorSaving(circle);
                    }
                });
            } else {
                this.errorSaving(circle);
            }
        });

        mDeleteButton.setOnClickListener(v -> {
            circle.remove();
            hideButtonLayer();
            lastMarker.remove();
        });

        mSeekBar.setProgress((int) circle.getRadius());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && progress < MIN_CIRCLE_RADIUS) {
                    mSeekBar.setProgress(MIN_CIRCLE_RADIUS);
                } else {
                    circle.setRadius(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void errorSaving(Circle circle) {
        Toast.makeText(getActivity(), "Error saving zone", Toast.LENGTH_SHORT).show();
        circle.remove();
        lastMarker.remove();
        lastMarker = null;
    }

    private boolean modifyMarker(Marker marker) {
        if (lastMarker != null && !lastMarker.equals(marker)) {
                //mCircleHashMap.get(lastMarker.getId()).remove();
                lastMarker.remove();
        }

        // If search marker replace with a geofence marker
        if (super.isSearchMarker(marker)){
            LatLng lt = marker.getPosition();
            marker.remove();
            setMarker(lt, DEFAULT_CIRCLE_RADIUS);
            return true;
        }

        showButtonLayer();
        Circle circle = mCircleHashMap.get(marker.getId());
        mDeleteButton.setOnClickListener(v -> {
            hideButtonLayer();
            // Remove from api
            removeGeofenceFromCircle(circle);
            // Remove circle
            circle.remove();
            //remove marker
            marker.remove();
            // delete from firebase
            if (this.uid != null) {
                this.mRepo.deleteGeofence(this.uid, mGeoFenceMap.get(marker.getId()).getUid());
            }
            //remove geofence
            this.mGeoFenceMap.remove(marker.getId());
        });

        mAcceptButton.setOnClickListener(v -> {
            // Remove current and add new with changes
            GeofenceStore.getInstance().removeGeofence(circle.getCenter());
            hideButtonLayer();
            // Update in firebase
            if (this.uid != null) {
                // Update in local
                Geofence geofence = this.mGeoFenceMap.get(marker.getId());
                geofence.setRadius((float) circle.getRadius());
                // Update in firebase
                this.mRepo.setGeofence(this.uid, geofence.getUid(), geofence);
                addGeofenceFromCircle(circle);
                // Update circle in map
                mCircleHashMap.put(marker.getId(), circle);
            } else {
                this.errorSaving(circle);
            }
        });

        mSeekBar.setProgress((int) circle.getRadius());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && progress < MIN_CIRCLE_RADIUS) {
                    mSeekBar.setProgress(MIN_CIRCLE_RADIUS);
                } else {
                    circle.setRadius(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return true;
    }

    private void addGeofenceFromCircle(Circle c) {
        Intent intent = new Intent(this.getContext(), GeofenceUpdateService.class);
        GeofenceStore.getInstance().addGeofence(c.getCenter(), (float) c.getRadius());
        this.getActivity().startService(intent);
    }

    @SuppressLint("MissingPermission")
    private void removeGeofenceFromCircle(Circle c) {
        Intent intent = new Intent(this.getActivity(), GeofenceUpdateService.class);
        GeofenceStore.getInstance().removeGeofence(c.getCenter());
        this.getActivity().startService(intent);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Intent intentDelete = new Intent(this.getActivity(), GeofenceTransitionsIntentService.class);
                intentDelete.putExtra("com.google.android.location.intent.extra.triggering_location", location);
                this.getActivity().startService(intentDelete);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mMap = googleMap;

        mMap.setOnMapClickListener((point) -> setMarker(point, DEFAULT_CIRCLE_RADIUS));

        mMap.setOnMarkerClickListener(this::modifyMarker);

        FamilyCare.getUser().observe(this, user -> {
            if (user != null) {
                this.uid = user.getUid();
                // Load markers
                if (this.mRepo != null) {
                    this.mRepo.getAllGeofences(this.uid).observe(this, geofences -> {
                        if (geofences != null && geofences.size() > 0){
                            GeofenceStore.getInstance().removeAll();
                            for (Geofence geofence : geofences) {
                                // Add to local store
                                GeofenceStore.getInstance().addGeofence(
                                        geofence.getCenter(),
                                        geofence.getRadius()
                                );
                                // Show in map
                                Marker marker = newMarker(geofence.getCenter());
                                Circle circle = newCircle(geofence.getCenter(), geofence.getRadius());
                                this.mCircleHashMap.put(marker.getId(), circle);
                                this.mGeoFenceMap.put(marker.getId(), geofence);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Load repo
        this.mRepo = new GeofenceRepository();

        // Load view
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        View rootView = super.inflateView(inflater, container, savedInstanceState);
        mAcceptButton = rootView.findViewById(R.id.button_accept);
        mDeleteButton = rootView.findViewById(R.id.button_delete);
        mSeekBar = rootView.findViewById(R.id.seekBar);
        mSeekBar.setMax(MAX_CIRCLE_RADIUS);

        return rootView;
    }

}
