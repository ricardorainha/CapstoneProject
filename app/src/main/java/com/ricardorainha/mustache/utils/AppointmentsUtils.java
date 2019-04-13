package com.ricardorainha.mustache.utils;

import com.ricardorainha.mustache.model.Appointment;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Session;

import java.util.Calendar;

public class AppointmentsUtils {

    public static void schedule(Calendar date, Barbershop barbershop) {
        Appointment appointment = new Appointment(date.getTimeInMillis(), System.currentTimeMillis(), barbershop, false, false);
        Session.getInstance().getUser().getValue().addAppointment(appointment);
        FirebaseUtils.updateUserInfo();
    }

    public static void confirm(Appointment appointment) {
        Session.getInstance().getUser().getValue().confirmAppointment(appointment.getTimeString());
        FirebaseUtils.updateUserInfo();
    }

    public static void cancel(Appointment appointment) {
        Session.getInstance().getUser().getValue().cancelAppointment(appointment.getTimeString());
        FirebaseUtils.updateUserInfo();
    }

}
