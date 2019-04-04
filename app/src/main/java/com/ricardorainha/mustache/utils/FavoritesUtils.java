package com.ricardorainha.mustache.utils;

import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Repository;
import com.ricardorainha.mustache.model.Session;

import java.util.Observable;
import java.util.Observer;

public class FavoritesUtils {

    public static void addFavorite(Barbershop barbershop) {
        Repository repo = Repository.getInstance();
        repo.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg instanceof Barbershop) {
                    Session.getInstance().getUser().getValue().addFavorite((Barbershop) arg);
                    FirebaseUtils.updateUserInfo();
                    repo.deleteObserver(this);
                }
            }
        });
        repo.requestBarbershopDetails(barbershop);

    }

    public static void removeFavorite(String placeId) {
        Session.getInstance().getUser().getValue().removeFavorite(placeId);
        FirebaseUtils.updateUserInfo();
    }

    public static boolean isFavorite(Barbershop barbershop) {
        return Session.getInstance().getUser().getValue().getFavorites().containsKey(barbershop.getPlaceId());
    }

}
