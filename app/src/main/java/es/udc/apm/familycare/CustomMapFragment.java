package es.udc.apm.familycare;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import es.udc.apm.familycare.maps.GeofenceService;
import es.udc.apm.familycare.maps.GeolocationService;


public class CustomMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "CustomMapFragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MAX_CIRCLE_RADIUS = 500;
    private static final int DEFAULT_CIRCLE_RADIUS = 100;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float SEARCH_ZOOM = 18f;

    private Boolean mLocationPermissionGranted = false;
    private HashMap<LatLng, Circle> mCircleHashMap = new HashMap<>();
    private GoogleMap mMap;
    private MapView mMapView;
    private Location mLastKnownLocation = null;
    private CameraPosition mCameraPosition = null;
    private FloatingActionButton mAcceptButton;
    private FloatingActionButton mDeleteButton;
    private SeekBar mSeekBar;
    private FusedLocationProviderClient mFusedLocationClient;
    private Marker lastMarker;

    @BindView(R.id.et_map_search)
    EditText etSearch;
    @BindView(R.id.btn_map_center)
    ImageView btnCenter;
    @BindView(R.id.btn_map_clear)
    ImageView btnClear;

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

    private void setMarker(LatLng point) {
        if (lastMarker != null) {
            mCircleHashMap.get(lastMarker.getPosition()).remove();
            lastMarker.remove();
        }

        lastMarker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(point)
                .radius(DEFAULT_CIRCLE_RADIUS)
                .strokeColor(Color.BLUE));
        showButtonLayer();

        mCircleHashMap.put(point, circle);

        mAcceptButton.setOnClickListener(v -> {
            lastMarker = null;
            hideButtonLayer();
            addGeofenceFromCircle(circle);
        });

        mDeleteButton.setOnClickListener(v -> {
            circle.remove();
            hideButtonLayer();
            lastMarker.remove();
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circle.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBar.setProgress((int) circle.getRadius());

    }

    private boolean modifyMarker(Marker marker) {
        if (lastMarker != null && !lastMarker.equals(marker)) {
            mCircleHashMap.get(lastMarker.getPosition()).remove();
            lastMarker.remove();
        }

        showButtonLayer();
        Circle c = mCircleHashMap.get(marker.getPosition());
        mDeleteButton.setOnClickListener(v -> {
            removeGeofenceFromCircle(c);
            c.remove();
            hideButtonLayer();
            marker.remove();
        });

        mAcceptButton.setOnClickListener(v -> {
            hideButtonLayer();
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                addGeofenceFromCircle(c);
            }
        });
        mSeekBar.setProgress((int) c.getRadius());
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this::setMarker);

        mMap.setOnMarkerClickListener(this::modifyMarker);

        updateLocationUI();

        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            this.getLocationPermission();
            if (this.mLocationPermissionGranted) {
                this.mMap.setMyLocationEnabled(true);
                this.btnCenter.setVisibility(View.VISIBLE);
            } else {
                this.mMap.setMyLocationEnabled(false);
                this.btnCenter.setVisibility(View.GONE);
                this.mLastKnownLocation = null;
            }
            // Custom location button
            this.mMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.mMap.getUiSettings().setCompassEnabled(false);
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), getResources().getString(R.string.error_location),
                    Toast.LENGTH_SHORT).show();
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

        if (mLocationPermissionGranted) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
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

    private void addGeofenceFromCircle(Circle c) {
        Intent intent = new Intent();
        intent.putExtra("Center",c.getCenter());
        intent.putExtra("Radius",c.getRadius());
        intent.setAction(GeofenceService.UPDATE_GEOFENCES);
        this.getActivity().sendBroadcast(intent);
    }

    private void removeGeofenceFromCircle(Circle c) {
        Intent intent = new Intent();
        intent.putExtra("Center",c.getCenter());
        intent.putExtra("Radius",c.getRadius());
        intent.setAction(GeofenceService.REMOVE_GEOFENCES);
        this.getActivity().sendBroadcast(intent);
    }


    public CustomMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mAcceptButton = rootView.findViewById(R.id.button_accept);
        mDeleteButton = rootView.findViewById(R.id.button_delete);
        mSeekBar = rootView.findViewById(R.id.seekBar);
        mSeekBar.setMax(MAX_CIRCLE_RADIUS);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        this.getActivity().startService(new Intent(this.getActivity(),GeolocationService.class));
        this.getActivity().startService(new Intent(this.getActivity(),GeofenceService.class));
        hideButtonLayer();

        mMapView = rootView.findViewById(R.id.mapView);

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
            mMapView.onResume();
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ButterKnife.bind(this, rootView);
        return rootView;
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

    @OnClick(R.id.et_map_search)
    void OnSearch() {
        dispatchSearch();
    }

    @OnFocusChange(R.id.et_map_search)
    void OnSearch(boolean focused) {
        if (focused) {
            dispatchSearch();
        }
    }

    @OnTextChanged(R.id.et_map_search)
    void OnSearchChanged() {
        if (etSearch.length() > 0) {
            btnClear.setVisibility(View.VISIBLE);
        } else {
            btnClear.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_map_center)
    void OnClickCenter() {
        this.getDeviceLocation();
    }

    @OnClick(R.id.btn_map_clear)
    void OnClickClear() {
        etSearch.setText("");
    }

    private void dispatchSearch() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    //.setBoundsBias()
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Log.e(TAG, e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Place selected
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                // Set search text
                CharSequence name = place.getAddress() != null ? place.getAddress() :
                        place.getName();
                etSearch.setText(name);
                // Go to location
                // TODO: Markers without zones
                // this.setMarker(place.getLatLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), SEARCH_ZOOM));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // Error selecting place
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Toast.makeText(getActivity(), getResources().getString(R.string.error_places),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d(TAG, "User cancel place");
            }
        }
    }
}
