package es.udc.apm.familycare;


import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.nio.channels.SeekableByteChannel;
import java.util.HashMap;


public class CustomMapFragment extends Fragment implements OnMapReadyCallback {

    private Boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private HashMap<LatLng, Circle> circleHashMap = new HashMap<LatLng, Circle>();
    private GoogleMap mMap;
    private MapView mMapView;
    private Location mLastKnownLocation = null;
    private CameraPosition mCameraPosition = null;
    private Button acceptButton;
    private Button deleteButton;
    private SeekBar seekBar;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = "CustomMapFragment";

    public CustomMapFragment(){
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.addMarker(new MarkerOptions().position(point));
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(point)
                        .radius(1000)
                        .strokeColor(Color.BLUE));
                circleHashMap.put(point, circle);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override

            public boolean onMarkerClick(Marker marker) {
                acceptButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                Circle c = circleHashMap.get(marker.getPosition());
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.remove();
                        marker.remove();
                        acceptButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                        seekBar.setVisibility(View.GONE);
                        seekBar.setOnSeekBarChangeListener(null);
                    }

                });
                seekBar.setProgress((int) c.getRadius());
                seekBar.setMax(1000);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                    {
                        c.setRadius(progress);
                    }

                    /**
                     * El usuario inicia la interacción con la Seekbar.
                     */
                    @Override
                    public void onStartTrackingTouch(SeekBar se)
                    {
                    }

                    /**
                     * El usuario finaliza la interacción con la Seekbar.
                     */
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0)
                    {
                    }
                });
                return true;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            getLocationPermission();
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
    /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */
        if (mLocationPermissionGranted) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if(location != null) {
                    mLastKnownLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                }
            });
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        acceptButton = rootView.findViewById(R.id.button_accept);
        deleteButton = rootView.findViewById(R.id.button_delete);
        seekBar = rootView.findViewById(R.id.seekBar);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                seekBar.setOnSeekBarChangeListener(null);
            }
        });

        acceptButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);


        mMapView = rootView.findViewById(R.id.mapView);
        if(mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
            mMapView.onResume();
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

}
