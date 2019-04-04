package com.ricardorainha.mustache.model;

import com.ricardorainha.mustache.network.BarbershopsDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Repository extends Observable implements Observer {
    private static final Repository ourInstance = new Repository();

    private static List<Barbershop> barbershops = new ArrayList();

    public static Repository getInstance() {
        return ourInstance;
    }

    private BarbershopsDB requester;

    private Repository() {
        requester = new BarbershopsDB();
        requester.addObserver(this);
    }

    public void requestBarbershops(double latitude, double longitude) {
        requester.getBarbershops(latitude, longitude);
    }

    public void requestBarbershopDetails(Barbershop barbershop) {
        requester.getBarbershopDetails(barbershop);
    }

    @Override
    public void update(Observable observable, Object response) {
        if (response instanceof BarbershopsResult) {
            barbershops = ((BarbershopsResult) response).getBarbershops();
            setChanged();
            notifyObservers(barbershops);
        }
        else if (response instanceof Barbershop) {
            Barbershop details = (Barbershop) response;
            setChanged();
            notifyObservers(details);
        }
        else if (response instanceof Throwable) {
            // TODO: handle exception
        }
        else if (response == null) {
            // TODO: handle call error
        }
    }
}
