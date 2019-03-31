package com.ricardorainha.mustache.view.fragments;

import android.location.Location;

import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Repository;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BarbershopsViewModel extends ViewModel implements Observer {

    private ObservableField<Location> lastLocation = new ObservableField<>();
    private ObservableField<Boolean> locationPermissionGranted = new ObservableField<>();
    private MutableLiveData<List<Barbershop>> barbershops = new MutableLiveData<>();
    private Repository repository;

    public static Location DEFAULT_LOCATION = new Location("");
    static {
        DEFAULT_LOCATION.setLatitude(-23.612930);
        DEFAULT_LOCATION.setLongitude(-46.698747);
    }

    public BarbershopsViewModel() {
        repository = Repository.getInstance();
        repository.addObserver(this);

        locationPermissionGranted.set(false);
        lastLocation.addOnPropertyChangedCallback(new androidx.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(androidx.databinding.Observable sender, int propertyId) {
                requestBarbershops();
            }
        });
    }

    public ObservableField<Location> getLastLocation() {
        return lastLocation;
    }

    public ObservableField<Boolean> getLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    public MutableLiveData<List<Barbershop>> getBarbershops() {
        return barbershops;
    }

    @Override
    public void update(Observable observable, Object response) {
        if (response instanceof List) {
            barbershops.setValue((List<Barbershop>) response);
        }

    }

    private void requestBarbershops() {
        repository.requestBarbershops(lastLocation.get().getLatitude(), lastLocation.get().getLongitude());
    }
}
