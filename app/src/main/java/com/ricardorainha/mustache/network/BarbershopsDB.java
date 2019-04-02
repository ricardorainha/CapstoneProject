package com.ricardorainha.mustache.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ricardorainha.mustache.model.BarbershopsResult;

import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * This class has information to retrieve information from Google Places, simulating
 * request that would be from the app's server-side (that will not be implemented on this project) *
 */

public class BarbershopsDB extends Observable {

    // https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCASb5AsmJkIBSOdJUJZXDrUjzIaci2B5Q&query=barbearias&location=-23.63,-46.73&radius=3000
    public static final String API_BASE_URL = "https://maps.googleapis.com/maps/api/";
    public static final String MAPS_API_KEY = "AIzaSyCASb5AsmJkIBSOdJUJZXDrUjzIaci2B5Q";
    private static final int DEFAULT_RADIUS = 3000;
    private static double lastLatitude;
    private static double lastLongitude;
    private static BarbershopsResult bsResult;
    private static Endpoints barbershopsAPI;

    public BarbershopsDB() {
        barbershopsAPI = getAPI();
    }

    public interface Endpoints {
        @GET("place/textsearch/json?key=" + MAPS_API_KEY + "&query=barbearias&radius=" + DEFAULT_RADIUS)
        Call<BarbershopsResult> getBarbershops(@Query("location") String location);
    }

    public static Endpoints getAPI() {
        if (barbershopsAPI == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            barbershopsAPI = retrofit.create(Endpoints.class);
        }

        return barbershopsAPI;
    }

    public void getBarbershops(double latitude, double longitude) {
        if (barbershopsAPI != null) {

            if ((latitude != lastLatitude) && (longitude != lastLongitude)) {
                lastLatitude = latitude;
                lastLongitude = longitude;

                StringBuilder location = new StringBuilder().append(latitude).append(",").append(longitude);

                Call<BarbershopsResult> call = barbershopsAPI.getBarbershops(location.toString());
                call.enqueue(new Callback<BarbershopsResult>() {
                    @Override
                    public void onResponse(Call<BarbershopsResult> call, Response<BarbershopsResult> response) {
                        if (response.isSuccessful()) {
                            bsResult = response.body();
                            notifyBarbershopsObservers();
                        }
                        else {
                            setChanged();
                            notifyObservers();
                        }
                    }

                    @Override
                    public void onFailure(Call<BarbershopsResult> call, Throwable t) {
                        setChanged();
                        notifyObservers(t);
                    }
                });
            }
            else {
                notifyBarbershopsObservers();
            }
        }
    }

    private void notifyBarbershopsObservers() {
        setChanged();
        notifyObservers(bsResult);
    }

}
