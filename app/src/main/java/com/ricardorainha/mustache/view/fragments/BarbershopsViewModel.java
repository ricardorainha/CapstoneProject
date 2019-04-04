package com.ricardorainha.mustache.view.fragments;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.ricardorainha.mustache.adapter.BarbershopsAdapter;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.LocationInfo;
import com.ricardorainha.mustache.model.Repository;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BarbershopsViewModel extends ViewModel implements Observer, BarbershopsAdapter.ActionCallback {

    private MutableLiveData<Boolean> locationPermissionGranted = new MutableLiveData<>();
    private MutableLiveData<List<Barbershop>> barbershops = new MutableLiveData<>();
    private MutableLiveData<BarbershopsAdapter> adapter = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Barbershop> selectedBarbershop = new MutableLiveData<>();
    private Repository repository;


    public BarbershopsViewModel() {
        repository = Repository.getInstance();
        repository.addObserver(this);

        locationPermissionGranted.setValue(false);
        LocationInfo.getInstance().getLastLocation().observeForever(location -> {
            loading.setValue(true);
            requestBarbershops();
        });
    }

    public MutableLiveData<Location> getLastLocation() {
        return LocationInfo.getInstance().getLastLocation();
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

    public MutableLiveData<Barbershop> getSelectedBarbershop() {
        return selectedBarbershop;
    }

    @Override
    public void update(Observable observable, Object response) {
        if (response instanceof List) {
            loading.setValue(false);
            barbershops.setValue((List<Barbershop>) response);
            adapter.setValue(new BarbershopsAdapter(barbershops.getValue(), this));
        }

    }

    public void onPermissionGranted(FusedLocationProviderClient flpClient) {
        getLocationPermissionGranted().setValue(true);
        LocationInfo.getInstance().getCurrentLocation(flpClient);
    }

    public void onPermissionDenied() {
        LocationInfo.getInstance().setDefaultLocation();
    }

    private void requestBarbershops() {
        repository.requestBarbershops(getLastLocation().getValue().getLatitude(), getLastLocation().getValue().getLongitude());
    }

    @Override
    public void onDetailsClicked(Barbershop barbershop) {
        this.selectedBarbershop.setValue(barbershop);
    }
}
