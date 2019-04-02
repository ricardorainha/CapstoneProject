package com.ricardorainha.mustache.view.fragments;

import android.location.Location;

import com.ricardorainha.mustache.adapter.BarbershopsAdapter;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Repository;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BarbershopsViewModel extends ViewModel implements Observer {

    private MutableLiveData<Location> lastLocation = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationPermissionGranted = new MutableLiveData<>();
    private MutableLiveData<List<Barbershop>> barbershops = new MutableLiveData<>();
    private MutableLiveData<BarbershopsAdapter> adapter = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private Repository repository;

    public static Location DEFAULT_LOCATION = new Location("");
    static {
        DEFAULT_LOCATION.setLatitude(-23.612930);
        DEFAULT_LOCATION.setLongitude(-46.698747);
    }

    public BarbershopsViewModel() {
        repository = Repository.getInstance();
        repository.addObserver(this);

        locationPermissionGranted.setValue(false);
        lastLocation.observeForever(location -> {
            loading.setValue(true);
            requestBarbershops();
        });
    }

    public MutableLiveData<Location> getLastLocation() {
        return lastLocation;
    }

    public MutableLiveData<Boolean> getLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<List<Barbershop>> getBarbershops() {
        return barbershops;
    }

    public MutableLiveData<BarbershopsAdapter> getAdapter() {
        return adapter;
    }

    @Override
    public void update(Observable observable, Object response) {
        if (response instanceof List) {
            loading.setValue(false);
            barbershops.setValue((List<Barbershop>) response);
            adapter.setValue(new BarbershopsAdapter(barbershops.getValue()));
        }

    }

    private void requestBarbershops() {
        repository.requestBarbershops(lastLocation.getValue().getLatitude(), lastLocation.getValue().getLongitude());
    }
}
