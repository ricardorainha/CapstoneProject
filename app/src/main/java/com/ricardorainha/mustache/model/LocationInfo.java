package com.ricardorainha.mustache.model;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Task;

import androidx.lifecycle.MutableLiveData;

public class LocationInfo {

    private static final LocationInfo ourInstance = new LocationInfo();
    private static final String DIRECTIONS_URL = "https://www.google.com/maps/dir/?api=1&origin=%s,%s&destination=%s&destination_place_id=%s";
    private static Location DEFAULT_LOCATION = new Location("");
    static {
        DEFAULT_LOCATION.setLatitude(-23.612930);
        DEFAULT_LOCATION.setLongitude(-46.698747);
    }

    private MutableLiveData<Location> lastLocation = new MutableLiveData<>();
    private boolean userLocationDefined = false;

    public static LocationInfo getInstance() {
        return ourInstance;
    }

    private LocationInfo() {
    }

    public MutableLiveData<Location> getLastLocation() {
        return lastLocation;
    }

    public void setDefaultLocation() {
        getLastLocation().setValue(DEFAULT_LOCATION);
    }

    public void getCurrentLocation(FusedLocationProviderClient flpClient) {
        try {
            if (!userLocationDefined) {
                Task<Location> locationResult = flpClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getLastLocation().setValue(task.getResult());
                    } else {
                        setDefaultLocation();
                    }
                });
                userLocationDefined = true;
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public Intent getDirectionsIntent(Barbershop barbershop) {
        String url = String.format(DIRECTIONS_URL,
                String.valueOf(getLastLocation().getValue().getLatitude()),
                String.valueOf(getLastLocation().getValue().getLongitude()),
                Uri.encode(barbershop.getName()),
                barbershop.getPlaceId());

        Intent directionsIntent = new Intent();
        directionsIntent.setAction(Intent.ACTION_VIEW);
        directionsIntent.setData(Uri.parse(url));

        return directionsIntent;
    }

    public interface LocationDefinedListener {
        void onLocationDefined();
    }
}
