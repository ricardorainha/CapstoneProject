package com.ricardorainha.mustache.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageReference;
import com.ricardorainha.mustache.utils.FirebaseUtils;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private HashMap<String, Barbershop> favorites = new HashMap<>();

    public User() { }

    public User(String uid, String name, String email, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(String uid, String name, String email, String phone, HashMap<String, Barbershop> favorites) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.favorites = favorites;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, Barbershop> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, Barbershop> favorites) {
        this.favorites = favorites;
    }

    @Exclude
    public void addFavorite(Barbershop barbershop) {
        if (this.favorites != null && !this.favorites.containsKey(barbershop.getPlaceId())) {
            this.favorites.put(barbershop.getPlaceId(), barbershop);
        }
    }

    @Exclude
    public void removeFavorite(Barbershop barbershop) {
        if (this.favorites != null && this.favorites.containsKey(barbershop.getPlaceId())) {
            this.favorites.remove(barbershop.getPlaceId());
        }
    }

    @Exclude
    public StorageReference getPhotoReference() {
        return FirebaseUtils.getProfilePhotoPath(getUid());
    }

    @Exclude
    public static User fromFirebaseUser(FirebaseUser firebaseUser) {
        return new User(firebaseUser.getUid(), firebaseUser.getDisplayName(),
                firebaseUser.getEmail(), firebaseUser.getPhoneNumber());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("phone", phone);
        hashMap.put("favorites", favorites);

        return hashMap;
    }

}
