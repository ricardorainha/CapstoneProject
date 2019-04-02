package com.ricardorainha.mustache.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.databinding.FragmentBarbershopsListBinding;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class BarbershopsListFragment extends Fragment {

    private FragmentBarbershopsListBinding binding;
    private BarbershopsViewModel viewModel;

    public BarbershopsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_barbershops_list, container, false);
        viewModel = ViewModelProviders.of(this.getParentFragment()).get(BarbershopsViewModel.class);

        configureFields();
        configureObservables();

        return binding.getRoot();
    }

    private void configureFields() {
        binding.rvBarbershops.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBarbershops.setHasFixedSize(true);

    }

    private void configureObservables() {
        viewModel.getLoading().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                binding.pbLoading.setVisibility(viewModel.getLoading().get() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getAdapter().observe(this, barbershops -> binding.rvBarbershops.setAdapter(viewModel.getAdapter().getValue()));
    }

}
