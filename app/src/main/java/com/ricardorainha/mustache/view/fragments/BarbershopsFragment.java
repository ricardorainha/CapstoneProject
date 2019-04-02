package com.ricardorainha.mustache.view.fragments;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.adapter.BarbershopsPagerAdapter;
import com.ricardorainha.mustache.databinding.FragmentBarbershopsBinding;
import com.ricardorainha.mustache.model.Session;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarbershopsFragment extends Fragment {

    private FragmentBarbershopsBinding binding;
    private BarbershopsViewModel viewModel;
    private FusedLocationProviderClient flpClient;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3217;


    public BarbershopsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_barbershops, container, false);
        viewModel = ViewModelProviders.of(this).get(BarbershopsViewModel.class);

        flpClient = LocationServices.getFusedLocationProviderClient(getContext());
        configureFields();
        getLocationPermission();

        return binding.getRoot();
    }

    private void configureFields() {
        binding.barbershopsPager.setAdapter(new BarbershopsPagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.barbershops_tabs_titles)));
        binding.barbershopsTabs.setupWithViewPager(binding.barbershopsPager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processPermissionGranted();
            }
            else {
                setDefaultLocation();
            }
        }
    }

    private void getCurrentLocation() {
        try {
            Task<Location> locationResult = flpClient.getLastLocation();
            locationResult.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    viewModel.getLastLocation().setValue(task.getResult());
                } else {
                    setDefaultLocation();
                }
            });
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void processPermissionGranted() {
        viewModel.getLocationPermissionGranted().setValue(true);
        getCurrentLocation();
    }

    private void setDefaultLocation() {
        viewModel.getLastLocation().setValue(viewModel.DEFAULT_LOCATION);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            processPermissionGranted();
        } else if (!Session.getInstance().hasAskedForPermissions()){
            Session.getInstance().setAskedForPermissions(true);
            this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            setDefaultLocation();
        }
    }
}
