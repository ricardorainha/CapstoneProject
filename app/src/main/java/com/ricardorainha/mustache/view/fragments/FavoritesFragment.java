package com.ricardorainha.mustache.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.FragmentFavoritesBinding;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.view.BarbershopDetailsActivity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoritesViewModel viewModel;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false);
        viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);

        configureFields();
        configureObservables();

        return binding.getRoot();
    }

    private void configureFields() {
        if (binding.clFavoritesLandscape == null) {
            binding.rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else {
            binding.rvFavorites.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        binding.rvFavorites.setHasFixedSize(true);
    }

    private void configureObservables() {
        viewModel.getAdapter().observe(this, adapter -> {
            binding.rvFavorites.setAdapter(adapter);
            binding.rvFavorites.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            binding.tvNoFavorites.setVisibility(adapter.getItemCount() > 0 ? View.GONE: View.VISIBLE);
        });

        viewModel.getSelectedBarbershop().observe(this, barbershop -> {
            if (barbershop != null) {
                showDetails(barbershop);
            }
        });
    }

    private void showDetails(Barbershop barbershop) {
        Intent detailsIntent = new Intent(this.getContext(), BarbershopDetailsActivity.class);
        detailsIntent.putExtra(BarbershopDetailsActivity.BARBERSHOP_KEY, barbershop);
        startActivity(detailsIntent);

        viewModel.getSelectedBarbershop().setValue(null);
    }

}
