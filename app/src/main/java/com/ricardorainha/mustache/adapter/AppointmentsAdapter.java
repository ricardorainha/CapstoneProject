package com.ricardorainha.mustache.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.ScheduleListItemBinding;
import com.ricardorainha.mustache.model.Appointment;
import com.ricardorainha.mustache.model.Barbershop;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder> {

    private ScheduleListItemBinding binding;
    private List<Appointment> appointments;
    private ClickListener listener;

    public AppointmentsAdapter(List<Appointment> appointments, ClickListener listener) {
        Collections.sort(appointments, (a1, a2) -> Long.compare(a2.getTime(), a1.getTime()));
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_item, parent, false);
        AppointmentsViewHolder viewHolder = new AppointmentsViewHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsViewHolder holder, int position) {
        holder.bind(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        if (appointments != null) {
            return appointments.size();
        }

        return 0;
    }

    class AppointmentsViewHolder extends RecyclerView.ViewHolder {

        ScheduleListItemBinding binding;
        boolean expand = false;

        public AppointmentsViewHolder(@NonNull ScheduleListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Appointment appointment) {
            binding.setAppointment(appointment);

            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(appointment.getTime());
            DateFormat timeFormat = DateFormat.getTimeInstance(java.text.DateFormat.SHORT);

            binding.tvMonth.setText(date.getDisplayName(Calendar.MONTH, Calendar.SHORT_FORMAT, Locale.getDefault()).toUpperCase());
            binding.tvDay.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
            binding.tvTime.setText(timeFormat.format(date.getTime()));
            binding.clScheduleMain.setOnClickListener(v -> {
                expand = !expand;
                binding.ivExpand.setImageDrawable(binding.getRoot().getContext().getDrawable(
                        expand ? R.drawable.ic_expand_less_black_36 :
                                R.drawable.ic_expand_more_black_36));
                binding.clDetails.setVisibility(expand ? View.VISIBLE : View.GONE);
            });

            binding.ivInfo.setOnClickListener(v -> listener.onInfoClicked(appointment.getBarbershop()));
            binding.ivDirections.setOnClickListener(v -> listener.onDirectionsClicked(appointment));
            binding.ivDelete.setOnClickListener(v -> listener.onCancelClicked(appointment));

            if (appointment.hasAlreadyPassed()) {
                binding.tvMonth.setPaintFlags(binding.tvMonth.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvDay.setPaintFlags(binding.tvDay.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvBarbershopName.setPaintFlags(binding.tvBarbershopName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvTime.setPaintFlags(binding.tvTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            binding.executePendingBindings();
        }
    }

    public interface ClickListener {
        void onInfoClicked(Barbershop barbershop);
        void onDirectionsClicked(Appointment appointment);
        void onCancelClicked(Appointment appointment);
    }

}
