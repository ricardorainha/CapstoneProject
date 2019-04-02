package com.ricardorainha.mustache.utils;

import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Session;

public class FavoritesUtils {

    public static void addFavorite(Barbershop barbershop) {
        Session.getInstance().getUser().getValue().addFavorite(barbershop);
        FirebaseUtils.updateUserInfo();
    }

    public static void removeFavorite(Barbershop barbershop) {
        Session.getInstance().getUser().getValue().removeFavorite(barbershop);
        FirebaseUtils.updateUserInfo();
    }

    public static boolean isFavorite(Barbershop barbershop) {
        return Session.getInstance().getUser().getValue().getFavorites().containsKey(barbershop.getPlaceId());
    }

}
