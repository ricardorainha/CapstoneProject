package com.ricardorainha.mustache.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.FragmentMyScheduleBinding;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.LocationInfo;
import com.ricardorainha.mustache.view.BarbershopDetailsActivity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MyScheduleFragment extends Fragment {

    private FragmentMyScheduleBinding binding;
    private MyScheduleViewModel viewModel;

    public MyScheduleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_schedule, container, false);
        viewModel = ViewModelProviders.of(this).get(MyScheduleViewModel.class);
        binding.setViewModel(viewModel);

        configureFields();
        configureObservables();

        return binding.getRoot();
    }

    private void configureFields() {
        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAppointments.setHasFixedSize(true);
    }

    private void configureObservables() {
        viewModel.getAdapter().observe(this, adapter -> binding.rvAppointments.setAdapter(adapter));
        viewModel.getDirectionsAppointment().observe(this, appointment -> {
            if (appointment != null) {
                startActivity(LocationInfo.getInstance().getDirectionsIntent(viewModel.getDirectionsAppointment().getValue().getBarbershop()));
            }
        });
        viewModel.getAppointmentToCancel().observe(this, appointment -> {
            if (appointment != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.cancelation_dialog_title));
                builder.setMessage(getString(R.string.cancelation_dialog_message, appointment.getBarbershop().getName()));
                builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
                builder.setPositiveButton(R.string.yes, (dialog, which) -> viewModel.cancelAppointment(appointment));
                builder.create().show();
            }
        });
        viewModel.getBarbershopDetails().observe(this, barbershop -> {
            if (barbershop != null) {
                showDetails(barbershop);
            }
        });
    }

    private void showDetails(Barbershop barbershop) {
        Intent detailsIntent = new Intent(this.getContext(), BarbershopDetailsActivity.class);
        detailsIntent.putExtra(BarbershopDetailsActivity.BARBERSHOP_KEY, barbershop);
        startActivity(detailsIntent);
    }

}
