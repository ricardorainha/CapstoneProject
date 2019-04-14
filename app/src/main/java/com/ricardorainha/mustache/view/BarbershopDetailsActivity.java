package com.ricardorainha.mustache.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.ActivityBarbershopDetailsBinding;
import com.ricardorainha.mustache.model.LocationInfo;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.receivers.ConnectionChangeReceiver;
import com.ricardorainha.mustache.utils.AppointmentsUtils;
import com.ricardorainha.mustache.utils.FavoritesUtils;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class BarbershopDetailsActivity extends AppCompatActivity {

    public static final String BARBERSHOP_KEY = "barbershop";
    private ActivityBarbershopDetailsBinding binding;
    private BarbershopDetailsViewModel viewModel;
    private MenuItem itemFavorite;
    private ConnectionChangeReceiver connectionChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_barbershop_details);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarbershopDetailsViewModel.class);
        binding.setViewModel(viewModel);

        connectionChangeReceiver = new ConnectionChangeReceiver();

        configureObservables();

        if (getIntent().hasExtra(BARBERSHOP_KEY)) {
            viewModel.getBarbershop().setValue(getIntent().getParcelableExtra(BARBERSHOP_KEY));
            if (Session.getInstance().getOnlineStatus().getValue()) {
                viewModel.retrieveBarbershopDetails();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerConnectionReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterConnectionReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        this.itemFavorite = menu.findItem(R.id.action_toggle_favorite);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_favorite:
                toggleFavorite();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenuIcon(boolean isFavorite) {
        if (itemFavorite != null) {
            itemFavorite.setIcon(isFavorite ? R.drawable.ic_favorite_white_24 : R.drawable.ic_favorite_border_white_24);
        }
    }

    private void toggleFavorite() {
        boolean isFavorite = FavoritesUtils.isFavorite(viewModel.getBarbershop().getValue());

        if (isFavorite) {
            FavoritesUtils.removeFavorite(viewModel.getBarbershop().getValue().getPlaceId());
        }
        else {
            FavoritesUtils.addFavorite(viewModel.getBarbershop().getValue());
        }

        updateMenuIcon(!isFavorite);
    }


    private void configureObservables() {
        viewModel.getBarbershop().observe(this, barbershop -> {
            if (binding.ivDetailsPhoto.getDrawable() == null) {
                if (barbershop.getPhotos().size() > 0) {
                    Glide.with(BarbershopDetailsActivity.this).load(barbershop.getPhotoUrl()).placeholder(R.drawable.mustache_black_background).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.ivDetailsPhoto);
                }
                else {
                    Glide.with(BarbershopDetailsActivity.this).load(R.drawable.mustache_black_background).into(binding.ivDetailsPhoto);
                }
            }
            binding.ivPhone.setVisibility(TextUtils.isEmpty(barbershop.getFormattedPhoneNumber()) ? View.GONE : View.VISIBLE);
            binding.tvDetailsPhone.setVisibility(TextUtils.isEmpty(barbershop.getFormattedPhoneNumber()) ? View.GONE : View.VISIBLE);

            binding.ivWebsite.setVisibility(TextUtils.isEmpty(barbershop.getWebsite()) ? View.GONE : View.VISIBLE);
            binding.tvDetailsWebsite.setVisibility(TextUtils.isEmpty(barbershop.getWebsite()) ? View.GONE : View.VISIBLE);

            boolean hasOpeningHours = (barbershop.getOpeningHours() != null) && (!barbershop.getOpeningHours().getWeekdayText().isEmpty());
            binding.ivOpenHours.setVisibility(hasOpeningHours ? View.VISIBLE : View.GONE);
            binding.tvDetailsOpenHours.setVisibility(hasOpeningHours ? View.VISIBLE : View.GONE);

            if (hasOpeningHours) {
                for (int i = 0; i < barbershop.getOpeningHours().getWeekdayText().size(); i++) {
                    String hour = barbershop.getOpeningHours().getWeekdayText().get(i).substring(0,1).toUpperCase() + barbershop.getOpeningHours().getWeekdayText().get(i).substring(1);
                    barbershop.getOpeningHours().getWeekdayText().set(i, hour);
                }
                String openingHoursText = TextUtils.join("\n", barbershop.getOpeningHours().getWeekdayText());
                binding.tvDetailsOpenHours.setText(openingHoursText);
            }

            updateMenuIcon(FavoritesUtils.isFavorite(barbershop));
        });

        viewModel.getLoading().observe(this, loading -> {
            binding.flPanel.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        Session.getInstance().getOnlineStatus().observe(this, connected -> {
            binding.fabSchedule.setEnabled(connected);
            binding.fabSchedule.setBackgroundTintList(ColorStateList.valueOf(getColor(connected ? R.color.colorAccent : android.R.color.darker_gray)));
        });
    }

    public void onScheduleClicked(View view) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dateDialog = new DatePickerDialog(this, (datePickerView, year, month, dayOfMonth) -> {
            TimePickerDialog timeDialog = new TimePickerDialog(this, (timePickerView, hourOfDay, minute) -> {
                showScheduleConfirmationDialog(year, month, dayOfMonth, hourOfDay, minute);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(this));
            timeDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dateDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dateDialog.show();
    }

    public void onDirectionsClicked(View view) {
        startActivity(LocationInfo.getInstance().getDirectionsIntent(viewModel.getBarbershop().getValue()));
    }

    public void onAddressClicked(View view) {
        Intent openAddressIntent = new Intent();
        openAddressIntent.setAction(Intent.ACTION_VIEW);
        openAddressIntent.setData(Uri.parse(viewModel.getBarbershop().getValue().getUrl()));

        startActivity(openAddressIntent);
    }

    public void onPhoneClicked(View view) {
        Intent openDialerIntent = new Intent();
        openDialerIntent.setAction(Intent.ACTION_DIAL);
        openDialerIntent.setData(Uri.parse("tel:" + viewModel.getBarbershop().getValue().getFormattedPhoneNumber()));

        startActivity(openDialerIntent);
    }

    public void onWebsiteClicked(View view) {
        Intent openWebsiteIntent = new Intent();
        openWebsiteIntent.setAction(Intent.ACTION_VIEW);
        openWebsiteIntent.setData(Uri.parse(viewModel.getBarbershop().getValue().getWebsite()));

        startActivity(openWebsiteIntent);
    }

    private void showScheduleConfirmationDialog(int year, int month, int day, int hour, int minute) {
        Calendar scheduleDate = Calendar.getInstance();
        scheduleDate.set(year, month, day, hour, minute, 0);
        DateFormat dateFormat = DateFormat.getDateInstance(java.text.DateFormat.FULL);
        DateFormat timeFormat = DateFormat.getTimeInstance(java.text.DateFormat.SHORT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirmation_dialog_title));
        builder.setMessage(getString(R.string.confirmation_dialog_message, dateFormat.format(scheduleDate.getTime()), timeFormat.format(scheduleDate.getTime())));
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            viewModel.schedule(scheduleDate);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getString(R.string.confirmation_dialog_title));
            alertBuilder.setMessage(getString(R.string.confirmation_scheduled_dialog_message, viewModel.getBarbershop().getValue().getName()));
            alertBuilder.setPositiveButton(android.R.string.ok, (dialog2, which2) -> {
                dialog2.dismiss();

                // schedule notification to be thrown after 1 minute to simulate barbershop response
                AppointmentsUtils.scheduleConfirmationNotification(this, viewModel.getBarbershop().getValue(), scheduleDate);
            });
            alertBuilder.create().show();
        });
        builder.create().show();
    }

    private void registerConnectionReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionChangeReceiver, intentFilter);
    }

    private void unregisterConnectionReceiver() {
        unregisterReceiver(connectionChangeReceiver);
    }

}
