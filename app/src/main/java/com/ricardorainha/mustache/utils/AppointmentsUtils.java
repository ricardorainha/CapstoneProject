package com.ricardorainha.mustache.utils;

import com.ricardorainha.mustache.model.Appointment;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Session;

import java.util.Calendar;

public class AppointmentsUtils {

    public static void schedule(Calendar date, Barbershop barbershop) {
        Appointment appointment = new Appointment(date.getTimeInMillis(), System.currentTimeMillis(), barbershop, false);
        Session.getInstance().getUser().getValue().addAppointment(appointment);
        FirebaseUtils.updateUserInfo();
    }

}
