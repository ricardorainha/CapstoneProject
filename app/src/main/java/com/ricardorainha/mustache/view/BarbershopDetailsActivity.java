package com.ricardorainha.mustache.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.ActivityBarbershopDetailsBinding;
import com.ricardorainha.mustache.model.LocationInfo;
import com.ricardorainha.mustache.utils.FavoritesUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class BarbershopDetailsActivity extends AppCompatActivity {

    public static final String BARBERSHOP_KEY = "barbershop";
    private ActivityBarbershopDetailsBinding binding;
    private BarbershopDetailsViewModel viewModel;
    private MenuItem itemFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_barbershop_details);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarbershopDetailsViewModel.class);
        binding.setViewModel(viewModel);

        configureObservables();

        if (getIntent().hasExtra(BARBERSHOP_KEY)) {
            viewModel.getBarbershop().setValue(getIntent().getParcelableExtra(BARBERSHOP_KEY));
            viewModel.retrieveBarbershopDetails();
        }
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
                    Glide.with(BarbershopDetailsActivity.this).load(barbershop.getPhotoUrl()).into(binding.ivDetailsPhoto);
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
    }

    public void onDirectionsClicked(View view) {
        startActivity(LocationInfo.getInstance().getDirectionsIntent(viewModel.getBarbershop().getValue()));
    }

}
