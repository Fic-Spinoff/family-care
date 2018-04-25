package es.udc.apm.familycare.maps;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import es.udc.apm.familycare.R;


public class VipMapFragment extends CustomMapFragment {
    private static final String TAG = "GuardCustomMapFragment";
    private static final int MAX_CIRCLE_RADIUS = 500;
    private static final int DEFAULT_CIRCLE_RADIUS = 100;

    private HashMap<LatLng, Circle> mCircleHashMap = new HashMap<>();
    private GoogleMap mMap;

    private FloatingActionButton mAcceptButton;
    private FloatingActionButton mDeleteButton;
    private SeekBar mSeekBar;
    private Marker lastMarker;



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

        mAcceptButton.setOnClickListener(v -> hideButtonLayer());

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
        super.onMapReady(googleMap);

        mMap = googleMap;

        mMap.setOnMapClickListener(this::setMarker);

        mMap.setOnMarkerClickListener(this::modifyMarker);


    }


    private void addGeofenceFromCircle(Circle c) {
        Intent intent = new Intent(this.getContext(), GeofenceUpdateService.class);
        intent.putExtra("Radius", (float) c.getRadius());
        intent.putExtra("Center",c.getCenter());
        this.getActivity().startService(intent);
    }

    private void removeGeofenceFromCircle(Circle c) {
        Intent intent = new Intent(this.getActivity(), GeofenceRemoveService.class);
        intent.putExtra("Center",c.getCenter());
        this.getActivity().startService(intent);
    }



    public VipMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.inflateView(inflater, container, savedInstanceState);
        mAcceptButton = rootView.findViewById(R.id.button_accept);
        mDeleteButton = rootView.findViewById(R.id.button_delete);
        mSeekBar = rootView.findViewById(R.id.seekBar);
        mSeekBar.setMax(MAX_CIRCLE_RADIUS);
        return rootView;
    }

}
