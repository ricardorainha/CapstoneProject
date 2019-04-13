package com.ricardorainha.mustache.view.fragments;

import com.ricardorainha.mustache.adapter.AppointmentsAdapter;
import com.ricardorainha.mustache.model.Appointment;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.model.User;
import com.ricardorainha.mustache.utils.AppointmentsUtils;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyScheduleViewModel extends ViewModel implements AppointmentsAdapter.ClickListener {

    private MutableLiveData<AppointmentsAdapter> adapter = new MutableLiveData<>();
    private MutableLiveData<Appointment> directionsAppointment = new MutableLiveData<>();
    private MutableLiveData<Appointment> appointmentToCancel = new MutableLiveData<>();

    public MyScheduleViewModel() {
        Session.getInstance().getUser().observeForever(user -> updateAdapter(user));
    }

    public MutableLiveData<AppointmentsAdapter> getAdapter() {
        return adapter;
    }

    public MutableLiveData<Appointment> getDirectionsAppointment() {
        return directionsAppointment;
    }

    public MutableLiveData<Appointment> getAppointmentToCancel() {
        return appointmentToCancel;
    }

    private void updateAdapter(User user) {
        adapter.setValue(new AppointmentsAdapter(new ArrayList<>(user.getAppointments().values()), this));
    }

    public void cancelAppointment(Appointment appointment) {
        AppointmentsUtils.cancel(appointment);
    }

    @Override
    public void onDirectionsClicked(Appointment appointment) {
        directionsAppointment.setValue(appointment);
    }

    @Override
    public void onCancelClicked(Appointment appointment) {
        appointmentToCancel.setValue(appointment);
    }
}
