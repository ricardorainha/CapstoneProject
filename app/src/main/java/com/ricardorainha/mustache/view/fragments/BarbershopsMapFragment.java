package com.ricardorainha.mustache.view.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.FragmentBarbershopsMapBinding;
import com.ricardorainha.mustache.model.Barbershop;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarbershopsMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String MAP_BUNDLE = "MapViewBundle";
    private static final int DEFAULT_ZOOM_LEVEL = 15;
    private FragmentBarbershopsMapBinding binding;
    private BarbershopsViewModel viewModel;
    private GoogleMap mMap;

    public BarbershopsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_barbershops_map, container, false);
        viewModel = ViewModelProviders.of(this.getParentFragment()).get(BarbershopsViewModel.class);

        configureFields();

        Bundle mapBundle = null;
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAP_BUNDLE);
        }

        binding.map.onCreate(mapBundle);
        binding.map.getMapAsync(this);

        return binding.getRoot();
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapBundle = outState.getBundle(MAP_BUNDLE);
        if (mapBundle == null) {
            mapBundle = new Bundle();
            outState.putBundle(MAP_BUNDLE, mapBundle);
        }

        binding.map.onSaveInstanceState(mapBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                mMap.setOnCameraIdleListener(() -> {
                    binding.searchAreaButton.setVisibility(View.VISIBLE);
                    mMap.setOnCameraIdleListener(null);
                });
            }
        });

        mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));

        configureObservables();
    }

    private void configureFields() {
        binding.searchAreaButton.setOnClickListener(v -> {
            mMap.clear();
            Location location = new Location("");
            location.setLatitude(mMap.getCameraPosition().target.latitude);
            location.setLongitude(mMap.getCameraPosition().target.longitude);
            viewModel.getLastLocation().setValue(location);
            binding.searchAreaButton.setVisibility(View.GONE);
        });
    }

    private void configureObservables() {
        viewModel.getLocationPermissionGranted().observe(this, permissionGranted -> {
            if (mMap == null) {
                return;
            }

            try {
                mMap.setMyLocationEnabled(permissionGranted);
                mMap.getUiSettings().setMyLocationButtonEnabled(permissionGranted);
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        });

        viewModel.getLastLocation().observe(this, location -> {
            if (mMap == null) {
                return;
            }
            addReferenceMarker();
        });

        viewModel.getLoading().observe(this, loading -> {
            if (mMap != null) {
                mMap.getUiSettings().setAllGesturesEnabled(!loading);
            }
            binding.map.setClickable(!loading);
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getBarbershops().observe(this, barbershops -> updateMap(barbershops));
    }

    private void updateMap(List<Barbershop> barbershops) {
        if (mMap == null) {
            return;
        }

        mMap.clear();
        addReferenceMarker();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_bearded_man_black_48);

        for (Barbershop barbershop : barbershops) {
            mMap.addMarker(
                    new MarkerOptions()
                    .title(barbershop.getName())
                    .icon(icon)
                    .position(new LatLng(barbershop.getGeometry().getLocation().getLat(), barbershop.getGeometry().getLocation().getLng()))
            );
        }
    }

    private void addReferenceMarker() {
        Location lastLocation = viewModel.getLastLocation().getValue();
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions().title(getString(R.string.reference)).position(latLng));
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.map.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.map.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.map.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.map.onLowMemory();
    }
}
