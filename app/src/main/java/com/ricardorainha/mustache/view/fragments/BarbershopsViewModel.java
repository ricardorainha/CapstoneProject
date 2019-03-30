package com.ricardorainha.mustache.view.fragments;

import android.location.Location;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class BarbershopsViewModel extends ViewModel {

    private ObservableField<Location> lastLocation = new ObservableField<>();
    private ObservableField<Boolean> locationPermissionGranted = new ObservableField<>();

    public static Location DEFAULT_LOCATION = new Location("");
    static {
        DEFAULT_LOCATION.setLatitude(-23.612930);
        DEFAULT_LOCATION.setLongitude(-46.698747);
    }

    public BarbershopsViewModel() {
        locationPermissionGranted.set(false);
    }

    public ObservableField<Location> getLastLocation() {
        return lastLocation;
    }

    public ObservableField<Boolean> getLocationPermissionGranted() {
        return locationPermissionGranted;
    }
}
