package com.ricardorainha.mustache.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageReference;
import com.ricardorainha.mustache.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private HashMap<String, Barbershop> favorites = new HashMap<>();
    private HashMap<String, Appointment> appointments = new HashMap<>();

    public User() { }

    public User(String uid, String name, String email, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(String uid, String name, String email, String phone, HashMap<String, Barbershop> favorites, HashMap<String, Appointment> appointments) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.favorites = favorites;
        this.appointments = appointments;
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

    public HashMap<String, Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(HashMap<String, Appointment> appointments) {
        this.appointments = appointments;
    }

    @Exclude
    public void addFavorite(Barbershop barbershop) {
        if (this.favorites != null && !this.favorites.containsKey(barbershop.getPlaceId())) {
            this.favorites.put(barbershop.getPlaceId(), barbershop);
        }
    }

    @Exclude
    public void removeFavorite(String placeId) {
        if (this.favorites != null) {
            this.favorites.remove(placeId);
        }
    }

    @Exclude
    public void addAppointment(Appointment appointment) {
        if (this.appointments != null && !this.appointments.containsKey(appointment.getTime())) {
            this.appointments.put(appointment.getTimeString(), appointment);
        }
    }

    @Exclude
    public void confirmAppointment(String time) {
        if (this.appointments != null && this.appointments.containsKey(time)) {
            this.appointments.get(time).setConfirmed(true);
        }
    }

    @Exclude
    public void cancelAppointment(String time) {
        if (this.appointments != null && this.appointments.containsKey(time)) {
            this.appointments.get(time).setCanceled(true);
        }
    }

    @Exclude
    public Appointment getNextAppointment() {
        long currentTimeInMillis = System.currentTimeMillis();
        List<Appointment> appointments = new ArrayList<>(getAppointments().values());
        Collections.sort(appointments, (a1, a2) -> Long.compare(a2.getTime(), a1.getTime()));

        Appointment nextAppointment = null;
        for (Appointment appointment : appointments) {
            if (appointment.getTime() > currentTimeInMillis) {
                if (!appointment.isCanceled()) {
                    nextAppointment = appointment;
                }
            }
            else {
                break;
            }
        }

        return nextAppointment;
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
        hashMap.put("appointments", appointments);

        return hashMap;
    }

}
