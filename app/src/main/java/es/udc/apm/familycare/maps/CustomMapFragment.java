package es.udc.apm.familycare.maps;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import es.udc.apm.familycare.R;


public abstract class CustomMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "CustomMapFragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float SEARCH_ZOOM = 18f;

    private FloatingActionButton mAcceptButton;
    private FloatingActionButton mDeleteButton;
    private SeekBar mSeekBar;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private MapView mMapView;
    private Location mLastKnownLocation = null;
    private CameraPosition mCameraPosition = null;
    private Marker searchMarker = null;

    private FusedLocationProviderClient mFusedLocationClient;

    @BindView(R.id.et_map_search)
    EditText etSearch;
    @BindView(R.id.btn_map_center)
    ImageView btnCenter;
    @BindView(R.id.btn_map_clear)
    ImageView btnClear;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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


        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }


    private void hideButtonLayer() {
        mAcceptButton.setVisibility(View.GONE);
        mDeleteButton.setVisibility(View.GONE);
        mSeekBar.setVisibility(View.GONE);
        mSeekBar.setOnSeekBarChangeListener(null);
    }

    public CustomMapFragment() {
    }

    public View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mAcceptButton = rootView.findViewById(R.id.button_accept);
        mDeleteButton = rootView.findViewById(R.id.button_delete);
        mSeekBar = rootView.findViewById(R.id.seekBar);
        hideButtonLayer();

        this.getActivity().startService(new Intent(this.getActivity(),GeolocationService.class));

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
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(),e.getConnectionStatusCode(),PLACE_AUTOCOMPLETE_REQUEST_CODE).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                CharSequence name = place.getAddress() != null ? place.getAddress() :
                        place.getName();
                etSearch.setText(name);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), SEARCH_ZOOM));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Toast.makeText(getActivity(), getResources().getString(R.string.error_places),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, status.getStatusMessage());
                searchMarker.remove();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "User cancel place");
            }
        }
    }
}
