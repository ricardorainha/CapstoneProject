package com.ricardorainha.mustache.view.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ricardorainha.mustache.R;

import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarbershopsMapFragment extends Fragment implements OnMapReadyCallback {

    private BarbershopsViewModel viewModel;
    private GoogleMap mMap;

    public BarbershopsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barbershops_map, container, false);
        viewModel = ViewModelProviders.of(this.getParentFragment()).get(BarbershopsViewModel.class);
        configureObservables();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                mMap.setOnCameraIdleListener(() -> {
                    Toast.makeText(getContext(), "Camera: " + mMap.getCameraPosition(), Toast.LENGTH_SHORT).show();
                    mMap.setOnCameraIdleListener(null);
                });
            }
        });
    }

    private void configureObservables() {
        viewModel.getLocationPermissionGranted().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (mMap == null) {
                    return;
                }

                try {
                    boolean granted = viewModel.getLocationPermissionGranted().get();
                    mMap.setMyLocationEnabled(granted);
                    mMap.getUiSettings().setMyLocationButtonEnabled(granted);
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        viewModel.getLastLocation().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (mMap == null) {
                    return;
                }

                Location lastLocation = viewModel.getLastLocation().get();
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(new MarkerOptions().title("Your Location").position(latLng).snippet("This is your location"));
            }
        });
    }
}
